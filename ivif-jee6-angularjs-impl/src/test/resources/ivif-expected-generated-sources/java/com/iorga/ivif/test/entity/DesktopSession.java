package com.iorga.ivif.test.entity;

import com.iorga.ivif.test.entity.DesktopSession.DesktopSessionId;
import java.io.Serializable;
import java.lang.Integer;
import java.lang.String;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@IdClass(DesktopSessionId.class)
@Table(name = "TEST_DESKTOP_SESSION")
public class DesktopSession {


    @Id
    @NotNull
    private Integer userId;

    @Id
    @NotNull
    private Integer computerId;

    @NotNull
    private String name;

    public static class DesktopSessionId implements Serializable {
        private Integer userId;
        private Integer computerId;

        public DesktopSessionId() {}

        public DesktopSessionId(Integer userId, Integer computerId) {
            this.userId = userId;
            this.computerId = computerId;
        }

        public Integer getUserId() {
            return userId;
        }
        public void setUserId(Integer userId) {
            this.userId = userId;
        }
        public Integer getComputerId() {
            return computerId;
        }
        public void setComputerId(Integer computerId) {
            this.computerId = computerId;
        }
    }

    /// Getters & Setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getComputerId() {
        return computerId;
    }

    public void setComputerId(Integer computerId) {
        this.computerId = computerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}