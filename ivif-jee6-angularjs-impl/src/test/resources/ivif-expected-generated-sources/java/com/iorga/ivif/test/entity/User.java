package com.iorga.ivif.test.entity;

import com.iorga.ivif.ja.BooleanUserType;
import com.iorga.ivif.test.Versionable;
import com.iorga.ivif.test.entity.select.UserStatusType;
import java.io.Serializable;
import java.lang.Boolean;
import java.lang.Integer;
import java.lang.Long;
import java.lang.String;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "TEST_USER")
public class User implements Serializable, Versionable<Long> {

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
    @JoinColumn(name = "PROFILE_ID")
    private Profile profile;

    @Version
    private Long version;

    @Column(name = "\"COMMENT\"")
    private String comment;

    @Type(type = "com.iorga.ivif.test.entity.select.UserStatusType$UserType")
    private UserStatusType status;

    public static class EnabledUserType extends BooleanUserType<String> {
        public EnabledUserType() {
            super("OK", "KO");
        }
    }
    @Type(type = "com.iorga.ivif.test.entity.User$EnabledUserType")
    private Boolean enabled;

    @Transient
    private String commentTemp;

    private String bigComment;


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

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
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

}