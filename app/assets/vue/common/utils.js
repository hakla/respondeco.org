import { format, parse } from 'date-fns'
import Config from './config'

const formats = ["DD.MM.YYYY", "YYYY-MM-DD", "MM/DD/YYYY"]

export const Notifications = {
  error (vm, status = {}) {
    let transformer = (error) => {
      let message = ''

      if (typeof status === 'string') {
        message = status
      } else if (error && error.status) {
        message = status[error.status] || (vm.$t && (vm.$t(`http.status.${error.status}`) || vm.$t('http.status.unknown'))) || ('Fehler: ' + error.status)
      } else {
        message = vm.$t('common.error.undefined')
      }

      return message
    }

    return (error) => {
      vm.$endLoading('global-loader')

      vm.$notify({
        duration: 2500,
        title: transformer(error),
        type: 'error'
      })
    }
  },

  isLoading (vm) {
    return vm.$isLoading('global-loader')
  },

  startLoading (vm) {
    vm.$startLoading('global-loader')
  },

  success (vm, cb = result => {}) {
    return (result) => {
      cb(result)

      vm.$endLoading('global-loader')

      vm.$notify({
        duration: 1000,
        title: vm.$t && vm.$t('common.success') || 'Erfolg!',
        type: 'success'
      })
    }
  }
}

export const ObjectNormaliser = {
  organisation (organisation) {
    return Object.assign({
      description: '',
      email: '',
      image: '',
      location: '',
      logo: '',
      name: '',
      website: ''
    }, organisation)
  }
}

export const ScrollHelper = {
  to (x, y) {
    $('html, body').animate({
      scrollTop: y,
      scollLeft: x
    })
  }
}

export default class Utils {

  static convertDate (date, to) {
    return Utils.formatDate(date, to || "YYYY-MM-DD")
  }

  static formatDate (date, dateFormat) {
    let formatted = undefined

    dateFormat = dateFormat || "DD.MM.YYYY"

    if (date) {
      date = Utils.parseDate(date)
      formatted = format(date, dateFormat)
    }

    return formatted
  }

  static parseDate (date) {
    let now = new Date()
    let parsed = formats.map(format => parse, date, format, now).filter(date => !isNaN(date))

    if (parsed.length > 0) {
      date = parsed[0]
    }

    return date
  }

  static backgroundImage (image) {
    let style = ""

    if (image !== undefined) {
      style = `background-image: url("${Utils.imageUrl(image)}"); `
    }

    return style
  }

  static imageUrl (image) {
    let url = image

    if (/^[0-9a-z]*\.(png|jpe?g)/.test(image)) {
      url = `${Config.ImageBaseUrl}/${image}`
    }

    return url
  }

  static methods () {
    return {
      backgroundImage: this.backgroundImage,
      imageUrl: this.imageUrl
    }
  }

}
