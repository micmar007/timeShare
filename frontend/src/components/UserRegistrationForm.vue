<template>
  <div class="registration-form">
    <h3>Zarejestruj się</h3>
    <form @submit.prevent="registerUser">
      <div class="form-group">
        <label for="username">Nazwa użytkownika:</label>
        <input type="text" id="username" v-model="username" required />
      </div>
      <div class="form-group">
        <label for="email">Email:</label>
        <input type="email" id="email" v-model="email" required />
      </div>
      <div class="form-group">
        <label for="password">Hasło:</label>
        <input type="password" id="password" v-model="password" required />
      </div>
      <div class="form-group">
        <label for="confirmPassword">Potwierdź hasło:</label>
        <input type="password" id="confirmPassword" v-model="confirmPassword" required />
      </div>
      <button type="submit">Zarejestruj się</button>
    </form>
    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      username: '',
      email: '',
      password: '',
      confirmPassword: '',
      errorMessage: ''
    };
  },
  methods: {
    async registerUser() {
      if (this.password !== this.confirmPassword) {
        this.errorMessage = 'Hasła muszą być takie same.';
        return;
      }

      try {
        const response = await axios.post('http://localhost:8080/api/auth/register', {
          username: this.username,
          email: this.email,
          password: this.password
        });

        console.log('Użytkownik zarejestrowany:', response.data);

        await this.loginUser(this.username, this.password);

        this.$router.push('/dashboard');
      } catch (error) {
        console.error('Błąd podczas rejestracji:', error);
        this.errorMessage = error.response?.data || 'Nie udało się zarejestrować użytkownika.';
      }
    },
    async loginUser() {
      try {
        const response = await axios.post('http://localhost:8080/api/auth/login', {
          username: this.username,
          password: this.password
        });

        const token = response.data.token;
        const userId = response.data.userId;
        const role = response.data.role;

        localStorage.setItem('authToken', token);
        localStorage.setItem('user', JSON.stringify({ id: userId, username: this.username, role: role }));



        axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;

        this.$router.push('/dashboard');
      } catch (error) {
        console.error('Błąd podczas logowania:', error);
        this.errorMessage = 'Nie udało się zalogować. Spróbuj ponownie.';
      }
    }

  }
};
</script>

<style scoped>
.registration-form {
  max-width: 400px;
  margin: 0 auto;
}

.registration-form h3 {
  text-align: center;
  color: #333;
  margin-bottom: 1.5rem;
  font-size: 1.5rem;
  font-weight: 600;
}

.form-group {
  margin-bottom: 1.5rem;
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
  padding: 0.75rem;
  box-sizing: border-box;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.registration-form button {
  width: 100%;
  padding: 0.75rem;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 600;
  transition: background-color 0.3s ease;
}

.registration-form button:hover {
  background-color: #358c6b;
}

.error {
  margin-top: 1rem;
  color: red;
  text-align: center;
}
</style>
