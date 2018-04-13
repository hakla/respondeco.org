//<editor-fold desc="Unify v2.4.0 styles">
import 'unify/vendor/cubeportfolio/css/cubeportfolio.css'
import 'unify/vendor/hamburgers/hamburgers.css'
import 'unify/vendor/hs-megamenu/src/hs.megamenu.css'
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
import 'unify/js/components/hs.sticky-block.js'
import 'unify/js/helpers/hs.hamburgers.js'
import 'unify/vendor/hs-megamenu/src/hs.megamenu.js'
//</editor-fold>

//<editor-fold desc="Libraries">
import { mapGetters } from 'vuex'
import Croppa from 'vue-croppa'
import Notifications from 'vue-notification'
import Spinner from 'vue-simple-spinner'
import VModal from 'vue-js-modal'
import VeeValidate from 'vee-validate'
import Vue from 'vue'
import { VueMasonryPlugin } from 'vue-masonry'
import VueUploadComponent from 'vue-upload-component'
import VueYoutubeEmbed from 'vue-youtube-embed'

import 'bootstrap-vue/dist/bootstrap-vue.css'
import 'vue-croppa/dist/vue-croppa.css'
import 'vue-multiselect/dist/vue-multiselect.min.css'
//</editor-fold>

//<editor-fold desc="Custom components">
import Autosize from 'common/directives/autosize'
import 'app/font-awesome'
import Popover from 'common/directives/popover'
import RespondecoIcon from 'common/components/respondeco-icon'
import StickyBlock from 'common/directives/sticky-block'
import Tooltip from 'common/directives/tooltip'
import UnifyBlock from 'common/components/unify-block'
import UnifyButton from 'common/components/unify-button'
import UnifyFormInput from 'common/components/unify-form-input'
import UnifyFormInputAddon from 'common/components/unify-form-input-addon'
import UnifyHero from 'common/components/unify-hero'
import UnifyList from 'common/components/unify-list'
import UnifyListItem from 'common/components/unify-list-item'
import UnifyListItemLabel from 'common/components/unify-list-item-label'
import UnifyListItemContent from 'common/components/unify-list-item-content'
import UnifyTextarea from 'common/components/unify-textarea'

Vue.component(RespondecoIcon.name, RespondecoIcon)
Vue.component('spinner', Spinner)
Vue.component(UnifyBlock.name, UnifyBlock)
Vue.component(UnifyButton.name, UnifyButton)
Vue.component(UnifyFormInput.name, UnifyFormInput)
Vue.component(UnifyFormInputAddon.name, UnifyFormInputAddon)
Vue.component(UnifyHero.name, UnifyHero)
Vue.component(UnifyList.name, UnifyList)
Vue.component(UnifyListItem.name, UnifyListItem)
Vue.component(UnifyListItemLabel.name, UnifyListItemLabel)
Vue.component(UnifyListItemContent.name, UnifyListItemContent)
Vue.component(UnifyTextarea.name, UnifyTextarea)
Vue.directive('autosize', Autosize)
// Vue.directive('popover', Popover)
Vue.directive('stickyblock', StickyBlock)
Vue.directive('tooltip', Tooltip)
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
Vue.use(Croppa)
Vue.use(Notifications)
Vue.use(VueYoutubeEmbed)
Vue.use(VeeValidate)
Vue.use(VModal, { dialog: true })
Vue.use(VueMasonryPlugin)

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
$(window).on('load respondeco-refresh-js-libraries', function () {
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
