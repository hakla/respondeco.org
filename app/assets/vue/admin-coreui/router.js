import Vue from 'vue'
import VueRouter from 'vue-router'

import Accounts from './accounts/accounts'

import App from './app'

import Login from './authentication/login'
import Logout from './authentication/logout'

import Organisation from './organisations/organisation'
import Organisations from './organisations/organisations'

import Project from './projects/project'
import Projects from './projects/projects'

Vue.use(VueRouter)

export const routes = [{
  path: '/',
  component: App,
  children: [{
    path: '/accounts',
    component: Accounts,
    meta: {
      title: 'Accounts'
    }
  }, {
    path: '/organisations',
    component: Organisations,
    meta: {
      title: 'Organisationen'
    }
  }, {
    name: 'organisation',
    path: 'organisations/:id',
    component: Organisation,
    props: true,
    meta: {
      title: 'Organisation anlegen / bearbeiten'
    }
  }, {
    path: '/projects',
    component: Projects,
    meta: {
      title: 'Projekte'
    }
  }, {
    name: 'project',
    path: 'projects/:id',
    component: Project,
    props: true,
    meta: {
      title: 'Projekt anlegen / bearbeiten'
    }
  }]
}, {
  path: '/login',
  component: Login
}, {
  path: '/logout',
  component: Logout
}]

export const router = new VueRouter({
  routes
})
