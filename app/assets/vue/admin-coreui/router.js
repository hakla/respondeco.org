import Vue from 'vue'
import VueRouter from 'vue-router'

import Account from './accounts/account'
import Accounts from './accounts/accounts'

import Comment from './comments/comment'

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
    component: Accounts,
    meta: {
      title: 'Accounts'
    },
    name: 'accounts',
    path: '/accounts',
  }, {
    component: Account,
    meta: {
      title: 'Account anlegen / bearbeiten'
    },
    name: 'account',
    path: 'accounts/:id',
    props: true,
  }, {
    component: Comment,
    meta: {
      title: 'Kommentar anlegen / bearbeiten'
    },
    name: 'comment',
    path: '/:type/:typeId/comments/:id',
    props: true
  }, {
    component: FinishedProjects,
    meta: {
      title: 'Abgeschlossene Projekte'
    },
    name: 'finishedProjects',
    path: '/finishedProjects',
  }, {
    component: FinishedProject,
    meta: {
      title: 'Abgeschlossenes Projekt anlegen / bearbeiten'
    },
    name: 'finishedProject',
    path: 'finishedProjects/:id',
    props: true,
  }, {
    component: Organisations,
    meta: {
      title: 'Organisationen'
    },
    name: 'organisations',
    path: '/organisations',
  }, {
    component: Organisation,
    meta: {
      title: 'Organisation anlegen / bearbeiten'
    },
    name: 'organisation',
    path: 'organisations/:id',
    props: true,
  }, {
    component: Projects,
    meta: {
      title: 'Projekte'
    },
    name: 'projects',
    path: '/projects',
  }, {
    component: Project,
    meta: {
      title: 'Projekt anlegen / bearbeiten'
    },
    name: 'project',
    path: 'projects/:id',
    props: true,
  }]
}, {
  component: Login,
  path: '/login',
}, {
  component: Logout,
  path: '/logout',
}]

export const router = new VueRouter({
  routes
})
