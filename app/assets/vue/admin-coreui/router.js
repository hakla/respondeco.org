import Vue from 'vue'
import VueRouter from 'vue-router'

import Account from './accounts/account'
import Accounts from './accounts/accounts'

import FinishedProject from './finishedProjects/finished-project'
import FinishedProjects from './finishedProjects/finished-projects'

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
    name: 'accounts',
    path: '/accounts',
    component: Accounts,
    meta: {
      title: 'Accounts'
    }
  }, {
    name: 'account',
    path: 'accounts/:id',
    component: Account,
    props: true,
    meta: {
      title: 'Account anlegen / bearbeiten'
    }
  }, {
    path: '/finishedProjects',
    name: 'finishedProjects',
    component: FinishedProjects,
    meta: {
      title: 'Abgeschlossene Projekte'
    }
  }, {
    name: 'finishedProject',
    path: 'finishedProjects/:id',
    component: FinishedProject,
    props: true,
    meta: {
      title: 'Abgeschlossenes Projekt anlegen / bearbeiten'
    }
  }, {
    path: '/organisations',
    name: 'organisations',
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
    name: 'projects',
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
