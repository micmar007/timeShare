package com.example.timeShare.controller;

import com.example.timeShare.model.SharedCalendar;
import com.example.timeShare.service.ShareCalendarRequest;
import com.example.timeShare.service.SharedCalendarService;
import com.example.timeShare.service.UpdatePermissionsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/shared-calendars")
public class SharedCalendarController {
    @Autowired
    private SharedCalendarService sharedCalendarService;

    @PostMapping("/share")
    public Mono<ResponseEntity<Void>> shareCalendar(@RequestBody ShareCalendarRequest request) {
        return sharedCalendarService.shareCalendar(request.getEmail(), request.getCalendarId(), request.getPermission())
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK)))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build()));
    }

    @GetMapping("/user/{userId}")
    public Flux<SharedCalendar> getSharedCalendars(@PathVariable Long userId) {
        return sharedCalendarService.findSharedCalendarsByUser(userId);
    }

    @PutMapping("/update-permissions")
    public Mono<ResponseEntity<SharedCalendar>> updatePermissions(@RequestBody UpdatePermissionsRequest request) {
        return sharedCalendarService.updatePermissions(request.getEmail(), request.getCalendarId(), request.getPermission())
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build()));
    }

    @DeleteMapping("/unshare")
    public Mono<ResponseEntity<Void>> unshareCalendar(@RequestParam Long calendarId, @RequestParam Long userId) {
        return sharedCalendarService.unshareCalendar(calendarId, userId)
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build()));
    }

    @GetMapping("/permissions")
    public Mono<ResponseEntity<String>> getSharedCalendarPermissions(
            @RequestParam Long userId,
            @RequestParam Long calendarId) {
        return sharedCalendarService.getSharedCalendarPermissions(userId, calendarId)
                .map(permissions -> ResponseEntity.ok(permissions.toString()))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build()));
    }
}
