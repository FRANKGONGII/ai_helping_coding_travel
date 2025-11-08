<template>
  <div class="login">
    <h1>登录</h1>
    <form @submit.prevent="login">
      <div>
        <label for="email">邮箱:</label>
        <input type="email" id="email" v-model="form.email" required />
      </div>
      <div>
        <label for="password">密码:</label>
        <input type="password" id="password" v-model="form.password" required />
      </div>
      <button type="submit">登录</button>
    </form>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'Login',
  data() {
    return {
      form: {
        email: '',
        password: ''
      }
    };
  },
  methods: {
    async login() {
      try {
        const response = await axios.post('/api/auth/login', this.form);
        alert('登录成功！');
        this.$router.push('/dashboard');
      } catch (error) {
        alert('登录失败，请检查邮箱或密码！');
        console.error(error);
      }
    }
  }
};
</script>

<style scoped>
.login {
  max-width: 400px;
  margin: 0 auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 5px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}
form div {
  margin-bottom: 15px;
}
label {
  display: block;
  margin-bottom: 5px;
}
input {
  width: 100%;
  padding: 8px;
  box-sizing: border-box;
}
button {
  width: 100%;
  padding: 10px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
button:hover {
  background-color: #369f6e;
}
</style>