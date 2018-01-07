import $ from 'jquery'

const Popover = {
  inserted (el) {
    $(el).popover()
  }
}

export default Popover
