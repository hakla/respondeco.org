import Vue from 'vue';
import App from 'admin/app';

import VueRouter from 'vue-router';

import Projects from 'admin/projects/projects';
import Project from 'admin/projects/project';
import Organisations from 'admin/organisations/organisations';
import Organisation from 'admin/organisations/organisation';
import Login from 'admin/authentication/login';
import Logout from 'admin/authentication/logout';

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
