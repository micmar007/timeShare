<template>
  <div class="shared-calendar-list">
    <h2>Współdzielone Kalendarze</h2>
    <ul>
      <li v-for="sharedCalendar in sharedCalendars" :key="sharedCalendar.id">
        <router-link :to="`/calendars/${sharedCalendar.id}`">
          {{ sharedCalendar.name }}
        </router-link>
        <div v-if="sharedCalendar.permission === 'WRITE' || sharedCalendar.permission === 'OWNER'">
          <button @click="editCalendar(sharedCalendar.id)">Edytuj Kalendarz</button>
        </div>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const sharedCalendars = ref([]);

onMounted(async () => {
  try {
    const userId = localStorage.getItem('userId');
    sharedCalendars.value = await getSharedCalendarsByUser(userId);
  } catch (error) {
    console.error('Błąd podczas pobierania współdzielonych kalendarzy:', error);
  }
});

function editCalendar(calendarId) {
  console.log('Edytuj kalendarz o ID:', calendarId);
}
</script>
