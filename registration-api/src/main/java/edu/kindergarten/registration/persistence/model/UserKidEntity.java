package edu.kindergarten.registration.persistence.model;

import javax.persistence.*;

@Entity
@Table(name = "user_kid", schema = "kindergarten", catalog = "")
public class UserKidEntity {
    private int id;
    private transient UserEntity userid;
    private KidprofileEntity kidid;

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

        UserKidEntity that = (UserKidEntity) o;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @ManyToOne
    @JoinColumn(name = "userid", referencedColumnName = "userid", nullable = false)
    public UserEntity getUserid() {
        return userid;
    }

    public void setUserid(UserEntity userid) {
        this.userid = userid;
    }

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "kidid", referencedColumnName = "id", nullable = false)
    public KidprofileEntity getKidid() {
        return kidid;
    }

    public void setKidid(KidprofileEntity kidid) {
        this.kidid = kidid;
    }
}
