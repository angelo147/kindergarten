package edu.kindergarten.registration.persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "goal", schema = "kindergarten", catalog = "")
public class Goal {
    private int goalid;
    private String name;
    @JsonIgnore
    private Category category;

    @Id
    @Column(name = "goalid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getGoalid() {
        return goalid;
    }

    public void setGoalid(int goalid) {
        this.goalid = goalid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne
    @JoinColumn(name = "categoryid", referencedColumnName = "categoryid", nullable = false)
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
