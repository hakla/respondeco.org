// The Vue build version to load with the `import` command,
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'

import Authentication from 'common/authentication'
import AdminPage from 'admin/components/admin-page'
import AdminFilter from 'admin/components/admin-filter'
import FormGroup from 'admin/components/form-group'

import { router } from './router'
import { http } from './http'
import store from './store'
import { mapGetters } from 'vuex'
import Notifications from 'vue-notification'

Authentication
  .init(router)

Vue.component(AdminFilter.name, AdminFilter)
Vue.component(AdminPage.name, AdminPage)
Vue.component(FormGroup.name, FormGroup)

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
      'anyLoading',
    ])
  }
}).$mount('#app')
