<template>
  <font-awesome-icon :icon="normalisedIcon"></font-awesome-icon>
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

        // let mapped = mapping[normalised[0]]
        // let normalisedIcon = "fa" + normalised[1].substring(0, 1).toUpperCase() + normalised[1].substring(1)
        //
        // FontAwesomeHelper.add([`${mapped}:${normalisedIcon}`])

        return normalised
      }
    },

    props: ['icon']
  }
</script>
