package edu.kindergarten.registration.rest;

import java.util.List;
import java.util.UUID;

public class UserInfo {
    private Long userId;
    private List<Role> role;
    private UUID jwtId;

    public UserInfo(long userId, List<Role> role, UUID jwtId) {
        this.userId = userId;
        this.role = role;
        this.jwtId = jwtId;
    }

    public UserInfo() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Role> getRole() {
        return role;
    }

    public void setRole(List<Role> role) {
        this.role = role;
    }

    public UUID getJwtId() {
        return jwtId;
    }

    public void setJwtId(UUID jwtId) {
        this.jwtId = jwtId;
    }
}
