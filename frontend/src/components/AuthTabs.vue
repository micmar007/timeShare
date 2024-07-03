<template>
  <div class="auth-tabs">
    <div class="tabs">
      <button :class="{ active: currentTab === 'login' }" @click="currentTab = 'login'">Logowanie</button>
      <button :class="{ active: currentTab === 'register' }" @click="currentTab = 'register'">Rejestracja</button>
    </div>
    <div class="tab-content">
      <LoginForm v-if="currentTab === 'login'" />
      <UserRegistrationForm v-if="currentTab === 'register'" />
    </div>
  </div>
</template>

<script>
import LoginForm from './LoginForm.vue';
import UserRegistrationForm from './UserRegistrationForm.vue';

export default {
  components: {
    LoginForm,
    UserRegistrationForm
  },
  data() {
    return {
      currentTab: 'login'
    };
  }
};
</script>

<style scoped>
.auth-tabs {
  max-width: 500px;
  margin: 2rem auto;
  background-color: #fff;
  padding: 2rem;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid #e6e6e6;
}

.tabs {
  display: flex;
  justify-content: space-between;
  margin-bottom: 1.5rem;
}

.tabs button {
  width: 50%;
  padding: 0.75rem;
  background: none;
  border: none;
  font-size: 1.1rem;
  font-weight: bold;
  cursor: pointer;
  transition: color 0.3s, border-bottom 0.3s;
}

.tabs button.active {
  color: #42b983;
  border-bottom: 2px solid #42b983;
}

.tabs button:not(.active):hover {
  color: #358c6b;
}

.tab-content {
  display: flex;
  flex-direction: column;
}
</style>
