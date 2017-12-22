import Vue from 'vue'
import Vuex from 'vuex'
import { createVuexLoader } from 'vuex-loading'

const VuexLoading = createVuexLoader({
  // The Vuex module name, 'loading' by default.
  moduleName: 'loading',
  // The Vue component name, 'v-loading' by default.
  componentName: 'v-loading',
  // Vue component class name, 'v-loading' by default.
  className: 'v-loading',
})

// import * as actions from './actions'
// import * as getters from './getters'
import Authentication from 'common/authentication'

import organisation from './modules/organisation'
import user from './modules/user'

Vue.use(Vuex)
Vue.use(VuexLoading)

const debug = process.env.NODE_ENV !== 'production'
const store = new Vuex.Store({
  modules: {
    organisation,
    user
  },
  plugins: [
    VuexLoading.Store
  ],
  strict: debug
})

// Update store when the authentication changes
Authentication.activeUser().then(user => {
  store.dispatch('activeUser', user)
})

export default store
