<template>
  <div class="dashboard">
    <header>
      <div class="header-content">
        <h2>Witaj, {{ username }}!</h2>
        <button @click="logout" class="logout-button">Wyloguj</button>
      </div>
    </header>
    <section>
      <AddCalendarForm @calendar-added="handleCalendarAdded" />

      <div v-if="calendars.length">
        <h3>Twoje kalendarze:</h3>
        <div class="calendars">
          <div v-for="calendar in calendars" :key="calendar.id" class="calendar" @click="goToCalendarDetail(calendar.id)">
            <h4>{{ calendar.name }}</h4>
            <p>{{ calendar.description }}</p>
            <img src="@/assets/delete.svg" class="delete-icon" @click.stop="deleteCalendar(calendar.id)" alt="Delete" />
          </div>
        </div>
      </div>

      <div v-if="sharedCalendars.length">
        <h3>Współdzielone kalendarze:</h3>
        <div class="calendars shared">
          <div v-for="sharedCalendar in sharedCalendars" :key="sharedCalendar.calendarId" class="calendar" @click="goToCalendarDetail(sharedCalendar.calendarId)">
            <h4>{{ sharedCalendar.calendarName }}</h4>
            <p><strong>Opis:</strong> {{ sharedCalendar.calendarDescription }}</p>
            <p><strong>Udostępnione przez:</strong> {{ sharedCalendar.ownerEmail }}</p>
            <p><strong>Uprawnienia:</strong> {{ sharedCalendar.permission }}</p>
          </div>
        </div>
      </div>

      <div v-if="!calendars.length && !sharedCalendars.length">
        <p>Nie masz jeszcze żadnych kalendarzy. Dodaj pierwszy kalendarz!</p>
      </div>
    </section>
  </div>
</template>

<script>
import AddCalendarForm from '@/components/AddCalendarForm.vue';
import calendarService from '@/services/calendarService.js';
import sharedCalendarService from '@/services/sharedCalendarService';

export default {
  name: 'Dashboard',
  components: {
    AddCalendarForm
  },
  data() {
    return {
      username: '',
      calendars: [],
      sharedCalendars: [],
      currentCalendarPermissions: {}
    };
  },
  mounted() {
    const user = JSON.parse(localStorage.getItem('user'));
    if (user && user.username) {
      this.username = user.username;
    } else {
      console.error('Nie znaleziono użytkownika w localStorage');
    }
    this.fetchUserCalendars();
    this.fetchSharedCalendars();
  },
  methods: {
    async fetchUserCalendars() {
      const user = JSON.parse(localStorage.getItem('user'));
      const userId = user.id;
      try {
        const response = await calendarService.getCalendarsByUser(userId);
        this.calendars = response.data;
        await this.fetchCalendarPermissions(this.calendars);
      } catch (error) {
        console.error('Błąd podczas pobierania kalendarzy:', error);
      }
    },
    async fetchSharedCalendars() {
      const user = JSON.parse(localStorage.getItem('user'));
      const userId = user.id;
      try {
        const response = await sharedCalendarService.getSharedCalendarsByUser(userId);
        this.sharedCalendars = response;
        await this.fetchSharedCalendarPermissions(this.sharedCalendars);
      } catch (error) {
        console.error('Błąd podczas pobierania współdzielonych kalendarzy:', error);
      }
    },
    handleCalendarAdded(newCalendar) {
      this.calendars.push(newCalendar);
    },
    goToCalendarDetail(calendarId) {
      this.$router.push({ name: 'CalendarDetail', params: { id: calendarId } });
    },
    async deleteCalendar(id) {
      if (confirm('Czy na pewno chcesz usunąć ten kalendarz?')) {
        try {
          await calendarService.deleteCalendar(id);
          this.calendars = this.calendars.filter(calendar => calendar.id !== id);
        } catch (error) {
          console.error('Błąd podczas usuwania kalendarza:', error);
        }
      }
    },
    logout() {
      localStorage.removeItem('authToken');
      localStorage.removeItem('user');
      this.$router.push('/auth');
    },
    async fetchCalendarPermissions(calendars) {
      const user = JSON.parse(localStorage.getItem('user'));
      const userId = user.id;
      try {
        for (const calendar of calendars) {
          const permissions = await calendarService.getCalendarPermissions(userId, calendar.id);
          this.$set(this.currentCalendarPermissions, calendar.id, permissions);
        }
      } catch (error) {
        console.error('Błąd podczas pobierania uprawnień do kalendarzy:', error);
      }
    },
    async fetchSharedCalendarPermissions(sharedCalendars) {
      const user = JSON.parse(localStorage.getItem('user'));
      const userId = user.id;
      try {
        for (const sharedCalendar of sharedCalendars) {
          const permissions = await sharedCalendarService.getSharedCalendarPermissions(userId, sharedCalendar.calendarId);
          this.$set(this.currentCalendarPermissions, sharedCalendar.calendarId, permissions);
        }
      } catch (error) {
        console.error('Błąd podczas pobierania uprawnień do współdzielonych kalendarzy:', error);
      }
    },
    canEditCalendar(calendarId) {
      const permissions = this.currentCalendarPermissions[calendarId];
      return permissions && permissions.canEdit;
    }
  }
};
</script>

<style scoped>
.dashboard {
  padding: 2rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  min-height: 100vh;
  background-color: #333;
  color: #ffffff;
}

.header-content {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

header h2 {
  margin: 0;
  color: #42b983;
}

.logout-button {
  background-color: #ff6666;
  color: white;
  border: none;
  border-radius: 5px;
  padding: 0.5rem 1rem;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.logout-button:hover {
  background-color: #e55b5b;
}

section {
  width: 100%;
  max-width: 1200px;
  text-align: center;
  flex-grow: 1;
}

section h3 {
  color: #42b983;
  margin-bottom: 1rem;
}

section p {
  margin-bottom: 2rem;
  font-size: 1.2rem;
  color: #ddd;
}

.calendars {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
  justify-content: center;
}

.calendars.shared {
  margin-top: 2rem;
}

.calendar {
  position: relative;
  background-color: #444;
  border-radius: 8px;
  padding: 1rem;
  width: 100%;
  text-align: center;
  cursor: pointer;
  transition: background-color 0.3s ease, transform 0.3s ease;
}

.calendar:hover {
  background-color: #555;
  transform: translateY(-5px);
}

.calendar h4 {
  margin: 0;
  color: #42b983;
}

.calendar p {
  color: #ccc;
  margin: 0.5rem 0;
}

.delete-icon {
  position: absolute;
  top: 10px;
  right: 10px;
  width: 20px;
  height: 20px;
  cursor: pointer;
  transition: opacity 0.3s ease;
}

.delete-icon:hover {
  opacity: 0.7;
}
</style>
