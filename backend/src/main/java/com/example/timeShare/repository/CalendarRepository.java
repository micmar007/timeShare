package com.example.timeShare.repository;

import com.example.timeShare.model.Calendar;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface CalendarRepository extends ReactiveCrudRepository<Calendar, Long> {
    Flux<Calendar> findByOwnerId(Long ownerId);
    Flux<Calendar> findAllByOrderByCreatedDateDesc();
}
