package com.example.timeShare.service;

import com.example.timeShare.model.Calendar;
import com.example.timeShare.repository.CalendarRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CalendarService {
    private final CalendarRepository calendarRepository;

    public CalendarService(CalendarRepository calendarRepository) {
        this.calendarRepository = calendarRepository;
    }

    public Mono<Calendar> createCalendar(Calendar calendar) {
        return calendarRepository.save(calendar);
    }

    public Flux<Calendar> getCalendarsByOwner(Long ownerId) {
        return calendarRepository.findByOwnerId(ownerId);
    }

    public Mono<Calendar> getCalendarById(Long id) {
        return calendarRepository.findById(id);
    }

    public Mono<Calendar> updateCalendar(Long id, Calendar calendar) {
        return calendarRepository.findById(id)
                .flatMap(existingCalendar -> {
                    existingCalendar.setName(calendar.getName());
                    existingCalendar.setDescription(calendar.getDescription());
                    return calendarRepository.save(existingCalendar);
                });
    }

    public Mono<Void> deleteCalendar(Long id) {
        return calendarRepository.deleteById(id);
    }

    public Flux<Calendar> getAllCalendarsSortedByCreatedDate() {
        return calendarRepository.findAllByOrderByCreatedDateDesc();
    }


}
