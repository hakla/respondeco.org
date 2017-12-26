import { format, parse } from 'date-fns'
import { German } from 'flatpickr/dist/l10n/de.js'
import Config from './config'
import Images from './services/images'

const formats = ['DD.MM.YYYY', 'YYYY-MM-DD', 'MM/DD/YYYY']

export const Categories = [
  'Ökonomie',
  'Umwelt',
  'Gesellschaft'
]

export const DateConfig = {
  wrap: true, // set wrap to true only when using 'input-group'
  altFormat: 'd.m.Y',
  altInput: true,
  dateFormat: 'Y-m-d',
  locale: German
}

export const ImageHelper = {
  backgroundImage (image) {
    let style = ''

    if (image !== undefined) {
      style = `background-image: url("${Utils.imageUrl(image)}"); `
    }

    return style
  },

  imageUrl (image) {
    let url = image

    if (/^[0-9a-z]*\.(png|jpe?g)/.test(image)) {
      url = `${Config.ImageBaseUrl}/${image}`
    }

    return url
  },

  saveFromCroppa (croppa, name, attr, croppaOptions = {}) {
    let promise = Promise.resolve({})

    // If the object does not have the hasImage function then the image wasn't changed
    if (croppa.hasImage) {
      if (croppa.hasImage()) {
        // Logo changed --> save
        promise = new Promise((resolve, reject) => {
          croppa.generateBlob(blob => {
            blob.name = name

            Images.save(blob).then(response => {
              resolve({
                [attr]: response.body
              })
            }, error => reject(error))
          }, ...croppaOptions)
        })
      } else {
        // Image removed
        promise = new Promise(resolve => resolve({
          [attr]: ''
        }))
      }
    }

    return promise
  }
}

export const ImageMixin = {
  methods: {
    backgroundImage: ImageHelper.backgroundImage,
    imageUrl: ImageHelper.imageUrl
  }
}

export const Notifications = {
  error (vm, status = {}) {
    let transformer = (error) => {
      let message = ''

      if (typeof status === 'string') {
        message = status
      } else if (error && error.status) {
        message = status[error.status] || (vm.$t && (vm.$t(`http.status.${error.status}`) || vm.$t('http.status.unknown'))) || ('Fehler: ' + error.status)
      } else {
        message = vm.$t && vm.$t('common.error.undefined') || 'Undefinierter Fehler'
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
  account (account = {}) {
    return Object.assign({
      email: undefined,
      id: undefined,
      name: undefined,
      password: undefined,
      organisationId: null,
      role: 'User'
    })
  },

  finishedProject (finishedProject = {}) {
    return Object.assign({

    }, finishedProject)
  },

  organisation (organisation = {}) {
    return Object.assign({
      description: '',
      email: '',
      image: '',
      location: '',
      logo: '',
      name: '',
      website: ''
    }, organisation)
  },

  project (project = {}) {
    return Object.assign({
      category: undefined,
      description: undefined,
      end: undefined,
      id: undefined,
      location: undefined,
      name: undefined,
      organisation: undefined,
      price: 0,
      start: undefined,
      subcategory: undefined,
      video: undefined
    }, project)
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

export const Subcategories = {
  'Ökonomie': [
    'Produkt',
    'Beschäftigungsverhältnisse',
    'Arbeitsbedingungen und Sozialschutz',
    'Gesundheit und Sicherheit am Arbeitsplatz',
    'Menschliche Entwicklung am Arbeitsplatz',
    'Schulungen und Weiterbildungen ',
    'Korruptionsbekämpfung',
    'Kundendienst, Beschwerdemanagement',
    'Schutz und Vertraulichkeit von Kundendaten'
  ],
  'Umwelt': [
    'Inanspruchnahme natürlicher Ressourcen',
    'Ressourcenmanagement',
    'Klimarelevante Emissionen'
  ],
  'Gesellschaft': [
    'Demokratie/Menschenrechte',
    'Einbindung der Gemeinschaft',
    'Bildung/ Kultur',
    'Schaffen von Arbeitsplätzen und berufliche Qualifizierung ',
    'Chancengerechtigkeit',
    'Anti-Diskriminierung/ schutzbedürftige Gruppen',
    'Stadtteil',
    'Unterstützung von Social Businesses'
  ]
}

export default class Utils {

  static convertDate (date, to) {
    return Utils.formatDate(date, to || 'YYYY-MM-DD')
  }

  static formatDate (date, dateFormat) {
    let formatted = undefined

    dateFormat = dateFormat || 'DD.MM.YYYY'

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
    let style = ''

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
