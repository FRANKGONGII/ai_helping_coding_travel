<template>
  <div class="login">
    <h1>登录</h1>
    <form @submit.prevent="login">
      <div>
        <label for="username">用户名:</label>
        <input type="text" id="username" v-model="form.username" required />
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
        username: '',
        password: ''
      }
    };
  },
  methods: {
    async login() {
      try {
        const response = await axios.post('/api/auth/login', this.form);
        localStorage.setItem('isLoggedIn', 'true');
        localStorage.setItem('userId', response.data); // 后端直接返回 userId
        console.log(response.data)
        alert('登录成功！' + localStorage.getItem('userId'));
        this.$router.push('/');
        this.$bus.$emit('login-status-changed'); // 登录成功后触发事件
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