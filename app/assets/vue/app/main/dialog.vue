<template>
  <modal class="vue-dialog"
         ref="dialog"
         :name="name"
         :height="height"
         :width="width"
         :classes="['v--modal', 'vue-dialog']"
         :pivot-y="0.3"
         :adaptive="adaptive"
         :scrollable="scrollable"
         :reset="true"
         :clickToClose="clickToClose"
         @before-open="$emit('beforeOpen')"
         @before-close="$emit('beforeClose')"
         @closed="$emit('closed')"
         @opened="$emit('opened')">

    <div class="dialog-content">
      <div class="dialog-c-title">
        {{ title }}
        <div class="g-font-size-22 g-pos-abs g-right-10 g-top-3 g-cursor-pointer" @click="$modal.hide(name)">
          <respondeco-icon icon="fal times"></respondeco-icon>
        </div>
      </div>
      <div class="dialog-c-text">
        <slot></slot>
      </div>
    </div>

    <slot name="buttons"></slot>
    <div v-if="!$slots.buttons" class="vue-dialog-buttons-none"></div>
  </modal>
</template>

<script>
  export default {
    name: 'respondeco-dialog',

    props: {
      adaptive: {
        type: Boolean,
        default: true
      },
      buttons: Array,
      clickToClose: {
        type: Boolean,
        default: true
      },
      height: {
        type: Number|String,
        default: 'auto'
      },
      name: String,
      scrollable: {
        type: Boolean,
        default: true
      },
      title: null,
      width: {
        type: Number|String,
        default: 400
      }
    },

    watch: {
      height (newValue) {
        // hack because the height and width are not reactive
        this.$refs.dialog._data.modal.height = newValue
      },

      width (newValue) {
        // hack because the height and width are not reactive
        this.$refs.dialog._data.modal.width = newValue
      }
    }
  }
</script>

<style scoped>

</style>
