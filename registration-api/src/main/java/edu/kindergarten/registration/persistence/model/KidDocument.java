package edu.kindergarten.registration.persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.sql.Blob;

@Entity
@Table(name = "kiddocument", schema = "kindergarten", catalog = "")
public class KidDocument {
    private int documentId;
    private String title;
    @JsonIgnore
    private Blob documentStream;
    @JsonIgnore
    private KidprofileEntity kidprofileEntity;

    @Id
    @Column(name = "documentid")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    public int getDocumentId() {
        return documentId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Blob getDocumentStream() {
        return documentStream;
    }

    public void setDocumentStream(Blob documentStream) {
        this.documentStream = documentStream;
    }

    @ManyToOne
    @JoinColumn(name = "kidprofileid", referencedColumnName = "id", nullable = false)
    public KidprofileEntity getKidprofileEntity() {
        return kidprofileEntity;
    }

    public void setKidprofileEntity(KidprofileEntity kidprofileEntity) {
        this.kidprofileEntity = kidprofileEntity;
    }
}
