import apiClient from './axios';

export default {
    getEventsByCalendar(calendarId) {
        return apiClient.get(`/events/calendar/${calendarId}`);
    },
    createEvent(event) {
        return apiClient.post('/events', event);
    },
    updateEvent(id, event) {
        return apiClient.put(`/events/${id}`, event);
    },
    deleteEvent(id) {
        return apiClient.delete(`/events/${id}`);
    }
};
