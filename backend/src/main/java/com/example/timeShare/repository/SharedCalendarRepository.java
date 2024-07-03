package com.example.timeShare.repository;


import com.example.timeShare.model.SharedCalendar;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SharedCalendarRepository extends ReactiveCrudRepository<SharedCalendar, Long> {
    Flux<SharedCalendar> findByUserId(Long userId);
    Mono<SharedCalendar> findByUserIdAndCalendarId(Long userId, Long calendarId);
    Mono<Void> deleteByUserIdAndCalendarId(Long userId, Long calendarId);

}

