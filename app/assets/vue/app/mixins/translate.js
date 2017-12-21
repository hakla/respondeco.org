import { i18n } from 'app/i18n'

export default {
  methods: {
    translate (key, count = 1) {
      return i18n.tc(key, count)
    }
  }
}
