package edu.kindergarten.registration.rest;

import edu.kindergarten.registration.persistence.controllers.*;
import edu.kindergarten.registration.messaging.Email;
import edu.kindergarten.registration.messaging.MessageService;
import edu.kindergarten.registration.persistence.model.*;
import edu.kindergarten.registration.rest.requests.LoginRequest;
import edu.kindergarten.registration.rest.requests.RegistrationRequest;
import edu.kindergarten.registration.security.JWTUtil;
import edu.kindergarten.registration.security.TokenCacheUtil;
import edu.kindergarten.registration.utils.PasswordUtils;
import edu.kindergarten.registration.utils.PersistenceHelper;
import edu.kindergarten.registration.utils.TokenUtil;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@RequestScoped
@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
public class KinderGartenEndpoint {
    @Inject
    @Email
    private MessageService email;
    @Inject
    private KidController kidController;
    @Inject
    private TokenUtil tokenUtil;
    @Inject
    private JWTUtil jwtUtil;
    @Inject
    private TokenCacheUtil cacheUtil;
    @Inject
    private PersistenceHelper persistenceHelper;
    @Inject
    private UserController userController;
    @Inject
    private CalendarEventRepo eventRepo;
    @Inject
    private PortofolioController portofolioController;
    @Inject
    private ActivityController activityController;
    @Inject
    private CategoryController categoryController;
    @Inject
    private GoalController goalController;
    @Inject
    private CommentsController commentsController;

    /**
     * Registration process for parent and children.
     * @param regRequest RegistrationRequest has all mandatory information for the system.
     * @return Returns OK if is successful.
     */
    @POST
    @Path("/register")
    public Response register(RegistrationRequest regRequest) {
        persistenceHelper.register(regRequest);
        return Response.ok(new edu.kindergarten.registration.rest.Response(ResponseCode.OK)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    /**
     * Assign kids to a teacher.
     * @param ids List of KidProfile ids.
     * @param userid Teacher id.
     * @return Returns OK if is successful.
     */
    @POST
    @Path("/kid/assign/{userid}")
    @RolesAllowed({"SUPERVISOR"})
    @ValidateUser
    public Response assignkid(List<Integer> ids, @PathParam("userid") int userid) {
        List<KidprofileEntity> kids = ids.parallelStream().map(id->kidController.findById(id)).collect(Collectors.toList());
        UserEntity user = userController.findById(userid);
        user.getUserkids().addAll(kids);
        userController.createUser(user);
        return Response.ok(new edu.kindergarten.registration.rest.Response(ResponseCode.OK)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    /**
     * Update user data. eg password.
     * Supervisor can update any users data.
     * @param user User data.
     * @return Returns the updated user.
     */
    @PUT
    @Path("/user")
    @RolesAllowed({"PARENT", "TEACHER", "SUPERVISOR"})
    @ValidateUser
    public Response updateUser(UserEntity user) {
        return Response.ok(userController.update(user)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    /**
     * Update profile data.
     * Supervisor can update any profile data.
     * @param profile Profile user data.
     * @return Returns OK if is successful.
     */
    @PUT
    @Path("/user/profile")
    @RolesAllowed({"PARENT", "TEACHER", "SUPERVISOR"})
    @ValidateUser
    public Response updateProfile(ProfileEntity profile) {
        userController.updateProfile(profile);
        return Response.ok(new edu.kindergarten.registration.rest.Response(ResponseCode.OK)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    /**
     * Send email with attachment to multiple users(parents).
     * @param emails List of user emails.
     * @param type Name of the attachment.
     * @return Returns OK if is successful.
     */
    @POST
    @Path("/send/{type}")
    @RolesAllowed({"TEACHER", "SUPERVISOR"})
    @ValidateUser
    public Response sendMessage(List<String> emails, @PathParam("type") String type) {
        email.sendMessage(emails, type);
        return Response.ok(new edu.kindergarten.registration.rest.Response(ResponseCode.OK)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    /**
     * Create new user.
     * Used only for supervisor to create teachers.
     * @param regRequest Mandatory data of a user to be registered at the system.
     * @return Returns created user.
     */
    @POST
    @Path("/user")
    @RolesAllowed({"SUPERVISOR"})
    @ValidateUser
    public Response createUser(RegistrationRequest regRequest) {
        return Response.ok(persistenceHelper.addUser(regRequest)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    /**
     * Get all system users info.
     * @return Returns all users of the system.
     */
    @GET
    @Path("/user/all")
    @RolesAllowed({"SUPERVISOR"})
    @ValidateUser
    public Response getUsers() {
        return Response.ok(new ResponseWrapper(ResponseCode.OK, userController.findAll(), null, null)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    /**
     * Get specific user info by userid.
     * @param userid Specific user id.
     * @return Returns the user if found.
     */
    @GET
    @Path("/user/{userid}")
    @RolesAllowed({"PARENT", "TEACHER", "SUPERVISOR"})
    @ValidateUser
    public Response getUser(@PathParam("userid") int userid) {
        return Response.ok(userController.findById(userid)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    /**
     * Create new Calendar Event. Parent can arrange a meeting with a teacher for registration or teacher can call parents for a meeting.
     * @param calendarEventEntity Calendar Event attributes.
     * @return Returns OK if is successful.
     */
    @POST
    @Path("/event")
    @RolesAllowed({"PARENT", "TEACHER", "SUPERVISOR"})
    @ValidateUser
    public Response newEvent(CalendarEventEntity calendarEventEntity) {
        eventRepo.saveEvent(calendarEventEntity);
        return Response.ok(new edu.kindergarten.registration.rest.Response(ResponseCode.OK)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    /**
     * Get events for a specific user.
     * @param userid Specific user id.
     * @return Returns all user events.
     */
    @GET
    @Path("/event/{userid}")
    @RolesAllowed({"PARENT", "TEACHER", "SUPERVISOR"})
    @ValidateUser
    public Response getUserEvent(@PathParam("userid") int userid) {
        return Response.ok(new ResponseWrapper(ResponseCode.OK, null, eventRepo.findByUserId(userid), null)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    /**
     * Login to the system.
     * @param login Username and password.
     * @return Logged in user with authentication token.
     */
    @POST
    @Path("/login")
    public Response login(LoginRequest login) {
        UserEntity user = userController.findActiveByUsername(login.getUsername());
        boolean passwordMatch = PasswordUtils.verifyUserPassword(login.getPassword(), user.getPassword(), user.getSalt());
        if(passwordMatch) {
            List<Role> userroles = user.getRoles().stream().map(role -> Role.valueOf(role.getRole())).collect(Collectors.toList());
            JwtResponse resp = jwtUtil.generateJWT((long) user.getUserid(), userroles, null);
            resp.setUser(user);
            return Response.ok(resp).type(MediaType.APPLICATION_JSON_TYPE).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).entity(new edu.kindergarten.registration.rest.Response(ResponseCode.LOGINFAILED)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    @POST
    @Path("/auth/jwt/{refreshToken}")
    public Response newJwt(@HeaderParam("Authorization") String jwt, @PathParam("refreshToken") String refreshToken) {
        jwt = jwt.replaceFirst("Bearer" + " ", "");
        UserInfo userInfo = Optional.ofNullable(cacheUtil.getRefreshTokens().getIfPresent(UUID.fromString(refreshToken))).orElse(new UserInfo());
        JwtResponse jwtResponse = jwtUtil.verifyRefreshToken(jwt, refreshToken, userInfo) ? jwtUtil.generateJWT(userInfo.getUserId(), userInfo.getRole(), refreshToken) : null;
        return jwtResponse!=null ? Response.ok(jwtResponse).type(MediaType.APPLICATION_JSON_TYPE).build()
        : Response.status(Response.Status.UNAUTHORIZED).entity(new edu.kindergarten.registration.rest.Response(ResponseCode.LOGINFAILED)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    @GET
    @Path("/token/{userid}")
    public Response generateToken(@PathParam("userid") int userid) {
        return Response.ok(tokenUtil.generateToken(userController.findById(userid).getProfileid().getEmail(), userid)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    @GET
    @Path("/validate/token/{token}")
    public Response validateToken(@PathParam("token")String token) {
        return Response.ok(tokenUtil.validateToken(token)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    @GET
    @Path("/document/{id}")
    @RolesAllowed({"PARENT", "TEACHER", "SUPERVISOR"})
    @ValidateUser
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFile(@PathParam("id") int id) {
        File file = null;
        try {
            file = persistenceHelper.getDocument(id);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        Response.ResponseBuilder response = Response.ok(file);
        //response.header("Content-Disposition", "attachment;filename=classes.jar");
        return response.build();
    }

    @GET
    @Path("/category/all")
    @RolesAllowed({"TEACHER", "SUPERVISOR"})
    @ValidateUser
    public Response getAllCategories() {
        return Response.ok(new ResponseWrapper(ResponseCode.OK, null, null,categoryController.findAll())).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    /**
     * Add comment at a kid's activity.
     * @param activityComment Activity comment attributes.
     * @return Returns OK if is successful.
     */
    @POST
    @Path("/comment")
    @RolesAllowed({"TEACHER", "SUPERVISOR"})
    @ValidateUser
    public Response addComments(ActivityComment activityComment) {
        Activity activity = activityController.findById(activityComment.getActivity().getActivityid());
        Portofolio portofolio = portofolioController.findById(activityComment.getPortofolio().getPortofolioid());
        activityComment.setActivity(activity);
        activityComment.setPortofolio(portofolio);
        commentsController.createComment(activityComment);
        return Response.ok(new edu.kindergarten.registration.rest.Response(ResponseCode.OK)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    /**
     * Get a comment of a specific activity of a portofolio.
     * @param portofolioid Portofolio id.
     * @param activityid Activity id.
     * @return Returns activity.
     */
    @GET
    @Path("/comment/{portofolioid}/{activityid}")
    @RolesAllowed({"TEACHER", "SUPERVISOR"})
    @ValidateUser
    public Response addComments(@PathParam("portofolioid") int portofolioid, @PathParam("activityid") int activityid) {
        ActivityComment comment = commentsController.findByportofolioandactivity(portofolioid, activityid);
        comment.setPortofolio(null);
        comment.setActivity(null);
        return Response.ok(comment).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    /**
     * Update comment.
     * @param activityComment Activity comment attributes.
     * @return Returns updated comment.
     */
    @PUT
    @Path("/comment")
    @RolesAllowed({"TEACHER", "SUPERVISOR"})
    @ValidateUser
    public Response updateComments(ActivityComment activityComment) {
        ActivityComment comment = commentsController.findById(activityComment.getId());
        comment.setComments(activityComment.getComments());
        commentsController.createComment(comment);
        comment.setPortofolio(null);
        comment.setActivity(null);
        return Response.ok(comment).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    /**
     * Generate a new portofolio id for a kid.
     * @param kidid Kid profile id.
     * @return Returns created portofolio.
     */
    @GET
    @Path("/portofolio/{kidid}")
    @RolesAllowed({"TEACHER", "SUPERVISOR"})
    @ValidateUser
    public Response getNewPortofolio(@PathParam("kidid") int kidid) {
        KidprofileEntity kid = kidController.findById(kidid);
        Portofolio portofolio = new Portofolio();
        portofolio.setKidprofileEntity(kid);
        return Response.ok(portofolioController.createPortofolio(portofolio)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    /**
     * Create a new activity.
     * @param activity Activity attributes.
     * @return Returnes created activity.
     */
    @POST
    @Path("/activity")
    @RolesAllowed({"TEACHER", "SUPERVISOR"})
    @ValidateUser
    public Response getNewActivity(Activity activity) {
        List<Category> categories = activity.getCategories().stream().map(Category::getCategoryid).map(id -> categoryController.findById(id)).collect(Collectors.toList());
        List<Goal> goals = activity.getGoals().stream().map(Goal::getGoalid).map(id -> goalController.findById(id)).collect(Collectors.toList());
        activity.setCategories(categories);
        activity.setGoals(goals);
        Activity activity1 = activityController.createActivity(activity);
        activity1.getCategories().forEach(category->category.setGoals(null));
        return Response.ok(activity1).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    /**
     * Set activites to a specific portofolio.
     * @param portofolioid Portofolio id.
     * @param activities List of Activity objects with ids.
     * @return Returns OK if is successful.
     */
    @POST
    @Path("/portofolio/{portofolioid}/setactivites")
    @RolesAllowed({"TEACHER", "SUPERVISOR"})
    @ValidateUser
    public Response setPortofolioActivities(@PathParam("portofolioid") int portofolioid, List<Activity> activities) {
        Portofolio portofolio = portofolioController.findById(portofolioid);
        List<Activity> collection = activities.stream().map(Activity::getActivityid).map(id -> activityController.findById(id)).collect(Collectors.toList());
        portofolio.setActivities(collection);
        portofolioController.createPortofolio(portofolio);
        return Response.ok(new edu.kindergarten.registration.rest.Response(ResponseCode.OK)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    /**
     * Upload files-documents. For example parent can upload required registration documents.
     * @param input File.
     * @param profileid Profile id.
     * @return Returns OK if is successful.
     */
    @POST
    @Path("/document/{profileid}")
    @RolesAllowed({"PARENT", "SUPERVISOR"})
    @ValidateUser
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(MultipartFormDataInput input, @PathParam("profileid") int profileid) {
        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        // Get file data to save
        List<InputPart> inputParts = uploadForm.get("attachment");
        for (InputPart inputPart : inputParts) {
            try {
                MultivaluedMap<String, String> header = inputPart.getHeaders();
                String fileName = getFileName(header);
                // convert the uploaded file to inputstream
                InputStream inputStream = inputPart.getBody(InputStream.class,
                        null);
                byte[] bytes = IOUtils.toByteArray(inputStream);
                persistenceHelper.updateDocument(profileid, fileName, bytes);
                return Response.status(200).entity(new edu.kindergarten.registration.rest.Response(ResponseCode.OK))
                        .build();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String getFileName(MultivaluedMap<String, String> header) {

        String[] contentDisposition = header.getFirst("Content-Disposition").split(";");

        for (String filename : contentDisposition) {
            if ((filename.trim().startsWith("filename"))) {

                String[] name = filename.split("=");

                return name[1].trim().replaceAll("\"", "");
            }
        }
        return "unknown";
    }

}
