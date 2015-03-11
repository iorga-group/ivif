package com.iorga.ivif.test.entity;

import com.iorga.ivif.ja.IEntity;
import com.iorga.ivif.test.entity.DesktopSession.DesktopSessionId;
import java.io.Serializable;
import java.lang.Integer;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@IdClass(DesktopSessionId.class)
@Table(name = "TEST_DESKTOP_SESSION")
public class DesktopSession implements Serializable, IEntity<DesktopSessionId> {

    private DesktopSessionId _entityId = new DesktopSessionId();

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

    @Override
    public DesktopSessionId entityId() {
        return _entityId;
    }

    @Override
    public void entityId(DesktopSessionId id) {
        if (id == null) {
            setUserId(null);
            setComputerId(null);
        } else {
            setUserId(id.getUserId());
            setComputerId(id.getComputerId());
        }
    }

    @Override
    public String displayName() {
        StringBuilder displayNameBuilder = new StringBuilder("DesktopSession#[");
        displayNameBuilder
            .append(userId).append(", ")
            .append(computerId).append("]");
        return displayNameBuilder.toString();
    }

    /// Getters & Setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
        _entityId.userId = userId;
    }

    public Integer getComputerId() {
        return computerId;
    }

    public void setComputerId(Integer computerId) {
        this.computerId = computerId;
        _entityId.computerId = computerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}