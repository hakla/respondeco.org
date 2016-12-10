import Vue from 'vue';
import VueResource from 'vue-resource';

import TokenHolder from '../common/token-holder';
import {
  router
} from './router';

Vue.use(VueResource);

Vue.http.options.root = '/api';
Vue.http.headers.common['X-Access-Token'] = "";
Vue.http.interceptors.push((request, next) => {

  TokenHolder.get(token => request.headers.set('X-Access-Token', token));

  next(response => {
    if (response.status === 401) {
      router.push('login');
    }
  });
});
