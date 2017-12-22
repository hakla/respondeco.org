<template>
  <!-- Alert Danger -->
  <transition name="fade">
    <div v-if="visible" class="alert show rounded-0" role="alert">
      <button type="button" class="close u-alert-close--light g-ml-10 g-mt-1 g-cursor-pointer" @click="dismiss" aria-label="Close">
        <span aria-hidden="true">×</span>
      </button>

      <div class="media">
        <div class="media-body">
          <slot></slot>
        </div>
      </div>
    </div>
  </transition>
  <!-- End Alert Danger -->
</template>

<script>
  export default {
    name: "alert",

    data () {
      return {
        visible: false
      }
    },

    methods: {
      dismiss () {
        this.visible = false
        this.$emit('input', this.visible)
      }
    },

    props: {
      value: Boolean
    },

    watch: {
      value (val) {
        this.visible = val
      }
    }
  }
</script>

<style scoped>

</style>
