package edu.kindergarten.registration.rest;

import edu.kindergarten.registration.persistence.controllers.CalendarEventRepo;
import edu.kindergarten.registration.persistence.controllers.KidController;
import edu.kindergarten.registration.persistence.controllers.TokenController;
import edu.kindergarten.registration.messaging.Email;
import edu.kindergarten.registration.messaging.MessageService;
import edu.kindergarten.registration.persistence.controllers.UserController;
import edu.kindergarten.registration.persistence.model.CalendarEventEntity;
import edu.kindergarten.registration.persistence.model.KidprofileEntity;
import edu.kindergarten.registration.persistence.model.ProfileEntity;
import edu.kindergarten.registration.persistence.model.UserEntity;
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

    @POST
    @Path("/register")
    public Response register(RegistrationRequest regRequest) {
        persistenceHelper.register(regRequest);
        return Response.ok(new edu.kindergarten.registration.rest.Response(ResponseCode.OK)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    @POST
    @Path("/kid/assign/{userid}")
    public Response assignkid(List<Integer> ids, @PathParam("userid") int userid) {
        List<KidprofileEntity> kids = ids.parallelStream().map(id->kidController.findById(id)).collect(Collectors.toList());
        UserEntity user = userController.findById(userid);
        user.getUserkids().addAll(kids);
        userController.createUser(user);
        return Response.ok(new edu.kindergarten.registration.rest.Response(ResponseCode.OK)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    @PUT
    @Path("/user")
    public Response updateUser(UserEntity user) {
        return Response.ok(userController.update(user)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    @PUT
    @Path("/user/profile")
    public Response updateProfile(ProfileEntity profile) {
        userController.updateProfile(profile);
        return Response.ok(new edu.kindergarten.registration.rest.Response(ResponseCode.OK)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    @POST
    @Path("/send/{type}")
    @RolesAllowed({"TEACHER", "SUPERVISOR"})
    @ValidateUser
    public Response sendMessage(List<String> emails, @PathParam("type") String type) {
        email.sendMessage(emails, type);
        return Response.ok(new edu.kindergarten.registration.rest.Response(ResponseCode.OK)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    @GET
    @Path("/user/all")
    @RolesAllowed({"SUPERVISOR"})
    @ValidateUser
    public Response getUsers() {
        return Response.ok(userController.findAll()).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    @GET
    @Path("/user/{userid}")
    @RolesAllowed({"PARENT", "TEACHER", "SUPERVISOR"})
    @ValidateUser
    public Response getUser(@PathParam("userid") int userid) {
        return Response.ok(userController.findById(userid)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    @POST
    @Path("/event")
    @RolesAllowed({"PARENT", "TEACHER", "SUPERVISOR"})
    @ValidateUser
    public Response newEvent(CalendarEventEntity calendarEventEntity) {
        eventRepo.saveEvent(calendarEventEntity);
        return Response.ok(new edu.kindergarten.registration.rest.Response(ResponseCode.OK)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    @GET
    @Path("/event/{userid}")
    @RolesAllowed({"PARENT", "TEACHER", "SUPERVISOR"})
    @ValidateUser
    public Response getUserEvent(@PathParam("userid") int userid) {
        return Response.ok(eventRepo.findByUserId(userid)).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

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

    @POST
    @Path("/document/{profileid}")
    @RolesAllowed({"PARENT", "SUPERVISOR"})
    @ValidateUser
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(MultipartFormDataInput input, @PathParam("profileid") int profileid) throws IOException {
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

                String finalFileName = name[1].trim().replaceAll("\"", "");
                return finalFileName;
            }
        }
        return "unknown";
    }

}
