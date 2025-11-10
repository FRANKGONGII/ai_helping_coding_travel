<template>
  <header class="dashboard-header">
    <div class="logo">旅行计划助手</div>
    <nav class="main-nav">
      <router-link to="/" class="nav-link">行程规划</router-link>
      <router-link to="/expenses" class="nav-link">旅游开销</router-link>
      <router-link to="/map" class="nav-link">地图页面交互</router-link>
    </nav>
    <nav class="user-actions">
      <template v-if="isLoggedIn">
        <router-link to="/profile" class="nav-link">个人信息</router-link>
        <button @click="logout" class="nav-link logout-btn">登出</button>
      </template>
      <template v-else>
        <router-link to="/login" class="nav-link">登录</router-link>
        <router-link to="/register" class="nav-link">注册</router-link>
      </template>
    </nav>
  </header>
</template>

<script>
export default {
  name: 'NavBar',
  data() {
    return {
      isLoggedIn: localStorage.getItem('isLoggedIn') === 'true',
      userId: localStorage.getItem('userId') || null,
    };
  },
  methods: {
    logout() {
      localStorage.removeItem('isLoggedIn');
      localStorage.removeItem('userId');
      this.isLoggedIn = false;
      this.userId = null;
      alert('已登出。');
      this.$router.push('/login'); // 登出后跳转到登录页
      this.$bus.$emit('login-status-changed'); // 登出后也触发事件
    },
    updateLoginStatus() {
      this.isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';
      this.userId = localStorage.getItem('userId') || null;
    }
  },
  created() {
    this.$bus.$on('login-status-changed', this.updateLoginStatus);
  },
  beforeDestroy() {
    this.$bus.$off('login-status-changed', this.updateLoginStatus);
  }
};
</script>

<style scoped>
.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 30px;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.logo {
  font-size: 1.8em;
  font-weight: bold;
  color: #2c3e50;
}

.main-nav {
  display: flex;
  gap: 20px;
}

.main-nav .nav-link {
  color: #42b983;
  text-decoration: none;
  font-weight: bold;
  transition: color 0.3s ease;
}

.main-nav .nav-link:hover {
  color: #369f75;
}

.user-actions {
  display: flex;
  gap: 20px;
}

.user-actions .nav-link {
  color: #42b983;
  text-decoration: none;
  font-weight: bold;
  transition: color 0.3s ease;
}

.user-actions .nav-link:hover {
  color: #369f75;
}

.user-actions .logout-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1em;
  color: #42b983;
  font-weight: bold;
  padding: 0;
}

.user-actions .logout-btn:hover {
  color: #369f75;
}
</style>