// The Vue build version to load with the `import` command,
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './app'

import Authentication from 'common/authentication'

import { router } from './router'
import { http } from './http'

Authentication
  .init(router);

const app = new Vue({
    router
}).$mount('#app')
