package com.college.backend.entity;
import jakarta.persistence.*;
import java.time.LocalDate;

//for creating table in the sql we have used this file.
@Entity //class should be mapped with the corresponding table in database
@Table(name = "notices") //it will represent the table and table name will be -> notices
public class Notice {


    @Id //make primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) //auto - increments the id
    private Long id;  // declared as private for security

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    private String title;

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    private String category;

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    private LocalDate publicationDate;

    public LocalDate getPublicationDate() {
        return publicationDate;
    }
    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    private String pdfPath; //for storing the path of file in the databse

    public String getPdfPath() {
        return pdfPath;
    }
    public void setPdfPath(String pdfPath) {
        this.pdfPath = pdfPath;
    }
}


//JPA is a specification that defines how Java objects are mapped to relational databases.
//Hibernate is a framework that implements JPA and converts Java objects into SQL queries automatically.
