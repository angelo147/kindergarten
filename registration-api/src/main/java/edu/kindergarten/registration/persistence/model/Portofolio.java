package edu.kindergarten.registration.persistence.model;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "portofolio", schema = "kindergarten", catalog = "")
public class Portofolio {
    private int portofolioid;
    private KidprofileEntity kidprofileEntity;
    private List<Activity> activities;

    @Id
    @Column(name = "portofolioid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getPortofolioid() {
        return portofolioid;
    }

    public void setPortofolioid(int portofolioid) {
        this.portofolioid = portofolioid;
    }

    @OneToOne
    @JoinColumn(name = "kidprofileid", referencedColumnName = "id")
    public KidprofileEntity getKidprofileEntity() {
        return kidprofileEntity;
    }

    public void setKidprofileEntity(KidprofileEntity kidprofileEntity) {
        this.kidprofileEntity = kidprofileEntity;
    }

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "portofolio_activity", joinColumns = @JoinColumn(name = "portofolioid"), inverseJoinColumns = @JoinColumn(name = "activityid"))
    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }
}
