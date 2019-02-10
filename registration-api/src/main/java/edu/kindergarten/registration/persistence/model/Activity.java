package edu.kindergarten.registration.persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "activity", schema = "kindergarten", catalog = "")
public class Activity {
    private int activityid;
    private String title;
    private List<Category> categories;
    private String description;
    private Date activityDate;
    private String context;
    //private String comments;
    @JsonIgnore
    private List<Portofolio> portofolios;
    private List<Goal> goals;

    @PrePersist
    private void pre() {
       activityDate = new Date();
    }

    @Id
    @Column(name = "activityid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getActivityid() {
        return activityid;
    }

    public void setActivityid(int activityid) {
        this.activityid = activityid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "activity_category", joinColumns = @JoinColumn(name = "activityid"), inverseJoinColumns = @JoinColumn(name = "categoryid"))
    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(Date activityDate) {
        this.activityDate = activityDate;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    /*public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }*/

    @ManyToMany(mappedBy = "activities")
    public List<Portofolio> getPortofolios() {
        return portofolios;
    }

    public void setPortofolios(List<Portofolio> portofolios) {
        this.portofolios = portofolios;
    }

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "activity_goal", joinColumns = @JoinColumn(name = "activityid"), inverseJoinColumns = @JoinColumn(name = "goalid"))
    public List<Goal> getGoals() {
        return goals;
    }

    public void setGoals(List<Goal> goals) {
        this.goals = goals;
    }
}
