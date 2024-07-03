package com.example.timeShare.controller;

import com.example.timeShare.model.Calendar;
import com.example.timeShare.service.CalendarService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Validated
@RestController
@RequestMapping("/api/calendars")
public class CalendarController {
    private final CalendarService calendarService;

    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Calendar>> getCalendarById(@PathVariable Long id) {
        return calendarService.getCalendarById(id)
                .map(calendar -> new ResponseEntity<>(calendar, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/user/{userId}")
    public Flux<Calendar> getCalendarsByUser(@PathVariable Long userId) {
        return calendarService.getCalendarsByOwner(userId);
    }


    @PostMapping
    public Mono<ResponseEntity<Calendar>> createCalendar(@Valid @RequestBody Calendar calendar) {
        return calendarService.createCalendar(calendar)
                .map(savedCalendar -> new ResponseEntity<>(savedCalendar, HttpStatus.CREATED));
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Calendar>> updateCalendar(@PathVariable Long id, @Valid @RequestBody Calendar calendar) {
        return calendarService.updateCalendar(id, calendar)
                .map(updatedCalendar -> new ResponseEntity<>(updatedCalendar, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteCalendar(@PathVariable Long id) {
        return calendarService.deleteCalendar(id)
                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/sorted-by-created-date")
    public Flux<Calendar> getAllCalendarsSortedByCreatedDate() {
        return calendarService.getAllCalendarsSortedByCreatedDate();
    }


}

