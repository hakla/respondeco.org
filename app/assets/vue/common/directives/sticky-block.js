let counter = 0

const StickyBlock = {
  inserted (el) {
    const className = `stickyblock-${++counter}`

    el.classList.add(className)

    $.HSCore.components.HSStickyBlock.init(`.${className}`);
  }
}

export default StickyBlock
