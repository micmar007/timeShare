<template>
  <div class="calendar-view">
    <header>
      <h2>{{ calendar.name }}</h2>
      <div>
        <button @click="goToEventList" class="event-list-button">Lista zdarzeń</button>
        <button @click="goBack" class="back-button">Powrót</button>
      </div>
    </header>
    <section>
      <vue-cal
          v-if="calendarEvents"
          :events="calendarEvents"
          :time="true"
          :view="view"
          @cell-click="showAddEventForm"
          @event-click="showEventDetails"
          style="height: 600px;"
      />
      <div v-if="canAddEvent && showForm" class="add-event-form">
        <h3>Dodaj wydarzenie</h3>
        <form @submit.prevent="addEvent">
          <div class="form-group">
            <label for="title">Tytuł:</label>
            <input type="text" id="title" v-model="newEvent.title" required />
          </div>
          <div class="form-group">
            <label for="description">Opis:</label>
            <textarea id="description" v-model="newEvent.description" required></textarea>
          </div>
          <div class="form-group">
            <label for="start">Czas rozpoczęcia:</label>
            <input type="datetime-local" id="start" v-model="newEvent.start" required />
          </div>
          <div class="form-group">
            <label for="end">Czas zakończenia:</label>
            <input type="datetime-local" id="end" v-model="newEvent.end" required />
          </div>
          <button type="submit">Dodaj</button>
          <button type="button" @click="showForm = false">Anuluj</button>
        </form>
      </div>
      <div v-if="canShareCalendar" class="share-calendar-form">
        <h2>Udostępnij kalendarz</h2>
        <form @submit.prevent="shareCalendar">
          <div class="form-group">
            <label for="email">Email użytkownika:</label>
            <input v-model="email" type="email" id="email" required />
          </div>
          <div class="form-group">
            <label for="permission">Uprawnienia:</label>
            <select v-model="permission" id="permission" required>
              <option value="READ">Odczyt</option>
              <option value="WRITE">Zapis</option>
              <option value="ADMIN">Admin</option>
            </select>
          </div>
          <button type="submit">Udostępnij</button>
        </form>
        <p v-if="message">{{ message }}</p>
      </div>
    </section>
  </div>
</template>

<script>
import VueCal from 'vue-cal';
import 'vue-cal/dist/vuecal.css';
import calendarService from '@/services/calendarService';
import eventService from '@/services/eventService';
import sharedCalendarService from '@/services/sharedCalendarService';

export default {
  name: 'CalendarView',
  components: {
    VueCal
  },
  data() {
    return {
      calendar: {},
      view: 'month',
      calendarEvents: [],
      showForm: false,
      newEvent: {
        title: '',
        description: '',
        start: '',
        end: ''
      },
      email: '',
      permission: 'READ',
      message: '',
      calendarId: null,
      canAddEvent: false,
      canShareCalendar: false
    };
  },
  async mounted() {
    this.calendarId = this.$route.params.id;
    this.permission = this.$route.params.permission;
    await this.fetchCalendar();
    await this.fetchEvents();
    this.setPermissions();
  },
  methods: {
    async fetchCalendar() {
      try {
        const response = await calendarService.getCalendar(this.calendarId);
        this.calendar = response.data;
      } catch (error) {
        console.error('Błąd podczas pobierania kalendarza:', error);
      }
    },
    async fetchEvents() {
      try {
        const response = await eventService.getEventsByCalendar(this.calendarId);
        this.calendarEvents = response.data.map(event => {
          if (!event.startTime || !event.endTime) {
            console.error('Wydarzenie ma nieprawidłową datę:', event);
            return null;
          }
          return {
            id: event.id,
            start: new Date(event.startTime),
            end: new Date(event.endTime),
            title: event.title
          };
        }).filter(event => event !== null);
      } catch (error) {
        console.error('Błąd podczas pobierania wydarzeń:', error);
      }
    },
    goBack() {
      this.$router.push('/dashboard');
    },
    goToEventList() {
      this.$router.push({ name: 'EventList', params: { calendarId: this.calendarId } });
    },
    showAddEventForm({ start, end }) {
      if (this.canAddEvent) {
        this.newEvent.start = start;
        this.newEvent.end = end;
        this.showForm = true;
      }
    },
    async addEvent() {
      const event = {
        ...this.newEvent,
        calendarId: this.calendarId,
        startTime: new Date(this.newEvent.start),
        endTime: new Date(this.newEvent.end)
      };
      try {
        const response = await eventService.createEvent(event);
        this.calendarEvents.push({
          id: response.data.id,
          start: new Date(response.data.startTime),
          end: new Date(response.data.endTime),
          title: response.data.title
        });
        this.showForm = false;
        this.newEvent = {
          title: '',
          description: '',
          start: '',
          end: ''
        };
      } catch (error) {
        console.error('Błąd podczas dodawania wydarzenia:', error);
      }
    },
    showEventDetails(event) {
      alert(`Szczegóły wydarzenia: ${event.title}`);
    },
    async shareCalendar() {
      try {
        const userExists = await sharedCalendarService.userExists(this.email);
        if (!userExists) {
          this.message = 'Użytkownik o podanym adresie email nie istnieje';
          return;
        }
        await sharedCalendarService.shareCalendar(this.email, this.calendarId, this.permission);
        this.message = 'Kalendarz został pomyślnie udostępniony!';
      } catch (error) {
        this.message = 'Błąd podczas udostępniania kalendarza: ' + error.message;
      }
    },
    setPermissions() {
      const user = JSON.parse(localStorage.getItem('user'));
      const userId = user.id;
      if (this.calendar.ownerId === userId || this.permission === 'ADMIN' || this.permission === 'WRITE') {
        this.canAddEvent = true;
        this.canShareCalendar = this.permission === 'ADMIN' || this.calendar.ownerId === userId;
      } else {
        this.canAddEvent = false;
        this.canShareCalendar = false;
      }
    }
  }
};
</script>

<style scoped>
.calendar-view {
  padding: 2rem;
  flex-direction: column;
  align-items: center;
  background-color: #333;
  color: #ffffff;
  min-height: 100vh;
  box-sizing: border-box;
}

header {
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

.back-button {
  background-color: #ff6666;
  color: white;
  border: none;
  border-radius: 5px;
  padding: 0.5rem 1rem;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.back-button:hover {
  background-color: #e55b5b;
}

.event-list-button {
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 5px;
  padding: 0.5rem 1rem;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.3s ease;
  margin-right: 5px;
}

.event-list-button:hover {
  background-color: #358c6b;
}

.view-switch {
  margin-bottom: 1rem;
}

.view-switch button {
  margin-right: 0.5rem;
  padding: 0.5rem 1rem;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.add-event-form {
  background-color: #444;
  padding: 1rem;
  border-radius: 8px;
  margin-top: 1rem;
}

.share-calendar-form {
  background-color: #444;
  padding: 1rem;
  border-radius: 8px;
  margin-top: 1rem;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 0.5rem;
  border-radius: 4px;
  border: 1px solid #ddd;
  background-color: #f9f9f9;
}

.add-event-form button,
.share-calendar-form button {
  margin-right: 0.5rem;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.add-event-form button[type="submit"],
.add-event-form button[type="button"],
.share-calendar-form button[type="submit"] {
  background-color: #42b983;
  color: white;
}

.add-event-form button[type="submit"]:hover,
.add-event-form button[type="button"]:hover,
.share-calendar-form button[type="submit"]:hover {
  background-color: #358c6b;
}
</style>
