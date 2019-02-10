package edu.kindergarten.registration.persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
@Entity
@Table(name = "activitycomment", schema = "kindergarten", catalog = "")
@NamedQuery(name="ActivityComment.findByportofolioandactivity", query="SELECT ac FROM ActivityComment ac where ac.portofolio.id = :portofolioid and ac.activity.activityid = :activityid")
public class ActivityComment {
    private int id;
    private Portofolio portofolio;
    private Activity activity;
    private String comments;
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @OneToOne
    @JoinColumn(name = "portofolioid", referencedColumnName = "portofolioid")
    public Portofolio getPortofolio() {
        return portofolio;
    }

    public void setPortofolio(Portofolio portofolio) {
        this.portofolio = portofolio;
    }

    @OneToOne
    @JoinColumn(name = "activityid", referencedColumnName = "activityid")
    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
