import 'unify/plugins/bootstrap/css/bootstrap.min.css';
import 'unify/css/style.css';

import 'unify/css/headers/header-default.css';
import 'unify/css/headers/header-v6.css';
import 'unify/css/footers/footer-v1.css';
import 'unify/plugins/animate.css';

// Theme
import 'unify/css/theme-colors/default.css';
import 'unify/css/theme-skins/dark.css';

import 'unify/plugins/line-icons/line-icons.css';
import 'unify/plugins/font-awesome/css/font-awesome.min.css';

import Vue from 'vue';

import Authentication from 'common/authentication';

import { router } from './router';
import './http';
import './i18n';

Authentication
  .init(router);

const app = new Vue({
    router
}).$mount('#app')
