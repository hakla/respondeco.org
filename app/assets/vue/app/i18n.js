import Vue from 'vue';
import VueI18n from 'vue-i18n';
import de from './i18n/de';

Vue.use(VueI18n);

Vue.config.lang = 'de';
Vue.locale('de', de);
