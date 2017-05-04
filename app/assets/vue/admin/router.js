import Vue from 'vue';
import App from './app';

import VueRouter from 'vue-router';

import Projects from './projects/projects';
import Project from './projects/project';
import Organisations from './organisations/organisations';
import Organisation from './organisations/organisation';
import Login from './authentication/login';
import Logout from './authentication/logout';

Vue.use(VueRouter);

export const routes = [{
  path: '/',
  component: App,
  children: [{
    path: 'organisations',
    component: Organisations
  }, {
    name: 'organisation',
    path: 'organisations/:id',
    component: Organisation
  }, {
    path: 'projects',
    component: Projects
  }, {
    name: 'project',
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
