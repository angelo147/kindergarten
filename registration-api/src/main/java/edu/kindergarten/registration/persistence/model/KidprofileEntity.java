package edu.kindergarten.registration.persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "kidprofile", schema = "kindergarten", catalog = "")
public class KidprofileEntity {
    private int id;
    private ProfileEntity fatherprofileid;
    private ProfileEntity motherprofileid;
    private ProfileEntity gurdianprofileid;
    private ProfileEntity kidprofileid;
    private List<KidDocument> kidDocuments;
    @JsonIgnore
    private List<UserEntity> users;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KidprofileEntity that = (KidprofileEntity) o;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @OneToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "fatherprofileid", referencedColumnName = "profileid", nullable = false)
    public ProfileEntity getFatherprofileid() {
        return fatherprofileid;
    }

    public void setFatherprofileid(ProfileEntity fatherprofileid) {
        this.fatherprofileid = fatherprofileid;
    }

    @OneToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "motherprofileid", referencedColumnName = "profileid", nullable = false)
    public ProfileEntity getMotherprofileid() {
        return motherprofileid;
    }

    public void setMotherprofileid(ProfileEntity motherprofileid) {
        this.motherprofileid = motherprofileid;
    }

    @OneToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "gurdianprofileid", referencedColumnName = "profileid", nullable = false)
    public ProfileEntity getGurdianprofileid() {
        return gurdianprofileid;
    }

    public void setGurdianprofileid(ProfileEntity gurdianprofileid) {
        this.gurdianprofileid = gurdianprofileid;
    }

    @OneToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "kidprofileid", referencedColumnName = "profileid", nullable = false)
    public ProfileEntity getKidprofileid() {
        return kidprofileid;
    }

    public void setKidprofileid(ProfileEntity kidprofileid) {
        this.kidprofileid = kidprofileid;
    }

    @OneToMany(mappedBy = "kidprofileEntity", cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.EAGER)
    public List<KidDocument> getKidDocuments() {
        if (kidDocuments == null) {
            kidDocuments = new ArrayList<>();
        }
        return kidDocuments;
    }

    public void setKidDocuments(List<KidDocument> kidDocuments) {
        this.kidDocuments = kidDocuments;
    }

    @ManyToMany(mappedBy = "userkids")
    public List<UserEntity> getUsers() {
        return users;
    }

    public void setUsers(List<UserEntity> users) {
        this.users = users;
    }
}
