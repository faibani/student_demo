import { createRouter, createWebHistory } from 'vue-router';
import StudentLogin from '../components/StudentLogin.vue';
import HelloWorldNew from '../views/HelloWorldNew.vue';

const routes = [
  {
    path: '/',
    alias: '/login',
    name: 'login',
    component: StudentLogin
  },
  {
    path: '/helloworld',
    name: 'HelloWorld',
    component: HelloWorldNew
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;