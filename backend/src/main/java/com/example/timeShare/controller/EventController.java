package com.example.timeShare.controller;

import com.example.timeShare.model.Event;
import com.example.timeShare.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Validated
@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Event>> getEventById(@PathVariable Long id) {
        return eventService.getEventById(id)
                .map(event -> new ResponseEntity<>(event, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/calendar/{calendarId}")
    public Flux<Event> getEventsByCalendar(@PathVariable Long calendarId) {
        return eventService.getEventsByCalendar(calendarId);
    }

    @PostMapping
    public Mono<ResponseEntity<Event>> createEvent(@Valid @RequestBody Event event) {
        return eventService.createEvent(event)
                .map(savedEvent -> new ResponseEntity<>(savedEvent, HttpStatus.CREATED));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Event>> updateEvent(@PathVariable Long id, @Valid @RequestBody Event event) {
        return eventService.updateEvent(id, event)
                .map(updatedEvent -> new ResponseEntity<>(updatedEvent, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteEvent(@PathVariable Long id) {
        return eventService.deleteEvent(id)
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/time-range")
    public Flux<Event> getEventsByTimeRange(@RequestParam("start") LocalDateTime start,
                                            @RequestParam("end") LocalDateTime end) {
        return eventService.getEventsByTimeRange(start, end);
    }

    @GetMapping("/sorted-by-start-time")
    public Flux<Event> getAllEventsSortedByStartTime() {
        return eventService.getAllEventsSortedByStartTime();
    }

}
