import Vue from 'vue';
import App from './app';

import Login from './authentication/login'
import Logout from './authentication/logout'

import Registration from './authentication/registration.vue'

import Organisation from './organisations/organisation'
import OrganisationAbout from './organisations/organisation-about'
import OrganisationComments from './organisations/organisation-comments'
import OrganisationRatings from './organisations/organisation-ratings'
import OrganisationProjects from './organisations/organisation-projects'
import OrganisationSettings from './organisations/organisation-settings'
import OrganisationSettingsProfile from './organisations/organisation-settings-profile'
import OrganisationSettingsSecurity from './organisations/organisation-settings-security'
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
    name: 'organisations',
    component: Organisations
  }, {
    path: 'organisations/:id',
    component: Organisation,
    children: [{
      path: 'about',
      name: 'organisation-about',
      component: OrganisationAbout
    }, {
      path: 'comments',
      name: 'organisation-comments',
      component: OrganisationComments
    }, {
      path: 'ratings',
      name: 'organisation-ratings',
      component: OrganisationRatings
    }, {
      path: '',
      name: 'organisation-projects',
      component: OrganisationProjects
    }, {
      path: 'settings',
      component: OrganisationSettings,
      alias: 'organisation-settings-profile',
      children: [{
        path: '',
        name: 'organisation-settings-profile',
        component: OrganisationSettingsProfile
      }, {
        path: 'security',
        name: 'organisation-settings-security',
        component: OrganisationSettingsSecurity
      }]
    }]
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
  }, {
    path: '/login',
    component: Login
  }, {
    path: '/logout',
    component: Logout
  }, {
    path: '/registration',
    component: Registration
  }]
}];

export const router = new VueRouter({
  routes,
  linkExactActiveClass: 'active'
})
