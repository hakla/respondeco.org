//<editor-fold desc="Unify v2.4.0 styles">
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
//</editor-fold>

//<editor-fold desc="Unify v2.4.0 scripts">
import 'unify/vendor/jquery.easing/js/jquery.easing.js'
import 'unify/vendor/masonry/dist/masonry.pkgd.js'

import 'unify/vendor/cubeportfolio/js/jquery.cubeportfolio.js'

import 'unify/js/hs.core.js'
import 'unify/js/components/hs.cubeportfolio.js'
import 'unify/js/components/hs.header.js'
import 'unify/js/components/hs.navigation.js'
import 'unify/js/helpers/hs.hamburgers.js'
import 'unify/vendor/hs-megamenu/src/hs.megamenu.js'
//</editor-fold>

//<editor-fold desc="Libraries">
import Vue from 'vue'
import { VueMasonryPlugin } from 'vue-masonry'
import VueYoutubeEmbed from 'vue-youtube-embed'
import Notifications from 'vue-notification'
import VeeValidate from 'vee-validate'
import { mapGetters } from 'vuex'
import VModal from 'vue-js-modal'
import VueUploadComponent from 'vue-upload-component'

import 'libs/font-awesome/js/fontawesome.js'
import 'libs/font-awesome/js/fa-light.js'
//</editor-fold>

//<editor-fold desc="Custom components">
import UnifyBlock from 'common/components/unify-block'
import UnifyButton from 'common/components/unify-button'
import UnifyFormInput from 'common/components/unify-form-input'
import UnifyFormInputAddon from 'common/components/unify-form-input-addon'
import UnifyList from 'common/components/unify-list'
import UnifyListItem from 'common/components/unify-list-item'
import UnifyListItemLabel from 'common/components/unify-list-item-label'
import UnifyListItemContent from 'common/components/unify-list-item-content'
import UnifyTextarea from 'common/components/unify-textarea'

Vue.component(UnifyBlock.name, UnifyBlock)
Vue.component(UnifyButton.name, UnifyButton)
Vue.component(UnifyFormInput.name, UnifyFormInput)
Vue.component(UnifyFormInputAddon.name, UnifyFormInputAddon)
Vue.component(UnifyList.name, UnifyList)
Vue.component(UnifyListItem.name, UnifyListItem)
Vue.component(UnifyListItemLabel.name, UnifyListItemLabel)
Vue.component(UnifyListItemContent.name, UnifyListItemContent)
Vue.component(UnifyTextarea.name, UnifyTextarea)
//</editor-fold>

//<editor-fold desc="App">
import Authentication from 'common/authentication'

import filters from './filters'
import { i18n } from './i18n'
import { router } from './router'
import store from './store/index.js'
import './http'
//</editor-fold>

// Initialize authentication with router
Authentication.init(router)

// Disable productionTip in console
Vue.config.productionTip = false

// Global filters
Vue.filter('formatDate', filters.formatDate)
Vue.filter('translate', filters.translate)

// Vue plugins
Vue.use(VueMasonryPlugin)
Vue.use(VueYoutubeEmbed)
Vue.use(Notifications)
Vue.use(VeeValidate)
Vue.use(VModal, { dialog: true })

Vue.component('file-upload', VueUploadComponent)

// Create application
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

// window onload to call unify specific scripts
$(window).on('load', function () {
  // initialization of header
  $.HSCore.components.HSHeader.init($('#js-header'))
  $.HSCore.helpers.HSHamburgers.init('.hamburger')

  // initialization of cubeportfolio
  $.HSCore.components.HSCubeportfolio.init('.cbp')

  // initialization of HSMegaMenu component
  $('.js-mega-menu').HSMegaMenu({
    event: 'hover',
    pageContainer: $('.container'),
    breakpoint: 991
  })
})
