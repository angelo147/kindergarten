package edu.kindergarten.registration.rest.requests;

import edu.kindergarten.registration.persistence.model.KidprofileEntity;
import edu.kindergarten.registration.persistence.model.UserEntity;
import edu.kindergarten.registration.rest.Role;

import java.util.List;

public class RegistrationRequest {
    private UserEntity user;
    private List<KidprofileEntity> kidprofiles;
    private PrimaryProfile primaryProfile;
    private Role role;

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public List<KidprofileEntity> getKidprofiles() {
        return kidprofiles;
    }

    public void setKidprofiles(List<KidprofileEntity> kidprofiles) {
        this.kidprofiles = kidprofiles;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public PrimaryProfile getPrimaryProfile() {
        return primaryProfile;
    }

    public void setPrimaryProfile(PrimaryProfile primaryProfile) {
        this.primaryProfile = primaryProfile;
    }

    public enum PrimaryProfile
    {
        FATHER, MOTHER, GURDIAN;
    }
}
