import Vue from 'vue';
import App from './app';

import Login from './authentication/login';
import Organisations from './organisations/organisations';
import Projects from './projects/projects';

import VueRouter from 'vue-router';
Vue.use(VueRouter);

export const routes = [{
  path: '/',
  component: App,
  children: [{
    path: 'organisations',
    component: Organisations
  }, {
    path: 'projects',
    component: Projects
  }]
}, {
  path: '/login',
  component: Login
}];

export const router = new VueRouter({
  routes
})
