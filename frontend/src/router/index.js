import { createRouter, createWebHistory } from 'vue-router';
import Dashboard from '@/components/Dashboard.vue';
import AuthTabs from '@/components/AuthTabs.vue';
import CalendarList from '@/components/CalendarList.vue';
import CalendarView from '@/components/CalendarView.vue';
import EventList from '@/components/EventList.vue';
import AddCalendarForm from '@/components/AddCalendarForm.vue';
import LoginForm from '@/components/LoginForm.vue';
import UserRegistrationForm from '@/components/UserRegistrationForm.vue';
import SharedCalendarList from "@/components/SharedCalendarList.vue";

const routes = [
    { path: '/dashboard', component: Dashboard, meta: { requiresAuth: true } },
    { path: '/auth', component: AuthTabs },
    { path: '/calendars', component: CalendarList, meta: { requiresAuth: true } },
    { path: '/calendars/:id', component: CalendarView, name: 'CalendarView', meta: { requiresAuth: true } },
    { path: '/events', component: EventList, meta: { requiresAuth: true } },
    { path: '/add-calendar', component: AddCalendarForm, meta: { requiresAuth: true } },
    { path: '/login', component: LoginForm },
    { path: '/register', component: UserRegistrationForm },
    { path: '/calendar-detail/:id', component: CalendarView, name: 'CalendarDetail', meta: { requiresAuth: true } },
    { path: '/shared-calendars', component: SharedCalendarList, meta: { requiresAuth: true } },
    { path: '/event-list/:calendarId', component: EventList, name: 'EventList', meta: { requiresAuth: true } },
    { path: '/', redirect: '/auth' }
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

router.beforeEach((to, from, next) => {
    const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
    const isAuthenticated = !!localStorage.getItem('authToken');

    if (requiresAuth && !isAuthenticated) {
        next('/auth');
    } else {
        next();
    }
});

export default router;
