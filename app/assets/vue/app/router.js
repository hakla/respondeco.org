import Vue from 'vue';
import App from './app';

import Login from './authentication/login';
import Logout from './authentication/logout';
import Organisation from './organisations/organisation'
import Organisations from './organisations/organisations'
import Project from './projects/project'
import Projects from './projects/projects'
import FinishedProject from './projects/finished-project'

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
    name: 'project',
    path: 'projects/:id',
    component: Project
  }, {
    name: 'finishedProject',
    path: 'finished-project/:id',
    component: FinishedProject
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
