import $ from 'jquery'

const Tooltip = {
  inserted (el) {
    $(el).tooltip()
  }
}

export default Tooltip
