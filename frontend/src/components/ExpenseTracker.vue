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
            <input type="text" v-model="newRecord.content" placeholder="描述" />
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
    };
  },
  created() {
    // this.checkLoginStatus(); // 登录状态现在由NavBar管理，无需在此处检查
    if (this.isLoggedIn) {
      this.fetchTravelPlans();
    }
  },
  methods: {
    // checkLoginStatus() {
    //   const token = localStorage.getItem('userToken');
    //   if (token) {
    //     this.isLoggedIn = true;
    //     this.userId = '1'; // Simulate logged-in user
    //   } else {
    //     this.isLoggedIn = false;
    //     this.userId = null;
    //   }
    // },
    async fetchTravelPlans() {
      if (!this.userId) return;
      try {
        const response = await axios.get(`/api/travelPlan/user/${this.userId}`);
        this.travelPlans = response.data;
      } catch (error) {
        console.error('Error fetching travel plans:', error);
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
  },
};
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