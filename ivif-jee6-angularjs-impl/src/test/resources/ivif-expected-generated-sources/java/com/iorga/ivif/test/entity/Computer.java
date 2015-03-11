package com.iorga.ivif.test.entity;

import com.iorga.ivif.ja.IEntity;
import java.io.Serializable;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "TEST_COMPUTER")
public class Computer implements Serializable, IEntity<Integer> {

    @Id
    @NotNull
    private Integer id;

    @NotNull
    private String name;

    @Version
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "DEFAULT_PROFILE_ID")
    private Profile defaultProfile;


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
        StringBuilder displayNameBuilder = new StringBuilder("Computer#");
        displayNameBuilder
            .append(id);
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Profile getDefaultProfile() {
        return defaultProfile;
    }

    public void setDefaultProfile(Profile defaultProfile) {
        this.defaultProfile = defaultProfile;
    }

}