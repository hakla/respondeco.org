<template>
  <i>
    <font-awesome-icon
      :border="border"
      :fixedWidth="fixedWidth"
      :flip="flip"
      :icon="normalisedIcon"
      :mask="mask"
      :listItem="listItem"
      :pull="pull"
      :pulse="pulse"
      :rotation="rotation"
      :size="size"
      :spin="spin"
      :transform="transform"
      :symbol="symbol"></font-awesome-icon>
  </i>
</template>

<script>
  import FontAwesomeIcon from '@fortawesome/vue-fontawesome'
  import Vue from 'vue'

  Vue.component('font-awesome-icon', FontAwesomeIcon)

  const mapping = {
    fal: 'light',
    far: 'regular',
    fas: 'solid'
  }

  export default {
    name: "respondeco-icon",

    components: {
      FontAwesomeIcon
    },

    computed: {
      normalisedIcon () {
        let normalised = this.icon

        if (this.icon.indexOf(' ') > -1) {
          // "{type} {icon}"
          normalised = this.icon.split(' ')
        } else {
          normalised = ['fas', normalised]
        }

        return normalised
      }
    },

    props: {
      border: {
        type: Boolean,
        default: false
      },
      fixedWidth: {
        type: Boolean,
        default: false
      },
      flip: {
        type: String,
        default: null,
        validator: (value) => ['horizontal', 'vertical', 'both'].indexOf(value) > -1
      },
      icon: {
        type: [Object, Array, String],
        required: true
      },
      mask: {
        type: [Object, Array, String],
        default: null
      },
      listItem: {
        type: Boolean,
        default: false
      },
      pull: {
        type: String,
        default: null,
        validator: (value) => ['right', 'left'].indexOf(value) > -1
      },
      pulse: {
        type: Boolean,
        default: false
      },
      rotation: {
        type: Number,
        default: null,
        validator: (value) => [90, 180, 270].indexOf(value) > -1
      },
      size: {
        type: String,
        default: null,
        validator: (value) => ['lg', 'xs', 'sm', '1x', '2x', '3x', '4x', '5x', '6x', '7x', '8x', '9x', '10x'].indexOf(value) > -1
      },
      spin: {
        type: Boolean,
        default: false
      },
      transform: {
        type: [String, Object],
        default: null
      },
      symbol: {
        type: [Boolean, String],
        default: false
      }
    }
  }
</script>
