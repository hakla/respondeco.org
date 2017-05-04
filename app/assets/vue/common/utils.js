import moment from 'moment'

const formats = [ "DD.MM.YYYY", "YYYY-MM-DD", "MM/DD/YYYY" ]

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

}
