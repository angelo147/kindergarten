package edu.kindergarten.registration.persistence.model;


import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="TOKEN")
@NamedQueries({
        @NamedQuery(name="Token.findAll", query="SELECT t FROM Token t"),
        @NamedQuery(name="Token.findActiveByEmail", query="SELECT t FROM Token t where t.active = true and t.email = :email and t.expireDate > :date and t.revokeDate is null and t.verifyDate is null"),
        @NamedQuery(name="Token.findActiveByValue", query="SELECT t FROM Token t where t.active = true and t.token = :token and t.expireDate > :date and t.revokeDate is null and t.verifyDate is null")
}) public class Token {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
    private String token;
    private Boolean active;
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date revokeDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date expireDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date verifyDate;
    private String email;
    private int userId;

    public Date getVerifyDate() {
        return verifyDate;
    }

    public void setVerifyDate(Date verifyDate) {
        this.verifyDate = verifyDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getRevokeDate() {
        return revokeDate;
    }

    public void setRevokeDate(Date revokeDate) {
        this.revokeDate = revokeDate;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
