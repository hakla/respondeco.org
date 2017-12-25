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

import VueRouter from 'vue-router'

Vue.use(VueRouter)

const meta = {
  noFooter: {
    footer: false,
  },

  noHeader: {
    header: false
  },

  scrollToHero: {
    scroll: '.unify-hero'
  },

  scrollToListGroup: {
    scroll: false // for now
  },

  scrollToTop: {
    scroll: (to) => {
      if (!to.query.page) {
        return 0
      } else {
        return false
      }
    }
  }
}

export const routes = [{
  path: '/',
  component: App,
  children: [{
    path: '',
    name: 'home',
    component: Home,
    meta: Object.assign({}, meta.noFooter, meta.noHeader, meta.scrollToTop)
  }, {
    path: 'organisations',
    name: 'organisations',
    component: Organisations,
    meta: meta.scrollToTop,
  }, {
    path: 'organisations/:id',
    component: Organisation,
    children: [{
      path: 'about',
      name: 'organisation-about',
      component: OrganisationAbout,
      meta: meta.scrollToListGroup,
    }, {
      path: 'comments',
      name: 'organisation-comments',
      component: OrganisationComments,
      meta: meta.scrollToListGroup,
    }, {
      path: 'ratings',
      name: 'organisation-ratings',
      component: OrganisationRatings,
      meta: meta.scrollToListGroup,
    }, {
      path: '',
      name: 'organisation-projects',
      component: OrganisationProjects,
      meta: meta.scrollToListGroup,
    }, {
      path: 'settings',
      component: OrganisationSettings,
      alias: 'organisation-settings-profile',
      beforeEnter (to, from, next) {
        /*if (Authentication.activeUser().state === undefined) {
          // not logged in, redirect to login
          next('/login')
        }*/

        next()
      },
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
    component: Projects,
    meta: meta.scrollToTop,
  }, {
    name: 'project',
    path: 'projects/:id',
    component: Project,
    meta: meta.scrollToHero
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
}]

export const router = new VueRouter({
  routes,
  linkExactActiveClass: 'active',

  scrollBehavior (to, from, savedPosition) {
    setTimeout(() => {
      if (to.meta.scroll != null && to.meta.scroll !== false) {
        let position = 0

        if (savedPosition) {
          position = savedPosition.y
        } else if (typeof to.meta.scroll === 'string') {
          let $el = $(to.meta.scroll)

          if ($el.length > 0) {
            try {
              position = $el.offset().top
            } catch (e) {
              return
            }
          }
        } else if (typeof to.meta.scroll === 'number') {
          position = to.meta.scroll
        } else if (typeof to.meta.scroll === 'function') {
          position = to.meta.scroll(to, from, savedPosition)
        }

        if (position !== false) {
          $('html, body').animate({
            scrollTop: position
          })
        }
      }
    }, 10)
  }
})
