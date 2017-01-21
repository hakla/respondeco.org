import Vue from 'vue';
import VueResource from 'vue-resource';

import TokenHolder from './authentication/token-holder';
import { router } from './router';

Vue.use(VueResource);

Vue.http.options.root = '/api/v1';
Vue.http.headers.common['X-Access-Token'] = "598c71bcf56c56b4a7271c1b08ca403c7fa77898ec'l9hvukszgp_u77xu(aq77o9_z'_ws.3)anhpjlbbhpp9t(acubbac~mt54p6~";
Vue.http.interceptors.push((request, next) => {

    TokenHolder.get(token => request.headers.set('X-Access-Token', token));

    next(response => {
        console.log(response);

        if (response.status === 401) {
            router.push('/login');
        }
    });
});
