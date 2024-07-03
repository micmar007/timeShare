package com.example.timeShare.service;

import com.example.timeShare.model.Event;
import com.example.timeShare.repository.EventRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public Mono<Event> createEvent(Event event) {
        return eventRepository.save(event);
    }

    public Flux<Event> getEventsByCalendar(Long calendarId) {
        return eventRepository.findByCalendarId(calendarId);
    }

    public Mono<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public Mono<Event> updateEvent(Long id, Event event) {
        return eventRepository.findById(id)
                .flatMap(existingEvent -> {
                    existingEvent.setTitle(event.getTitle());
                    existingEvent.setDescription(event.getDescription());
                    existingEvent.setStartTime(event.getStartTime());
                    existingEvent.setEndTime(event.getEndTime());
                    existingEvent.setLocation(event.getLocation());
                    existingEvent.setAllDay(event.isAllDay());
                    return eventRepository.save(existingEvent);
                });
    }

    public Mono<Void> deleteEvent(Long id) {
        return eventRepository.deleteById(id);
    }

    public Flux<Event> getEventsByTimeRange(LocalDateTime start, LocalDateTime end) {
        return eventRepository.findByStartTimeBetween(start, end);
    }

    public Flux<Event> getAllEventsSortedByStartTime() {
        return eventRepository.findAllByOrderByStartTimeAsc();
    }
}
