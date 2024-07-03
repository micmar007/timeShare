<template>
  <div class="event-list">
    <header>
      <h2>Lista wydarzeń kalendarza</h2>
      <button @click="goBack" class="back-button">Powrót</button>
    </header>
    <ul>
      <li v-for="event in events" :key="event.id">
        <div>
          <strong>{{ event.title }}</strong> ({{ formatDate(event.startTime) }} - {{ formatDate(event.endTime) }})
        </div>
        <div>
          <button @click="editEvent(event)">Edytuj</button>
          <button @click="deleteEvent(event.id)">Usuń</button>
        </div>
      </li>
    </ul>
    <div v-if="showForm" class="edit-event-form">
      <h3>Edytuj wydarzenie</h3>
      <form @submit.prevent="updateEvent">
        <div class="form-group">
          <label for="title">Tytuł:</label>
          <input type="text" id="title" v-model="currentEvent.title" required />
        </div>
        <div class="form-group">
          <label for="description">Opis:</label>
          <textarea id="description" v-model="currentEvent.description" required></textarea>
        </div>
        <div class="form-group">
          <label for="start">Czas rozpoczęcia:</label>
          <input type="datetime-local" id="start" v-model="currentEvent.startTime" required />
        </div>
        <div class="form-group">
          <label for="end">Czas zakończenia:</label>
          <input type="datetime-local" id="end" v-model="currentEvent.endTime" required />
        </div>
        <button type="submit">Zaktualizuj</button>
        <button type="button" @click="showForm = false">Anuluj</button>
      </form>
    </div>
  </div>
</template>

<script>
import eventService from '@/services/eventService';

export default {
  name: 'EventList',
  data() {
    return {
      events: [],
      showForm: false,
      currentEvent: {
        id: '',
        title: '',
        description: '',
        startTime: '',
        endTime: ''
      }
    };
  },
  async mounted() {
    this.calendarId = this.$route.params.calendarId;
    await this.fetchEvents();
  },
  methods: {
    async fetchEvents() {
      try {
        const response = await eventService.getEventsByCalendar(this.calendarId);
        this.events = response.data.map(event => ({
          ...event,
          startTime: new Date(event.startTime),
          endTime: new Date(event.endTime)
        }));
      } catch (error) {
        console.error('Błąd podczas pobierania wydarzeń:', error);
      }
    },
    editEvent(event) {
      this.currentEvent = {
        ...event,
        startTime: event.startTime.toISOString().slice(0, 16),
        endTime: event.endTime.toISOString().slice(0, 16)
      };
      this.showForm = true;
    },
    async updateEvent() {
      const updatedEvent = {
        ...this.currentEvent,
        startTime: new Date(this.currentEvent.startTime),
        endTime: new Date(this.currentEvent.endTime)
      };
      try {
        await eventService.updateEvent(this.currentEvent.id, updatedEvent);
        await this.fetchEvents();
        this.showForm = false;
      } catch (error) {
        console.error('Błąd podczas aktualizacji wydarzenia:', error);
      }
    },
    async deleteEvent(eventId) {
      if (confirm('Czy na pewno chcesz usunąć to wydarzenie?')) {
        try {
          await eventService.deleteEvent(eventId);
          await this.fetchEvents();
        } catch (error) {
          console.error('Błąd podczas usuwania wydarzenia:', error);
        }
      }
    },
    formatDate(date) {
      return new Date(date).toLocaleString();
    },
    goBack() {
      this.$router.push({ name: 'CalendarView', params: { id: this.calendarId } });
    }
  }
};
</script>

<style scoped>
.event-list {
  padding: 2rem;
  background-color: #333;
  color: #fff;
  border-radius: 8px;
  max-width: 600px;
  margin: 2rem auto;
}

header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.event-list h2 {
  color: #42b983;
}

ul {
  list-style-type: none;
  padding: 0;
}

li {
  background-color: #444;
  margin: 0.5rem 0;
  padding: 0.5rem;
  border-radius: 4px;
}

li div {
  display: flex;
  justify-content: space-between;
}

button {
  background-color: #42b983;
  border: none;
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background-color: #358c6b;
}

.back-button {
  background-color: #ff6666;
  color: white;
  border: none;
  border-radius: 5px;
  padding: 0.5rem 1rem;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.back-button:hover {
  background-color: #e55b5b;
}

.edit-event-form {
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
.form-group textarea {
  width: 100%;
  padding: 0.5rem;
  border-radius: 4px;
  border: 1px solid #ddd;
}
</style>
