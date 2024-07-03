import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import apiClient from './services/axios';
import VCalendar from 'v-calendar';
import 'v-calendar/style.css';
import './assets/main.css';
import VueCal from "vue-cal";

const app = createApp(App);

const token = localStorage.getItem('authToken');
if (token) {
    apiClient.defaults.headers.common['Authorization'] = `Bearer ${token}`;
}

app.use(router);
app.use(VCalendar, VueCal,{})
app.mount('#app');
