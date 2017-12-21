import moment from 'moment'
import Config from './config'

const formats = ["DD.MM.YYYY", "YYYY-MM-DD", "MM/DD/YYYY"]

export default class Utils {

  static convertDate(date, to) {
    return Utils.formatDate(date, to || "YYYY-MM-DD")
  }

  static formatDate(date, format) {
    let formatted = undefined

    format = format || "DD.MM.YYYY"

    if (date) {
      formatted = moment(date, formats).format(format)
    }

    return formatted
  }

  static backgroundImage(image) {
    let style = ""

    if (image !== undefined) {
      style = `background-image: url("${Utils.imageUrl(image)}"); `
    }

    return style
  }

  static imageUrl(image) {
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
