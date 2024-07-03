package com.example.timeShare.service;

import com.example.timeShare.model.Permission;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShareCalendarRequest {
    private String email;
    private Long calendarId;
    private Permission permission;
}
