package com.example.timeShare.repository;

import com.example.timeShare.model.Event;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

public interface EventRepository extends ReactiveCrudRepository<Event, Long> {
    Flux<Event> findByCalendarId(Long calendarId);
    Flux<Event> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    Flux<Event> findAllByOrderByStartTimeAsc();
}
