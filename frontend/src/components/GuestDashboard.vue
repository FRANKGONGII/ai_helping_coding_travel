<template>
  <div class="guest-dashboard">
    <h1>访客仪表盘</h1>
    <p>您现在可以作为访客浏览旅行计划功能。</p>
    <!-- 这里可以添加一些访客可以访问的功能链接 -->
  </div>
</template>

<script>
export default {
  name: 'GuestDashboard'
}
</script>

<style scoped>
.guest-dashboard {
  text-align: center;
  margin-top: 50px;
  font-family: 'Arial', sans-serif;
}

h1 {
  color: #2c3e50;
  margin-bottom: 20px;
}

p {
  font-size: 1.1em;
  color: #555;
}
</style>