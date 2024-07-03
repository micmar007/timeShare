package com.example.timeShare.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("calendars")
public class Calendar {
    @Id
    private Long id;

    private String name;
    private String description;

    @Column("owner_id")
    private Long ownerId;

    @Column("createddate")
    @CreatedDate
    private LocalDateTime createdDate;

    @Column("updateddate")
    @LastModifiedDate
    private LocalDateTime updatedDate;

}
