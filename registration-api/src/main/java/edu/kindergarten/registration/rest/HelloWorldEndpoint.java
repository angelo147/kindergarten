package edu.kindergarten.registration.rest;

import edu.kindergarten.registration.persistence.controllers.TokenController;
import edu.kindergarten.registration.messaging.Email;
import edu.kindergarten.registration.messaging.MessageService;
import edu.kindergarten.registration.persistence.controllers.UserController;
import edu.kindergarten.registration.persistence.model.KidprofileEntity;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequestScoped
@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
public class HelloWorldEndpoint {
    @Inject
    @Email
    private MessageService email;
    @Inject
    private TokenController controller;
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

    /*@Parameter(
            description = "The host for whom to retrieve the JVM system properties for.",
            required = true,
            example = "foo",
            schema = @Schema(type = SchemaType.STRING))*/

    @POST
    @Path("/register")
    /*@APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "404",
                            description = "Missing description",
                            content = @Content(mediaType = "text/plain")),
                    @APIResponse(
                            responseCode = "200",
                            description = "JVM system properties of a particular host.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = edu.kindergarten.registration.rest.Response.class))) })
    @Operation(
            summary = "Get JVM system properties for particular host",
            description = "Retrieves and returns the JVM system properties from the system "
                    + "service running on the particular host.")*/
    public Response register(RegistrationRequest regRequest) {
        persistenceHelper.register(regRequest);
        return Response.ok("OK").type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    @GET
    @Path("/user")
    public Response getUser() {
        return Response.ok(userController.findById(9)).type(MediaType.APPLICATION_JSON_TYPE).build();
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
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @POST
    @Path("/auth/jwt/{refreshToken}")
    public Response newJwt(@HeaderParam("Authorization") String jwt, @PathParam("refreshToken") String refreshToken) {
        jwt = jwt.replaceFirst("Bearer" + " ", "");
        UserInfo userInfo = Optional.ofNullable(cacheUtil.getRefreshTokens().getIfPresent(UUID.fromString(refreshToken))).orElse(new UserInfo());
        JwtResponse jwtResponse = jwtUtil.verifyRefreshToken(jwt, refreshToken, userInfo) ? jwtUtil.generateJWT(userInfo.getUserId(), userInfo.getRole(), refreshToken) : null;
        return jwtResponse!=null ? Response.ok(jwtResponse).type(MediaType.APPLICATION_JSON_TYPE).build()
        : Response.ok("Fuck you!").type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    @GET
    @Path("/token")
    public Response generateToken() {
        return Response.ok(tokenUtil.generateToken("dimoange@gmail.com")).type(MediaType.APPLICATION_JSON_TYPE).build();
    }

    @GET
    @Path("/document/{userId}/{id}")
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
    @Path("/document/{userId}/{profileid}")
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
                return Response.status(200).entity("Uploaded file name : " + fileName)
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
