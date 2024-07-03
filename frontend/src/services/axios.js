import axios from 'axios';
import router from '@/router';

const apiClient = axios.create({
    baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
    headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json'
    }
});


apiClient.interceptors.response.use(
    response => response,
    error => {
        if (error.response && error.response.status === 401) {

            localStorage.removeItem('authToken');
            localStorage.removeItem('user');

            router.push('/auth');
        }
        return Promise.reject(error);
    }
);

export default apiClient;
