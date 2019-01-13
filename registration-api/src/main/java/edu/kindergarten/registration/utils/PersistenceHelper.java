package edu.kindergarten.registration.utils;

import edu.kindergarten.registration.persistence.controllers.KidController;
import edu.kindergarten.registration.persistence.controllers.RoleController;
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

    public void register() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("test");
        String salt = PasswordUtils.getSalt(30);
        userEntity.setPassword(PasswordUtils.generateSecurePassword("ad9715!", salt));
        userEntity.setSalt(salt);
        ProfileEntity father = new ProfileEntity(); father.setName("test");
        ProfileEntity kid = new ProfileEntity(); kid.setName("test");
        userEntity.setProfileid(father);
        //em.persist(father);
        //registrationRequest.getUserEntity().setProfileid(registrationRequest.getFatherProfile());
        KidprofileEntity kidprofileEntity = new KidprofileEntity();
        kidprofileEntity.setFatherprofileid(father);
        kidprofileEntity.setMotherprofileid(father);
        kidprofileEntity.setGurdianprofileid(father);
        kidprofileEntity.setKidprofileid(kid);

        /*UserKidEntity userKidEntity = new UserKidEntity();
        userKidEntity.setUserid(userEntity); userKidEntity.setKidid(kidprofileEntity);*/

        userEntity.getUserkids().add(kidprofileEntity);

        RoleEntity role = roleController.findAll().stream().filter(att -> Role.TEACHER.toString().equalsIgnoreCase(att.getRole())).findFirst().orElse(null);
        userEntity.getRoles().add(role);

        //kidController.createKid(kidprofileEntity);
        userController.createUser(userEntity);
    }

    public void register(RegistrationRequest registrationRequest) {
        UserEntity user = registrationRequest.getUser();
        String password = user.getPassword();
        String salt = PasswordUtils.getSalt(30);
        user.setPassword(PasswordUtils.generateSecurePassword(password, salt));
        user.setSalt(salt);
        registrationRequest.getUser().getAddress().setUser(user);
        if (registrationRequest.isMother()) {
            user.setProfileid(registrationRequest.getKidprofiles().stream().findAny().orElse(null).getMotherprofileid());
        } else {
            user.setProfileid(registrationRequest.getKidprofiles().stream().findAny().orElse(null).getFatherprofileid());
        }
        user.getUserkids().addAll(registrationRequest.getKidprofiles());
        RoleEntity role = roleController.findAll().stream().filter(att -> registrationRequest.getRole().toString().equalsIgnoreCase(att.getRole())).findFirst().orElse(null);
        user.getRoles().add(role);
        userController.createUser(user);
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
