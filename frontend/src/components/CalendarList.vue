<template>
  <div>
    <h1>Lista Kalendarzy</h1>
    <ul>
      <li v-for="calendar in calendars" :key="calendar.id">
        <router-link :to="{ name: 'CalendarView', params: { id: calendar.id } }">
          {{ calendar.name }}
        </router-link>
      </li>
    </ul>
    <p v-if="error">{{ error }}</p>
  </div>
</template>

<script>
import CalendarService from '@/services/calendarService.js';

export default {
  data() {
    return {
      calendars: [],
      error: null
    };
  },
  created() {
    CalendarService.getCalendars()
        .then(response => {
          this.calendars = response.data;
        })
        .catch(err => {
          this.error = 'Błąd przy pobieraniu kalendarzy';
        });
  }
};
</script>
