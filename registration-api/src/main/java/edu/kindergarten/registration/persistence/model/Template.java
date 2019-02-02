package edu.kindergarten.registration.persistence.model;

import javax.persistence.*;
import java.sql.Blob;

@Entity
@Table(name="TEMPLATES")
@NamedQuery(name="Template.findByType", query="SELECT t FROM Template t where t.type = :templatetype")
public class Template {
    @Id
    private long id;
    private String html;
    private String type;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
