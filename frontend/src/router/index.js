import Vue from 'vue'
import Router from 'vue-router'
import Home from '@/components/Home'
import Register from '@/components/Register'
import Login from '@/components/Login'
import GuestDashboard from '@/components/GuestDashboard'
import MapPage from '@/components/MapPage'
import ExpenseTracker from '@/components/ExpenseTracker'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'Home',
      component: Home
    },
    {
      path: '/register',
      name: 'Register',
      component: Register
    },
    {
      path: '/login',
      name: 'Login',
      component: Login
    },
    {
      path: '/guest-dashboard',
      name: 'GuestDashboard',
      component: GuestDashboard
    },
    {
      path: '/map',
      name: 'MapPage',
      component: MapPage
    },
    {
      path: '/expenses',
      name: 'ExpenseTracker',
      component: ExpenseTracker
    }
  ]
})
