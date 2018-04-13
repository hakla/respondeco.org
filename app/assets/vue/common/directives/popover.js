import $ from 'jquery'

const Popover = {
  inserted (el, binding) {
    $(el).popover(binding.value || {})
  }
}

export default Popover
