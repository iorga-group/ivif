package com.iorga.ivif.test.entity;

import com.iorga.ivif.ja.IEntity;
import java.io.Serializable;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "TEST_PROFILE")
public class Profile implements Serializable, IEntity<Integer> {

    @Id
    @NotNull
    private Integer id;

    @Column(name = "NAME")
    @NotNull
    private String name;

    private String description;


    @Override
    public Integer entityId() {
        return id;
    }

    @Override
    public void entityId(Integer id) {
        setId(id);
    }

    @Override
    public String displayName() {
        StringBuilder displayNameBuilder = new StringBuilder();
        displayNameBuilder
            .append(name);
        return displayNameBuilder.toString();
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}