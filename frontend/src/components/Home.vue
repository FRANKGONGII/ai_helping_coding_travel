<template>
  <div class="dashboard-container">
    <!-- 顶部导航/用户状态区域 -->
    <header class="dashboard-header">
      <div class="logo">旅行计划助手</div>
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

    <!-- 主要内容区域 -->
    <main class="dashboard-main">
      <aside class="sidebar">
        <h2>智能行程规划</h2>
        <form @submit.prevent="generatePlan" class="plan-form">
          <div class="form-group">
            <label for="destination">目的地:</label>
            <input type="text" id="destination" v-model="travelPlan.destination" placeholder="例如：日本东京" required>
          </div>
          <div class="form-group">
            <label for="dates">日期:</label>
            <input type="text" id="dates" v-model="travelPlan.dates" placeholder="例如：5天，2025/12/01 - 2025/12/05">
          </div>
          <div class="form-group">
            <label for="budget">预算:</label>
            <input type="text" id="budget" v-model="travelPlan.budget" placeholder="例如：1万元">
          </div>
          <div class="form-group">
            <label for="travelers">同行人数:</label>
            <input type="number" id="travelers" v-model="travelPlan.travelers" placeholder="例如：2">
          </div>
          <div class="form-group">
            <label for="preferences">旅行偏好:</label>
            <textarea id="preferences" v-model="travelPlan.preferences" rows="4" placeholder="例如：喜欢美食和动漫，带孩子"></textarea>
          </div>
          <button type="submit" class="btn btn-primary">生成旅行计划</button>
        </form>
        <div class="voice-input-section">
          <button class="btn btn-secondary" @click="startVoiceInput">
            <i class="fas fa-microphone"></i> 语音输入
          </button>
          <p v-if="isListening">正在聆听...</p>
          <p v-if="voiceInputResult">识别结果: {{ voiceInputResult }}</p>
          <div v-if="voiceInputResult" class="voice-actions">
            <button class="btn btn-primary" @click="generatePlanFromVoiceResult">使用识别结果生成计划</button>
            <button class="btn btn-tertiary" @click="resetVoiceInput">重新语音输入</button>
          </div>
        </div>
      </aside>

      <section class="content-display">
        <template v-if="aiPlanResult || budgetAnalysis">
          <h2 v-if="budgetAnalysis">预算花费分析</h2>
          <h2 v-else>AI 生成的旅行计划</h2>
          <div class="ai-plan-output">
            <pre v-if="budgetAnalysis" class="budget-details">{{ budgetAnalysis }}</pre>
            <pre v-else class="plan-details">{{ aiPlanResult }}</pre>
          </div>
          <div class="ai-plan-output-actions">
            <button @click="saveTravelPlan" :disabled="isSaving" class="btn btn-primary">
              {{ isSaving ? '保存中...' : '保存旅行计划' }}
            </button>
            <button @click="calculateBudget" :disabled="!travelPlanId || isCalculatingBudget" class="btn btn-primary">
              {{ isCalculatingBudget ? '计算中...' : '计算预算花费' }}
            </button>
          </div>
        </template>
        <div v-else class="no-plan-message">
          <p>在这里查看您的个性化旅行计划。</p>
        </div>
      </section>
    </main>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'Home',
  data() {
    return {
      isLoggedIn: localStorage.getItem('isLoggedIn') === 'true',
      userId: localStorage.getItem('userId') || null,
      travelPlan: {
        destination: '',
        dates: '',
        budget: '',
        travelers: null,
        preferences: ''
      },
      isListening: false,
      voiceInputResult: '',
      aiPlanResult: null, // 存储 AI 生成的旅行计划结果
      isLoading: false, // 新增：加载状态
      error: null, // 新增：错误信息
      isSaving: false, // 新增：保存状态
      travelPlanId: null, // 存储保存成功后的旅行计划ID
      budgetAnalysis: null, // 存储预算分析结果
      isCalculatingBudget: false, // 预算计算状态
    };
  },
  created() {
    // this.checkLoginStatus(); // 已经通过data属性初始化，无需再次检查
  },
  methods: {
    checkLoginStatus() {
      // 实际应用中，这里会检查 localStorage 中的 token 或 Vuex store 中的状态
      this.isLoggedIn = localStorage.getItem('isLoggedIn') === 'true';
      this.userId = localStorage.getItem('userId') || null;
    },
    // login() { // 登录逻辑已移至Login.vue
    //   // 模拟登录逻辑
    //   this.isLoggedIn = true;
    //   this.username = 'TestUser';
    //   alert('登录成功！');
    // },
    logout() {
      localStorage.removeItem('isLoggedIn');
      localStorage.removeItem('userId');
      this.isLoggedIn = false;
      this.userId = null;
      this.aiPlanResult = ''; // 清空AI计划结果
      alert('已登出。');
    },
    viewProfile() {
      alert('查看个人信息功能待实现。');
    },
    async generatePlan() {
      this.isLoading = true; // 开始加载
      this.error = null; // 清除之前的错误
      this.aiPlanResult = ''; // 清空之前的计划
      // 构建问题字符串
      const questionText = `请为我规划一次旅行：目的地是${this.travelPlan.destination}，日期从${this.travelPlan.dates}到${this.travelPlan.dates}，预算${this.travelPlan.budget}元，同行人数${this.travelPlan.travelers}，旅行偏好是${this.travelPlan.preferences}。`;
      const requestBody = { question: questionText };
  
      try {
          this.aiPlanResult = '正在生成旅行计划，请稍候...';
          const response = await axios.post('/api/ai/travelPlan', requestBody, {
              headers: {
                  'Content-Type': 'application/json' // 后端接收的是 @RequestBody String
              },
              timeout: 60000 // 设置请求超时时间为1分钟
          });
          console.log('后端返回的原始数据 (generatePlan):', response.data); // 添加打印语句
          this.aiPlanResult = response.data; // 假设后端返回的TravelPlan对象中有一个details字段包含计划内容
      } catch (error) {
          console.error('生成旅行计划失败:', error);
          this.error = '生成旅行计划失败，请稍后再试。';
          if (axios.isCancel(error)) {
            this.error = '请求已取消。';
          } else if (error.code === 'ECONNABORTED') {
            this.error = '请求超时，请检查网络或稍后再试。';
          } else {
            this.error = '生成旅行计划失败，请稍后再试。';
          }
      } finally {
        this.isLoading = false; // 结束加载
      }
    },
    startVoiceInput() {
      if (!('webkitSpeechRecognition' in window)) {
        alert('您的浏览器不支持语音识别，请尝试使用 Chrome 浏览器。');
        return;
      }
  
      this.voiceInputResult = '请开始说话...';
      this.isListening = true;
      this.aiPlanResult = ''; // 清空之前的AI计划结果
  
      const recognition = new webkitSpeechRecognition();
      recognition.continuous = false;
      recognition.interimResults = false;
      recognition.lang = 'zh-CN'; // 设置为中文
  
      recognition.onresult = (event) => {
        const transcript = event.results[0][0].transcript;
        this.voiceInputResult = transcript;
        this.isListening = false;
      };
  
      recognition.onerror = (event) => {
        console.error('语音识别错误:', event.error);
        this.voiceInputResult = '语音识别失败，请重试。';
        this.isListening = false;
      };
  
      recognition.onend = () => {
        if (this.isListening) { // 如果onresult没有触发，说明没有识别到结果
          this.voiceInputResult = '没有检测到语音输入。';
        }
        this.isListening = false;
      };
  
      recognition.start();
    },
    async generatePlanFromVoiceResult() {
      if (!this.voiceInputResult || this.voiceInputResult === '请开始说话...' || this.voiceInputResult === '语音识别失败，请重试。' || this.voiceInputResult === '没有检测到语音输入。') {
        alert('请先进行语音输入或语音输入结果无效。');
        return;
      }
      this.isLoading = true; // 开始加载
      this.error = null; // 清除之前的错误
      this.aiPlanResult = ''; // 清空之前的计划
      const requestBody = { question: this.voiceInputResult };
  
      try {
          this.aiPlanResult = '正在根据语音结果生成旅行计划，请稍候...';
          const response = await axios.post('/api/ai/travelPlan', requestBody, {
              headers: {
                  'Content-Type': 'application/json' // 后端接收的是 @RequestBody String
              },
              timeout: 60000 // 设置请求超时时间为1分钟
          });
          console.log('后端返回的原始数据 (generatePlanFromVoiceResult):', response.data); // 添加打印语句
          this.aiPlanResult = response.data; // 假设后端返回的TravelPlan对象中有一个details字段包含计划内容
      } catch (error) {
          console.error('根据语音结果生成旅行计划失败:', error);
          this.error = '生成旅行计划失败，请稍后再试。';
          if (axios.isCancel(error)) {
            this.error = '请求已取消。';
          } else if (error.code === 'ECONNABORTED') {
            this.error = '请求超时，请检查网络或稍后再试。';
          } else {
            this.error = '生成旅行计划失败，请稍后再试。';
          }
      } finally {
        this.isLoading = false; // 结束加载
      }
    },
    resetVoiceInput() {
      this.voiceInputResult = '';
      this.aiPlanResult = '';
      this.isListening = false;
    },
    async saveTravelPlan() {
      if (!this.isLoggedIn) {
        alert('请先登录才能保存旅行计划！');
        return;
      }
      this.isSaving = true; // 开始保存
      this.error = null; // 清除之前的错误

      // 构建要保存的旅行计划对象
      const travelPlanToSave = {
        userId: localStorage.getItem('userId'), // 从登录状态获取用户ID
        destination: this.travelPlan.destination || "未知目的地",
        origin: "", // 假设出发地为空，实际应用中应从表单获取
        peopleNum: this.travelPlan.travelers || 1, // 传递数值类型，默认值设为1
        startDate: "2024-01-01", // 假设开始日期，实际应用中应从表单获取
        endDate: "2024-01-05", // 假设结束日期，实际应用中应从表单获取
        budget: this.travelPlan.budget ? parseFloat(this.travelPlan.budget) : 0.0, // 将预算转换为浮点数，并提供默认值
        preferences:(this.travelPlan.preferences || "无偏好"), // 将偏好转换为JSON字符串，并提供默认值
        details: this.aiPlanResult // AI 生成的旅行计划内容
      };

      try {
        const response = await axios.post('/api/travelPlan/save', travelPlanToSave);
        this.isSaving = false;
        alert('旅行计划保存成功！');
        this.travelPlanId = response.data.id; // Assuming the backend returns the saved travel plan with an 'id'
      } catch (error) {
        console.error('保存旅行计划失败:', error);
        this.error = '保存旅行计划失败，请稍后再试。';
      } finally {
        this.isSaving = false; // 结束保存
      }
    },
    // Add budget calculation method
    async calculateBudget () {
      if (!this.travelPlanId) return
      this.isCalculatingBudget = true
      this.error = null
      try {
        const response = await axios.post('/api/ai/analyze-budget', null, {
          params: { travelPlanId: this.travelPlanId }
        })
        this.budgetAnalysis = response.data
      } catch (error) {
        console.error('预算计算失败:', error)
        this.error = '预算计算失败，请稍后重试'
      } finally {
        this.isCalculatingBudget = false
      }
    },
  }
};
</script>

<style scoped>
.dashboard-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f0f2f5;
  font-family: 'Arial', sans-serif;
  color: #333;
}

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

.user-actions .nav-link {
  margin-left: 20px;
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
}

.dashboard-main {
  display: flex;
  flex: 1;
  padding: 20px;
  gap: 20px;
  justify-content: center; /* 使内容居中 */
}

.sidebar {
  flex: 0 0 350px; /* 固定宽度 */
  background-color: #fff;
  padding: 25px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.sidebar h2 {
  color: #2c3e50;
  margin-bottom: 25px;
  font-size: 1.6em;
  text-align: center;
}

.plan-form .form-group {
  margin-bottom: 18px;
}

.plan-form label {
  display: block;
  margin-bottom: 8px;
  font-weight: bold;
  color: #555;
}

.plan-form input[type="text"],
.plan-form input[type="number"],
.plan-form textarea {
  width: calc(100% - 20px);
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 1em;
  transition: border-color 0.3s ease;
}

.plan-form input:focus,
.plan-form textarea:focus {
  border-color: #42b983;
  outline: none;
}

.plan-form textarea {
  resize: vertical;
}

.btn {
  display: block;
  width: 100%;
  padding: 12px 20px;
  border-radius: 5px;
  text-decoration: none;
  font-weight: bold;
  transition: background-color 0.3s ease, transform 0.2s ease;
  font-size: 1.1em;
  cursor: pointer;
  text-align: center;
  margin-top: 20px;
}

.plan-form button[type="submit"] {
  display: block;
  width: 100%;
  padding: 12px 20px;
  border-radius: 5px;
  text-decoration: none;
  font-weight: bold;
  transition: background-color 0.3s ease, transform 0.2s ease;
  font-size: 1.1em;
  cursor: pointer;
  text-align: center;
  margin-top: 20px;
}

.plan-form button[type="submit"]:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.voice-actions button {
  margin-top: 10px;
}

.loading-indicator,
.error-message {
  margin-top: 20px;
  padding: 15px;
  border-radius: 5px;
  text-align: center;
  font-weight: bold;
}

.loading-indicator {
  background-color: #e0f7fa;
  color: #00796b;
}

.error-message {
  background-color: #ffebee;
  color: #c62828;
}

.btn-primary {
  background-color: #42b983;
  color: #fff;
  border: 1px solid #42b983;
}

.btn-primary:hover {
  background-color: #369f75;
  transform: translateY(-2px);
}

.btn-secondary {
  background-color: #2c3e50;
  color: #fff;
  border: 1px solid #2c3e50;
  margin-top: 15px;
}

.btn-secondary:hover {
  background-color: #1a242f;
  transform: translateY(-2px);
}

.voice-input-section {
  margin-top: 30px;
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.voice-input-section p {
  margin-top: 10px;
  font-style: italic;
  color: #666;
}

.content-display {
  flex: 1;
  max-width: 800px; /* 限制最大宽度 */
  background-color: #fff;
  padding: 25px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.content-display h2 {
  color: #2c3e50;
  margin-bottom: 25px;
  font-size: 1.6em;
  text-align: center;
}

.ai-plan-output {
  background-color: #f9f9f9;
  border: 1px solid #eee;
  border-radius: 5px;
  padding: 15px;
  min-height: 200px;
  max-height: 400px; /* 设置最大高度 */
  overflow-y: auto; /* 允许垂直滚动 */
  white-space: pre-wrap; /* 保留空白符和换行 */
  word-wrap: break-word; /* 自动换行 */
}

.no-plan-message {
  text-align: center;
  color: #888;
  padding: 50px;
}
</style>

.ai-plan-output-actions {
  display: flex;
  gap: 15px; /* 按钮之间的间距 */
  margin-top: 20px;
  justify-content: center; /* 按钮居中显示 */
}

.ai-plan-output-actions .btn {
  width: auto; /* 按钮宽度自适应内容 */
  flex-grow: 1; /* 按钮在 flex 容器中平均分配空间 */
  margin-top: 0; /* 移除按钮默认的 margin-top */
}
