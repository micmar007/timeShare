import apiClient from './axios';

export default {
    register(user) {
        return apiClient.post('/auth/register', user)
            .then(response => response.data)
            .catch(error => {
                if (error.response && error.response.data) {
                    throw new Error(error.response.data);
                } else {
                    throw new Error('An error occurred during registration');
                }
            });
    },
    login(credentials) {
        return apiClient.post('/auth/login', credentials)
            .then(response => {
                localStorage.setItem('authToken', response.data.token);
                apiClient.defaults.headers.common['Authorization'] = `Bearer ${response.data.token}`;
                return response;
            });
    },
    logout() {
        localStorage.removeItem('authToken');
        delete apiClient.defaults.headers.common['Authorization'];
    }
};
