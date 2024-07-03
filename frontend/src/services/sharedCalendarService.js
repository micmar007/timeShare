import apiClient from './axios';

const sharedCalendarService = {
    async getSharedCalendarsByUser(userId) {
        try {
            const response = await apiClient.get(`/shared-calendars/user/${userId}`);
            return response.data;
        } catch (error) {
            console.error('Błąd podczas pobierania współdzielonych kalendarzy:', error);
            throw error;
        }
    },
    async shareCalendar(email, calendarId, permission) {
        const token = localStorage.getItem('authToken');
        try {
            const response = await apiClient.post('/shared-calendars/share', { email, calendarId, permission }, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });
            return response.data;
        } catch (error) {
            console.error('Błąd podczas udostępniania kalendarza:', error);
            throw error;
        }
    },
    async userExists(email) {
        try {
            const response = await apiClient.get(`/users/exists`, { params: { email } });
            return response.data;
        } catch (error) {
            console.error('Błąd podczas sprawdzania istnienia użytkownika:', error);
            throw error;
        }
    },
    async updatePermissions(email, calendarId, permission) {
        const token = localStorage.getItem('authToken');
        try {
            const response = await apiClient.put('/shared-calendars/update-permissions', { email, calendarId, permission }, {
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });
            return response.data;
        } catch (error) {
            console.error('Błąd podczas aktualizacji uprawnień kalendarza:', error);
            throw error;
        }
    },
    async unshareCalendar(calendarId, userId) {
        const token = localStorage.getItem('authToken');
        try {
            await apiClient.delete('/shared-calendars/unshare', {
                params: { calendarId, userId },
                headers: {
                    Authorization: `Bearer ${token}`
                }
            });
        } catch (error) {
            console.error('Błąd podczas cofania udostępnienia kalendarza:', error);
            throw error;
        }
    }
};

export default sharedCalendarService;
