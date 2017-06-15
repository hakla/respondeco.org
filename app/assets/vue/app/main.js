import 'unify/plugins/bootstrap/css/bootstrap.min.css';
import 'unify/css/style.css';

import 'unify/css/headers/header-default.css';
import 'unify/css/footers/footer-v1.css';
import 'unify/plugins/animate.css';

import 'unify/plugins/cube-portfolio/cubeportfolio/css/cubeportfolio.min.css'
import 'unify/plugins/cube-portfolio/cubeportfolio/custom/custom-cubeportfolio.css'

import 'unify/plugins/cube-portfolio/cubeportfolio/js/jquery.cubeportfolio.min.js'

// Theme
import 'unify/css/theme-colors/default.css';
import 'unify/css/theme-skins/dark.css';

import 'unify/plugins/line-icons/line-icons.css';
import 'unify/plugins/font-awesome/css/font-awesome.min.css';

// Libraries
import Vue from 'vue';

// App
import Authentication from 'common/authentication';

import { router } from './router';
import './http';
import { i18n } from './i18n';

Authentication
  .init(router);

const app = new Vue({
  router,
  i18n
}).$mount('#app')
