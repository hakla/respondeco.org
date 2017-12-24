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

Vue.use(Vuex)
Vue.use(VuexLoading)

const debug = process.env.NODE_ENV !== 'production'
const store = new Vuex.Store({
  plugins: [
    VuexLoading.Store
  ],
  strict: debug
})

export default store
