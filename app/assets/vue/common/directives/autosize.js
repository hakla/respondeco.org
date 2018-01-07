import autosize from 'autosize'

const Autosize = {
  componentUpdated (el) {
    autosize(el)
  },

  inserted (el) {
    autosize(el)
  },

  update (el) {
    autosize(el)
  }
}

export default Autosize
