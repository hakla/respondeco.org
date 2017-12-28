import Vue from 'vue'
import App from './app'

import FinishedProject from './projects/finished-project'

import Home from './home'

import Login from './authentication/login'
import Logout from './authentication/logout'

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

import Registration from './authentication/registration.vue'

import { RouteHelper } from './utils'

import VueRouter from 'vue-router'

Vue.use(VueRouter)

export const routes = [{
  path: '/',
  component: App,
  children: [{
    component: FinishedProject,
    name: 'finishedProject',
    path: 'finished-project/:id',
  }, {
    component: Home,
    meta: Object.assign(
      {},
      RouteHelper.meta.noFooter,
      RouteHelper.meta.noHeader,
      RouteHelper.meta.scrollToTop
    ),
    name: 'home',
    path: '',
  }, {
    component: Login,
    name: 'login',
    path: '/login',
  }, {
    component: Logout,
    name: 'logout',
    path: '/logout',
  }, {
    component: Organisations,
    meta: RouteHelper.meta.scrollToTop,
    name: 'organisations',
    path: 'organisations',
  }, {
    component: Organisation,
    path: 'organisations/:id',
    children: [{
      component: OrganisationAbout,
      meta: RouteHelper.meta.scrollToListGroup,
      name: 'organisation-about',
      path: 'about',
    }, {
      component: OrganisationComments,
      meta: RouteHelper.meta.scrollToListGroup,
      name: 'organisation-comments',
      path: 'comments',
    }, {
      component: OrganisationProjects,
      meta: RouteHelper.meta.scrollToListGroup,
      name: 'organisation-projects',
      path: '',
    }, {
      component: OrganisationRatings,
      meta: RouteHelper.meta.scrollToListGroup,
      name: 'organisation-ratings',
      path: 'ratings',
    }, {
      alias: 'organisation-settings-profile',
      component: OrganisationSettings,
      path: 'settings',
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
    component: Projects,
    name: 'projects',
    path: 'projects',
    meta: RouteHelper.meta.scrollToTop,
  }, {
    component: Project,
    meta: RouteHelper.meta.scrollToHero,
    name: 'project',
    path: 'projects/:id',
    props: true
  }, {
    component: Registration,
    name: 'registration',
    path: '/registration',
  }]
}]

export const router = new VueRouter({
  routes,
  linkExactActiveClass: 'active',

  scrollBehavior: RouteHelper.scrollBehavior
})
