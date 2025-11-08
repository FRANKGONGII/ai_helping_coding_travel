<template>
  <div class="home-container">
    <div class="home-content">
      <h1>欢迎来到旅行计划应用！</h1>
      <p>开始您的精彩旅程，轻松规划，尽情探索。</p>
      <div class="button-group">
        <router-link to="/register" class="btn btn-primary">注册</router-link>
        <router-link to="/login" class="btn btn-secondary">登录</router-link>
        <router-link to="/guest-dashboard" class="btn btn-tertiary">直接开始</router-link>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Home'
}
</script>

<style scoped>
.home-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh; /* 使容器至少占满整个视口高度 */
  background-color: #f0f2f5; /* 浅灰色背景 */
  color: #333; /* 深色字体 */
  font-family: 'Arial', sans-serif;
}

.home-content {
  background-color: #fff;
  padding: 40px 60px;
  border-radius: 10px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1); /* 柔和的阴影 */
  text-align: center;
  max-width: 500px;
  width: 100%;
}

h1 {
  color: #2c3e50;
  margin-bottom: 20px;
  font-size: 2.5em;
}

p {
  font-size: 1.2em;
  line-height: 1.6;
  margin-bottom: 30px;
}

.button-group {
  display: flex;
  justify-content: center;
  gap: 20px; /* 按钮之间的间距 */
}

.btn {
  display: inline-block;
  padding: 12px 25px;
  border-radius: 5px;
  text-decoration: none;
  font-weight: bold;
  transition: background-color 0.3s ease, transform 0.2s ease;
  font-size: 1.1em;
}

.btn-primary {
  background-color: #42b983; /* Vue 绿色 */
  color: #fff;
  border: 1px solid #42b983;
}

.btn-primary:hover {
  background-color: #369f75;
  transform: translateY(-2px);
}

.btn-secondary {
  background-color: #2c3e50; /* 深灰色 */
  color: #fff;
  border: 1px solid #2c3e50;
}

.btn-secondary:hover {
  background-color: #1a242f;
  transform: translateY(-2px);
}

.btn-tertiary {
  background-color: #6c757d; /* 灰色 */
  color: #fff;
  border: 1px solid #6c757d;
}

.btn-tertiary:hover {
  background-color: #5a6268;
  transform: translateY(-2px);
}
</style>