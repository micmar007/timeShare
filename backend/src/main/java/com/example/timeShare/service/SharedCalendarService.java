package com.example.timeShare.service;

import com.example.timeShare.model.SharedCalendar;
import com.example.timeShare.model.User;
import com.example.timeShare.model.Calendar;
import com.example.timeShare.model.Permission;
import com.example.timeShare.repository.SharedCalendarRepository;
import com.example.timeShare.repository.UserRepository;
import com.example.timeShare.repository.CalendarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SharedCalendarService {
    @Autowired
    private SharedCalendarRepository sharedCalendarRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CalendarRepository calendarRepository;


    public Mono<Void> shareCalendar(String email, Long calendarId, Permission permission) {
        return userRepository.findByEmail(email)
                .zipWith(calendarRepository.findById(calendarId))
                .flatMap(tuple -> {
                    User user = tuple.getT1();
                    Calendar calendar = tuple.getT2();

                    return sharedCalendarRepository.findByUserIdAndCalendarId(user.getId(), calendar.getId())
                            .flatMap(sharedCalendar -> {
                                sharedCalendar.setPermission(permission);
                                return sharedCalendarRepository.save(sharedCalendar);
                            })
                            .switchIfEmpty(Mono.defer(() -> {
                                SharedCalendar newSharedCalendar = new SharedCalendar();
                                newSharedCalendar.setUserId(user.getId());
                                newSharedCalendar.setCalendarId(calendar.getId());
                                newSharedCalendar.setPermission(permission);
                                return sharedCalendarRepository.save(newSharedCalendar);
                            }));
                }).then();
    }

    public Flux<SharedCalendar> findSharedCalendarsByUser(Long userId) {
        return sharedCalendarRepository.findByUserId(userId)
                .flatMap(sharedCalendar ->
                        calendarRepository.findById(sharedCalendar.getCalendarId())
                                .flatMap(calendar ->
                                        userRepository.findById(calendar.getOwnerId())
                                                .map(owner -> {
                                                    sharedCalendar.setCalendarName(calendar.getName());
                                                    sharedCalendar.setCalendarDescription(calendar.getDescription());
                                                    sharedCalendar.setOwnerEmail(owner.getEmail());
                                                    return sharedCalendar;
                                                })
                                )
                );
    }
    public Mono<SharedCalendar> updatePermissions(String email, Long calendarId, Permission permission) {
        return userRepository.findByEmail(email)
                .flatMap(user -> sharedCalendarRepository.findByUserIdAndCalendarId(user.getId(), calendarId)
                        .flatMap(sharedCalendar -> {
                            sharedCalendar.setPermission(permission);
                            return sharedCalendarRepository.save(sharedCalendar);
                        }))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Shared calendar or user not found")));
    }


    public Mono<Void> unshareCalendar(Long calendarId, Long userId) {
        return sharedCalendarRepository.deleteByUserIdAndCalendarId(userId, calendarId);
    }

    public Mono<Permission> getSharedCalendarPermissions(Long userId, Long calendarId) {
        return sharedCalendarRepository.findByUserIdAndCalendarId(userId, calendarId)
                .map(SharedCalendar::getPermission);
    }


}
