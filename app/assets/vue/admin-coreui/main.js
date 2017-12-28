// The Vue build version to load with the `import` command,
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import { mapGetters } from 'vuex'
import Notifications from 'vue-notification'

import Croppa from 'vue-croppa'
import 'vue-croppa/dist/vue-croppa.css'

import Multiselect from 'vue-multiselect'
import 'vue-multiselect/dist/vue-multiselect.min.css'

import Spinner from 'vue-simple-spinner'

import flatPickr from 'vue-flatpickr-component'
import 'flatpickr/dist/flatpickr.css'
import 'flatpickr/dist/themes/material_blue.css'

import 'simple-line-icons/css/simple-line-icons.css'

import fontawesome from '@fortawesome/fontawesome'

import faBan from '@fortawesome/fontawesome-pro-solid/faBan'
import faBuilding from '@fortawesome/fontawesome-pro-light/faBuilding'
import faBullhorn from '@fortawesome/fontawesome-pro-solid/faBullhorn'
import faCalendar from '@fortawesome/fontawesome-pro-light/faCalendar'
import faLock from '@fortawesome/fontawesome-pro-light/faLock'
import faTimes from '@fortawesome/fontawesome-pro-light/faTimes'
import faTrash from '@fortawesome/fontawesome-pro-solid/faTrash'
import faTrophy from '@fortawesome/fontawesome-pro-solid/faTrophy'
import faUser from '@fortawesome/fontawesome-pro-solid/faUser'
import faUsers from '@fortawesome/fontawesome-pro-solid/faUsers'
import faWrench from '@fortawesome/fontawesome-pro-solid/faWrench'
import faDotCircle from '@fortawesome/fontawesome-pro-solid/faDotCircle'

import Authentication from 'common/authentication'

import AdminButton from './components/button'
import AdminCard from './components/card'
import AdminFileChooser from './components/file-chooser'
import AdminPage from './components/page'
import AdminPageItem from './components/page-item'
import AdminTable from './components/table'

import RespondecoIcon from 'common/components/respondeco-icon'

import { router } from './router'
import { http } from './http'
import store from './store'

Authentication.init(router)

fontawesome.library.add(
  faBan,
  faBuilding,
  faBullhorn,
  faCalendar,
  faDotCircle,
  faLock,
  faTimes,
  faTrash,
  faTrophy,
  faUser,
  faUsers,
  faWrench
)

Vue.component(AdminButton.name, AdminButton)
Vue.component(AdminCard.name, AdminCard)
Vue.component(AdminFileChooser.name, AdminFileChooser)
Vue.component(AdminPage.name, AdminPage)
Vue.component(AdminPageItem.name, AdminPageItem)
Vue.component(AdminTable.name, AdminTable)
Vue.component(RespondecoIcon.name, RespondecoIcon)
Vue.component('spinner', Spinner)

Vue.use(Croppa)
Vue.use(flatPickr)
Vue.use(Notifications)

const app = new Vue({
  router,
  store,

  computed: {
    ...mapGetters('loading', [
      /*
        `isLoading` returns a function with a parameter of loader name.
        e.g. `isLoading('creating user')` will return you a boolean value.
      */
      'isLoading',
      /*
        `anyLoading` returns a boolean value if any loader name exists on store.
      */
      'anyLoading'
    ])
  }
}).$mount('#app')
