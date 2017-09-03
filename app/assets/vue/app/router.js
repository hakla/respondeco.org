import Vue from 'vue';
import App from './app';

import Login from './authentication/login';
import Logout from './authentication/logout';
import Organisation from './organisations/organisation'
import Organisations from './organisations/organisations'
import Project from './projects/project'
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
    path: 'organisations/:id',
    component: Organisation
  }, {
    path: 'projects',
    component: Projects
  }, {
    path: 'projects/:id',
    component: Project
  }]
}, {
  path: '/login',
  component: Login
}, {
  path: '/logout',
  component: Logout
}];

export const router = new VueRouter({
  routes
})
