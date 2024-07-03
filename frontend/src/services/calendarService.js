import apiClient from './axios';

export default {
    getCalendarsByUser(userId) {
        const token = localStorage.getItem('authToken');
        return apiClient.get(`/calendars/user/${userId}`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    },
    getCalendar(calendarId) {
        return apiClient.get(`/calendars/${calendarId}`);
    },
    createCalendar(calendar) {
        const token = localStorage.getItem('authToken');
        return apiClient.post('/calendars', calendar, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    },
    updateCalendar(id, calendar) {
        const token = localStorage.getItem('authToken');
        return apiClient.put(`/calendars/${id}`, calendar, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    },
    deleteCalendar(id) {
        const token = localStorage.getItem('authToken');
        return apiClient.delete(`/calendars/${id}`, {
            headers: {
                Authorization: `Bearer ${token}`
            }
        });
    }
};
