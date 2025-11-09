<template>
  <div class="expense-tracker-container">
    <!-- <nav class="main-nav">
      <router-link to="/">行程规划</router-link>
      <router-link to="/expenses">旅游开销</router-link>
    </nav> -->
    <h1>旅行记录开销</h1>
    <div class="content-wrapper">
      <div class="left-panel">
        <h2>我的旅行计划</h2>
        <button @click="showAddTravelPlanForm = true" class="add-button">新增旅行计划</button>

        <div v-if="showAddTravelPlanForm" class="add-travel-plan-form">
          <h4>{{ editingTravelPlan ? '编辑旅行计划' : '新增旅行计划' }}</h4>
          <input type="text" v-model="newTravelPlan.destination" placeholder="目的地" />
          <input type="text" v-model="newTravelPlan.origin" placeholder="出发地" />
          <input type="number" v-model="newTravelPlan.peopleNum" placeholder="人数" />
          <input type="date" v-model="newTravelPlan.startDate" placeholder="开始日期" />
          <input type="date" v-model="newTravelPlan.endDate" placeholder="结束日期" />
          <input type="text" v-model="newTravelPlan.preferences" placeholder="偏好" />
          <button @click="saveTravelPlan" class="save-button">保存</button>
          <button @click="showAddTravelPlanForm = false; editingTravelPlan = null; resetNewTravelPlanForm();" class="cancel-button">取消</button>
        </div>

        <div v-if="!isLoggedIn" class="login-prompt">
          <p>请登录以查看您的旅行计划。</p>
          <router-link to="/login">点击此处登录</router-link>
        </div>
        <div v-else-if="travelPlans.length === 0">
          <p>您还没有任何旅行计划。</p>
        </div>
        <div v-else class="travel-plan-list">
          <div
            v-for="plan in travelPlans"
            :key="plan.id"
            :class="['travel-plan-card', { selected: selectedTravelPlan && selectedTravelPlan.id === plan.id }]"
            @click="selectTravelPlan(plan)"
          >
            <h3>{{ plan.name }}</h3>
            <p>目的地: {{ plan.destination }}</p>
            <p>日期: {{ plan.startDate }} - {{ plan.endDate }}</p>
            <button @click.stop="editTravelPlan(plan)" class="edit-button">编辑</button>
            <button @click.stop="deleteTravelPlan(plan.id)" class="delete-button">删除</button>
          </div>
        </div>
      </div>
      <div class="right-panel">
        <h2>旅行开销详情</h2>
        <p v-if="!selectedTravelPlan">请选择一个旅行计划查看其开销记录。</p>
        <div v-else>
          <h3>{{ selectedTravelPlan.name }} 的开销记录</h3>
          <div class="expense-actions">
            <button @click="showAddRecordForm = true" class="add-button">新增开销</button>
          </div>

          <div v-if="showAddRecordForm" class="add-record-form">
            <h4>新增开销记录</h4>
            <div class="input-with-voice">
              <input type="text" v-model="newRecord.content" placeholder="描述" />
              <button @click="startVoiceInput" :disabled="isRecording">{{ isRecording ? '正在录音...' : '语音输入' }}</button>
            </div>
            <input type="number" v-model="newRecord.money" placeholder="金额" />
            <input type="datetime-local" v-model="newRecord.consumptionTime" placeholder="消费时间" />
            <button @click="addExpenseRecord" class="save-button">保存</button>
            <button @click="showAddRecordForm = false" class="cancel-button">取消</button>
          </div>

          <div v-if="expenseRecords.length === 0">
            <p>该旅行计划暂无开销记录。</p>
          </div>
          <div v-else class="expense-record-list">
            <div v-for="record in expenseRecords" :key="record.id" class="expense-record-card">
              <h4>{{ record.content }}</h4>
              <p>金额: {{ record.money }}</p>
              <p>消费时间: {{ record.consumptionTime }}</p>
              <button @click="deleteExpenseRecord(record.id)" class="delete-button">删除</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'ExpenseTracker',
  data() {
    return {
      travelPlans: [],
      isLoggedIn: localStorage.getItem('isLoggedIn') === 'true', // 从localStorage获取登录状态
      userId: localStorage.getItem('userId') || null, // 从localStorage获取用户ID
      selectedTravelPlan: null,
      expenseRecords: [],
      showAddRecordForm: false,
      newRecord: {
        content: '',
        money: 0,
        consumptionTime: new Date().toISOString().slice(0, 19),
      },
      isRecording: false, // 新增录音状态
      showAddTravelPlanForm: false, // 控制新增旅行计划表单的显示
      newTravelPlan: {
        destination: '',
        origin: '',
        peopleNum: 1,
        startDate: null,
        endDate: null,
        budget: 0,
        preferences: '',
        details: '',
      },
      editingTravelPlan: null, // 存储正在编辑的旅行计划
    };
  },
  created() {
    // this.checkLoginStatus(); // 登录状态现在由NavBar管理，无需在此处检查
    if (this.isLoggedIn) {
      this.fetchTravelPlans();
    }
  },
  methods: {
    async fetchTravelPlans() {
      if (!this.userId) return;
      try {
        const response = await axios.get(`/api/travelPlan/user/${this.userId}`);
        this.travelPlans = response.data;
      } catch (error) {
        console.error('Error fetching travel plans:', error);
      }
    },
    async saveTravelPlan() {
      if (!this.userId) {
        alert('请先登录！');
        return;
      }

      try {
        if (this.editingTravelPlan) {
          // 更新现有旅行计划
          const planToUpdate = {
            ...this.newTravelPlan,
            peopleNum: Number(this.newTravelPlan.peopleNum), // 确保 peopleNum 是数字类型
          };
          const response = await axios.put(`/api/travelPlan/${planToUpdate.id}`, planToUpdate);
          const index = this.travelPlans.findIndex(plan => plan.id === response.data.id);
          if (index !== -1) {
            this.travelPlans.splice(index, 1, response.data);
          }
          this.editingTravelPlan = null;
        } else {
          // 新增旅行计划
          const planToAdd = {
            ...this.newTravelPlan,
            userId: this.userId,
            peopleNum: Number(this.newTravelPlan.peopleNum), // 确保 peopleNum 是数字类型
          };
          const response = await axios.post('/api/travelPlan', planToAdd, {
            params: { aiGeneratedResult: '' }
          });
          this.travelPlans.push(response.data);
        }
        this.showAddTravelPlanForm = false;
        this.resetNewTravelPlanForm();
      } catch (error) {
        console.error('Error saving travel plan:', error);
        alert('保存旅行计划失败。');
      }
    },
    async selectTravelPlan(plan) {
      this.selectedTravelPlan = plan;
      this.expenseRecords = [];
      if (plan && plan.id) {
        try {
          const response = await axios.get(`/api/travelPlan/record/plan/${plan.id}`);
          this.expenseRecords = response.data;
        } catch (error) {
          console.error(`Error fetching expense records for plan ${plan.id}:`, error);
        }
      }
    },
    async addExpenseRecord() {
      if (!this.selectedTravelPlan || !this.selectedTravelPlan.id) {
        alert('请先选择一个旅行计划。');
        return;
      }
      try {
        const recordToAdd = {
          ...this.newRecord,
          travelPlanId: this.selectedTravelPlan.id,
        };
        const response = await axios.post('/api/travelPlan/record', recordToAdd);
        this.expenseRecords.push(response.data);
        this.showAddRecordForm = false;
        this.resetNewRecordForm();
      } catch (error) {
        console.error('Error adding expense record:', error);
        alert('添加开销记录失败。');
      }
    },
    async deleteExpenseRecord(recordId) {
      if (!confirm('确定要删除这条开销记录吗？')) return;
      try {
        await axios.delete(`/api/travelPlan/record/${recordId}`);
        this.expenseRecords = this.expenseRecords.filter(record => record.id !== recordId);
      } catch (error) {
        console.error('Error deleting expense record:', error);
        alert('删除开销记录失败。');
      }
    },
    resetNewRecordForm() {
      this.newRecord = {
        content: '',
        money: 0,
        consumptionTime: new Date().toISOString().slice(0, 19),
      };
    },
    resetNewTravelPlanForm() {
      this.newTravelPlan = {
        destination: '',
        origin: '',
        peopleNum: 1,
        startDate: null,
        endDate: null,
        budget: 0,
        preferences: '',
        details: '',
      };
    },
    async deleteTravelPlan(planId) {
      if (!confirm('确定要删除这个旅行计划吗？')) return;
      try {
        await axios.delete(`/api/travelPlan/${planId}`);
        this.travelPlans = this.travelPlans.filter(plan => plan.id !== planId);
        if (this.selectedTravelPlan && this.selectedTravelPlan.id === planId) {
          this.selectedTravelPlan = null;
          this.expenseRecords = [];
        }
      } catch (error) {
        console.error('Error deleting travel plan:', error);
        alert('删除旅行计划失败。'  + planId + (error.response ? error.response.data : error.message));
      }
    },
    editTravelPlan(plan) {
      // 复制一份，避免直接修改原始数据
      const planToEdit = { ...plan };

      // 格式化日期为 YYYY-MM-DD，以适应 input type="date"
      if (planToEdit.startDate) {
        planToEdit.startDate = new Date(planToEdit.startDate).toISOString().slice(0, 10);
      }
      if (planToEdit.endDate) {
        planToEdit.endDate = new Date(planToEdit.endDate).toISOString().slice(0, 10);
      }

      this.editingTravelPlan = planToEdit;
      // 将编辑中的旅行计划数据填充到 newTravelPlan 中，以便表单显示
      this.newTravelPlan = { ...planToEdit };
      this.showAddTravelPlanForm = true;
    },
    startVoiceInput() {
      if (!('webkitSpeechRecognition' in window)) {
        alert('抱歉，您的浏览器不支持语音识别。请使用 Chrome 浏览器。');
        return;
      }

      const recognition = new webkitSpeechRecognition();
      recognition.lang = 'zh-CN'; // 设置识别语言为中文
      recognition.interimResults = false; // 只返回最终结果
      recognition.maxAlternatives = 1; // 只返回一个最有可能的结果

      recognition.onstart = () => {
        this.isRecording = true;
        console.log('语音识别开始...');
      };

      recognition.onresult = (event) => {
        const transcript = event.results[0][0].transcript;
        this.newRecord.content = transcript;
        console.log('识别结果:', transcript);
      };

      recognition.onerror = (event) => {
        this.isRecording = false;
        console.error('语音识别错误:', event.error);
        alert('语音识别出错，请重试。');
      };

      recognition.onend = () => {
        this.isRecording = false;
        console.log('语音识别结束。');
      };

      recognition.start();
    },
  },
}
</script>

<style scoped>
.expense-tracker-container {
  display: flex;
  flex-direction: column;
  height: 100vh; /* Full viewport height */
  padding: 20px;
  text-align: center;
}

.main-nav a {
  display: inline-block;
  padding: 10px 20px;
  background-color: #42b983; /* Green background */
  color: white; /* White text */
  text-decoration: none;
  border-radius: 5px;
  font-weight: bold;
  transition: background-color 0.3s ease;
}

.main-nav a:hover {
  background-color: #369f75; /* Darker green on hover */
} */

.content-wrapper {
  display: flex;
  flex: 1; /* Take remaining space */
  gap: 20px;
  margin-top: 20px;
}

.left-panel,
.right-panel {
  flex: 1;
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 20px;
  text-align: left;
  overflow-y: auto;
}

.left-panel {
  background-color: #f9f9f9;
}

.right-panel {
  background-color: #ffffff;
}

.login-prompt {
  padding: 20px;
  color: #555;
  font-size: 1.1em;
}

.travel-plan-card {
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.travel-plan-card:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.travel-plan-card.selected {
  border-color: #42b983;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.travel-plan-card h3 {
  margin-top: 0;
  color: #333;
}

.travel-plan-card p {
  color: #666;
  font-size: 0.9em;
}

.expense-record-card {
  background-color: #f0f8ff; /* Light blue background */
  border: 1px solid #add8e6;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 10px;
  text-align: left;
}

.expense-record-card h4 {
  margin-top: 0;
  color: #2f4f4f;
}

.expense-record-card p {
  color: #4682b4;
  font-size: 0.9em;
}

.expense-actions {
  margin-bottom: 15px;
  text-align: right;
}

.add-button {
  background-color: #28a745; /* Green */
  color: white;
  border: none;
  padding: 8px 15px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 0.9em;
  transition: background-color 0.3s ease;
}

.add-button:hover {
  background-color: #218838;
}

.delete-button {
  background-color: #dc3545; /* Red */
  color: white;
  border: none;
  padding: 5px 10px;
  border-radius: 3px;
  cursor: pointer;
  font-size: 0.8em;
  margin-left: 10px;
  transition: background-color 0.3s ease;
}

.delete-button:hover {
  background-color: #c82333;
}

.add-travel-plan-form {
  background-color: #f0f2f5;
  padding: 20px;
  border-radius: 10px;
  margin-bottom: 20px;
  display: grid;
  grid-template-columns: 1fr 1fr; /* Two columns */
  gap: 15px; /* Space between grid items */
  align-items: center;
}

.add-travel-plan-form h4 {
  grid-column: 1 / -1; /* Span across all columns */
  text-align: center;
  color: #333;
  margin-bottom: 15px;
}

.add-travel-plan-form input[type="text"],
.add-travel-plan-form input[type="number"],
.add-travel-plan-form input[type="date"],
.add-travel-plan-form textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  box-sizing: border-box; /* Include padding and border in the element's total width and height */
}

.add-travel-plan-form textarea {
  grid-column: 1 / -1; /* Textarea spans full width */
  min-height: 80px;
  resize: vertical;
}

.add-travel-plan-form .save-button,
.add-travel-plan-form .cancel-button {
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 1em;
  transition: background-color 0.3s ease;
  width: auto; /* Adjust button width */
}

.add-travel-plan-form .save-button {
  background-color: #28a745;
  color: white;
  border: none;
  justify-self: end; /* Align to the right */
}

.add-travel-plan-form .save-button:hover {
  background-color: #218838;
}

.add-travel-plan-form .cancel-button {
  background-color: #6c757d;
  color: white;
  border: none;
  justify-self: start; /* Align to the left */
}

.add-travel-plan-form .cancel-button:hover {
  background-color: #5a6268;
}

.add-record-form {
  background-color: #e9ecef;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.add-record-form input {
   padding: 8px;
   border: 1px solid #ced4da;
   border-radius: 4px;
 }

 .input-with-voice {
   display: flex;
   gap: 10px;
   align-items: center;
 }

 .input-with-voice input {
   flex-grow: 1;
 }

 .save-button {
  background-color: #007bff; /* Blue */
  color: white;
  border: none;
  padding: 8px 15px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 0.9em;
  transition: background-color 0.3s ease;
}

.save-button:hover {
  background-color: #0056b3;
}

.cancel-button {
  background-color: #6c757d; /* Gray */
  color: white;
  border: none;
  padding: 8px 15px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 0.9em;
  transition: background-color 0.3s ease;
}

.cancel-button:hover {
  background-color: #5a6268;
}
/* */
</style>