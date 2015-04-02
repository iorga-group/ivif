package com.iorga.ivif.test.entity;

import com.iorga.ivif.ja.IEntity;
import com.iorga.ivif.test.Versionable;
import com.iorga.ivif.test.entity.select.UserPassType;
import com.iorga.ivif.test.entity.select.UserStatusType;
import java.io.Serializable;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "TEST_USER")
public class User implements Serializable, IEntity<Integer>, Versionable<Long> {

    public static String ENABLED_TRUE_VALUE = "OK";
    public static String ENABLED_FALSE_VALUE = "KO";

    @Id
    @NotNull
    @SequenceGenerator(name = "USER_ID_SEQ", sequenceName = "USER_ID_SEQ")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="USER_ID_SEQ")
    private Integer id;

    @Column(name = "LAST_NAME")
    @NotNull
    private String name;

    @Column(name = "FIRST_NAME")
    @NotNull
    private String firstName;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumns({
        @JoinColumn(name = "PROFILE_ID", referencedColumnName="id", insertable = false, updatable = false)
    })
    private Profile profile;

    @Version
    private Long version;

    @Column(name = "\"COMMENT\"")
    private String comment;

    @Type(type = "com.iorga.ivif.test.entity.select.UserStatusType$UserType")
    private UserStatusType status;

    @Column(name = "enabled")
    private String enabled_value;

    @Column(name = "enabled", insertable = false, updatable = false)
    @Formula("(CASE WHEN enabled = 'OK' THEN 1 WHEN enabled = 'KO' THEN 0 ELSE NULL END)")
    private Boolean enabled;

    @Transient
    private String commentTemp;

    private String bigComment;

    @Type(type = "com.iorga.ivif.test.entity.select.UserPassType$UserType")
    private UserPassType pass;

    private Date lastModification;


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
            .append(name).append(", ")
            .append(firstName);
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public UserStatusType getStatus() {
        return status;
    }

    public void setStatus(UserStatusType status) {
        this.status = status;
    }

    public String getEnabled_value() {
        return enabled_value;
    }

    public void setEnabled_value(String enabled_value) {
        this.enabled_value = enabled_value;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
        // set the original value
        if (Boolean.TRUE.equals(enabled)) {
            setEnabled_value(ENABLED_TRUE_VALUE);
        } else if (Boolean.FALSE.equals(enabled)) {
            setEnabled_value(ENABLED_FALSE_VALUE);
        } else {
            setEnabled_value(null);
        }
    }

    public String getCommentTemp() {
        return commentTemp;
    }

    public void setCommentTemp(String commentTemp) {
        this.commentTemp = commentTemp;
    }

    public String getBigComment() {
        return bigComment;
    }

    public void setBigComment(String bigComment) {
        this.bigComment = bigComment;
    }

    public UserPassType getPass() {
        return pass;
    }

    public void setPass(UserPassType pass) {
        this.pass = pass;
    }

    public Date getLastModification() {
        return lastModification;
    }

    public void setLastModification(Date lastModification) {
        this.lastModification = lastModification;
    }

}