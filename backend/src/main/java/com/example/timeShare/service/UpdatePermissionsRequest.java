package com.example.timeShare.service;
import com.example.timeShare.model.Permission;
import lombok.Data;

@Data
public class UpdatePermissionsRequest {
    private String email;
    private Long calendarId;
    private Permission permission;
}

