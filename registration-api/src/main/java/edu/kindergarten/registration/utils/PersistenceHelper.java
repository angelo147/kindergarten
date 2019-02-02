package edu.kindergarten.registration.utils;

import edu.kindergarten.registration.messaging.Email;
import edu.kindergarten.registration.messaging.MessageService;
import edu.kindergarten.registration.persistence.controllers.KidController;
import edu.kindergarten.registration.persistence.controllers.RoleController;
import edu.kindergarten.registration.persistence.controllers.StatusController;
import edu.kindergarten.registration.persistence.controllers.UserController;
import edu.kindergarten.registration.persistence.model.*;
import edu.kindergarten.registration.rest.Role;
import edu.kindergarten.registration.rest.requests.RegistrationRequest;
import org.apache.commons.io.FileUtils;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;

@Stateless
public class PersistenceHelper {
    @Inject
    private UserController userController;
    @Inject
    private KidController kidController;
    @Inject
    private RoleController roleController;
    @Inject
    private StatusController statusController;
    @Inject
    @Email
    private MessageService email;

    public void register(RegistrationRequest registrationRequest) {
        UserEntity user = registrationRequest.getUser();
        String password = user.getPassword();
        String salt = PasswordUtils.getSalt(30);
        user.setPassword(PasswordUtils.generateSecurePassword(password, salt));
        user.setSalt(salt);
        registrationRequest.getUser().getAddress().setUser(user);
        if (registrationRequest.getMother()) {
            user.setProfileid(registrationRequest.getKidprofiles().stream().findAny().orElse(null).getMotherprofileid());
        } else {
            user.setProfileid(registrationRequest.getKidprofiles().stream().findAny().orElse(null).getFatherprofileid());
        }
        user.getUserkids().addAll(registrationRequest.getKidprofiles());
        RoleEntity role = roleController.findAll().stream().filter(att -> registrationRequest.getRole().toString().equalsIgnoreCase(att.getRole())).findFirst().orElse(null);
        StatusEntity status = statusController.findAll().stream().filter(att -> "preactive".equalsIgnoreCase(att.getStatus())).findFirst().orElse(null);
        user.getRoles().add(role);
        user.setStatusid(status);
        email.sendMessage(user.getProfileid().getEmail(), userController.createUser(user).getUserid());
    }

    public void updateDocument(int profileid, String title, byte[] file) {
        KidprofileEntity storedkidprofile = kidController.findById(profileid);
        KidDocument kidDocument = new KidDocument();
        kidDocument .setKidprofileEntity(storedkidprofile);
        kidDocument.setTitle(title);
        Blob blob = null;
        try {
            blob = new javax.sql.rowset.serial.SerialBlob(file);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        kidDocument.setDocumentStream(blob);
        storedkidprofile.getKidDocuments().add(kidDocument);
    }

    public File getDocument(int id) throws IOException, SQLException {
        KidDocument kidDocument = kidController.findDocById(id);
        InputStream inputStream = null;
        inputStream = kidDocument.getDocumentStream().getBinaryStream();
        File file = new File("C:/Users/Aggelos/file.tmp");
        FileUtils.copyInputStreamToFile(inputStream, file);
        return file;
    }
}
