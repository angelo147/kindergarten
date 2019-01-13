package edu.kindergarten.registration.persistence.model;

import javax.persistence.*;

@Entity
@Table(name = "status", schema = "kindergarten", catalog = "")
public class StatusEntity {
    private int statusid;
    private String status;

    @Id
    @Column(name = "statusid")
    public int getStatusid() {
        return statusid;
    }

    public void setStatusid(int statusid) {
        this.statusid = statusid;
    }

    @Basic
    @Column(name = "status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StatusEntity that = (StatusEntity) o;

        if (statusid != that.statusid) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = statusid;
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }
}
