package com.iorga.ivif.test.entity;

import java.io.Serializable;
import java.lang.Integer;
import java.lang.String;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "TEST_PROFILE")
public class Profile implements Serializable {

    @Id
    @NotNull
    private Integer id;

    @Column(name = "NAME")
    @NotNull
    private String name;


    /// Getters & Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}