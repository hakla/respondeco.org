import Vue from 'vue';
import VueResource from 'vue-resource';

import TokenHolder from '../common/token-holder';
import {
  router
} from './router';

Vue.use(VueResource);

Vue.http.options.root = '/api/v1'
Vue.http.headers.common['X-Access-Token'] = ""
Vue.http.interceptors.push((request, next) => {
  TokenHolder.get(token => request.headers.set('X-Access-Token', token))

  next(response => {
    // if (response.status === 401) {
    //   let path = ""
    //
    //   if (!/^(\/|\/login|\/logout)$/.test(router.currentRoute.path)) {
    //     path = `?route=${router.currentRoute.path}`
    //   }
    //
    //   router.push(`/login${path}`)
    // }
  });
});
