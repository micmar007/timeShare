<template>
  <div class="add-calendar-form">
    <h3>Dodaj nowy kalendarz</h3>
    <form @submit.prevent="addCalendar">
      <div class="form-group">
        <label for="name">Nazwa kalendarza:</label>
        <input type="text" id="name" v-model="name" required />
      </div>
      <div class="form-group">
        <label for="description">Opis kalendarza:</label>
        <textarea id="description" v-model="description" required></textarea>
      </div>
      <button type="submit">Dodaj kalendarz</button>
    </form>
    <p v-if="message" class="message">{{ message }}</p>
  </div>
</template>

<script>
import calendarService from '../services/calendarService.js';

export default {
  data() {
    return {
      name: '',
      description: '',
      message: ''
    };
  },
  methods: {
    async addCalendar() {
      try {
        const user = JSON.parse(localStorage.getItem('user'));
        const ownerId = user.id;
        const token = localStorage.getItem('authToken');

        if (!token) {
          this.message = 'Brak tokenu autoryzacyjnego. Upewnij się, że jesteś zalogowany.';
          return;
        }

        if (!ownerId) {
          this.message = 'Brak ID użytkownika. Upewnij się, że jesteś zalogowany.';
          return;
        }

        const config = {
          headers: {
            Authorization: `Bearer ${token}`
          }
        };

        const calendar = {
          name: this.name,
          description: this.description,
          ownerId: parseInt(ownerId)
        };

        const response = await calendarService.createCalendar(calendar);

        this.message = 'Kalendarz został pomyślnie dodany!';
        console.log('Dodano kalendarz:', response.data);

        this.name = '';
        this.description = '';

        this.$emit('calendar-added', response.data);
      } catch (error) {
        console.error('Błąd podczas dodawania kalendarza:', error);
        this.message = 'Nie udało się dodać kalendarza. Spróbuj ponownie.';
      }
    }
  }
};
</script>

<style scoped>
.add-calendar-form {
  max-width: 300px;
  margin: 2rem auto;
  padding: 1rem;
  background-color: #ffffff;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid #e6e6e6;
}

.add-calendar-form h3 {
  text-align: center;
  color: #333;
  margin-bottom: 1rem;
  font-size: 1.2rem;
  font-weight: 600;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: #555;
  font-weight: 500;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 0.5rem;
  box-sizing: border-box;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 0.9rem;
  background-color: #f9f9f9;
}

.form-group input:focus,
.form-group textarea:focus {
  border-color: #42b983;
  outline: none;
  background-color: #ffffff;
}

.add-calendar-form button {
  width: 100%;
  padding: 0.5rem;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 600;
  transition: background-color 0.3s ease;
}

.add-calendar-form button:hover {
  background-color: #358c6b;
}

.message {
  margin-top: 1rem;
  color: #42b983;
  text-align: center;
  font-weight: 500;
}
</style>
