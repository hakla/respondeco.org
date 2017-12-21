import { i18n } from './i18n'
import utils from 'common/utils'

export default {

  formatDate (date) {
    return utils.formatDate(date)
  },

  translate (key) {
    return i18n.tc(key)
  }

}
