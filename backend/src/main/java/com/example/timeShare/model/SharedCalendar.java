package com.example.timeShare.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@Table("shared_calendars")
public class SharedCalendar {
    @Id
    private Long id;

    @Column("calendar_id")
    private Long calendarId;

    @Column("user_id")
    private Long userId;

    @Setter
    @Column("permission")
    private Permission permission;

    @Transient
    private String calendarName;

    @Transient
    private String calendarDescription;

    @Transient
    private String ownerEmail;

}
