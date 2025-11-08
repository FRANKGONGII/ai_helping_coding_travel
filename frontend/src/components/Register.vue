<template>
  <div class="register">
    <h1>注册</h1>
    <form @submit.prevent="register">
      <div>
        <label for="username">用户名:</label>
        <input type="text" id="username" v-model="form.username" required />
      </div>
      <div>
        <label for="email">邮箱:</label>
        <input type="email" id="email" v-model="form.email" required />
      </div>
      <div>
        <label for="password">密码:</label>
        <input type="password" id="password" v-model="form.password" required />
      </div>
      <button type="submit">注册</button>
    </form>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'Register',
  data() {
    return {
      form: {
        username: '',
        email: '',
        password: ''
      }
    };
  },
  methods: {
    async register() {
      try {
        const response = await axios.post('/api/auth/register', this.form);
        alert('注册成功！请登录。');
        this.$router.push('/login');
      } catch (error) {
        alert('注册失败，请重试！');
        console.error(error);
      }
    }
  }
};
</script>

<style scoped>
.register {
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