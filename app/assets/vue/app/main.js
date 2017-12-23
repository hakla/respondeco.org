// Unify v2.4.0 styles
import 'unify/vendor/cubeportfolio/css/cubeportfolio.css'
import 'unify/vendor/hamburgers/hamburgers.css'
import 'unify/vendor/hs-megamenu/src/hs.megamenu.css'
import 'unify/vendor/icon-awesome/css/font-awesome.css'
import 'unify/vendor/icon-line/css/simple-line-icons.css'
import 'unify/vendor/icon-hs/style.css'

// Unify has to be more specific than plugins
import 'unify/css/unify-core.css'
import 'unify/css/unify-components.css'
import 'unify/css/unify-globals.css'

// Unify v2.4.0 scripts
import 'unify/vendor/jquery.easing/js/jquery.easing.js'
import 'unify/vendor/masonry/dist/masonry.pkgd.js'

import 'unify/js/hs.core.js'
import 'unify/js/components/hs.header.js'
import 'unify/js/helpers/hs.hamburgers.js'
import 'unify/vendor/hs-megamenu/src/hs.megamenu.js'
import 'unify/js/components/hs.navigation.js'

// hs.* scripts
import 'unify/js/hs.core.js'
import 'unify/js/components/hs.header.js'
import 'unify/js/helpers/hs.hamburgers.js'

import 'unify/vendor/cubeportfolio/js/jquery.cubeportfolio.js'

// Libraries
import Vue from 'vue'
import { VueMasonryPlugin } from 'vue-masonry'
import VueYoutubeEmbed from 'vue-youtube-embed'
import { mapGetters } from 'vuex'

// App
import Authentication from 'common/authentication'

import filters from './filters'
import { i18n } from './i18n';
import { router } from './router'
import store from './store/index.js'
import './http'

Authentication
  .init(router)

Vue.config.productionTip = false

Vue.filter('formatDate', filters.formatDate)
Vue.filter('translate', filters.translate)

Vue.use(VueMasonryPlugin)
Vue.use(VueYoutubeEmbed)

const app = new Vue({
  router,
  store,
  i18n,
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

$(window).on('load', function () {
  // initialization of header
  $.HSCore.components.HSHeader.init($('#js-header'))
  $.HSCore.helpers.HSHamburgers.init('.hamburger')

  // initialization of HSMegaMenu component
  $('.js-mega-menu').HSMegaMenu({
    event: 'hover',
    pageContainer: $('.container'),
    breakpoint: 991
  })
})
