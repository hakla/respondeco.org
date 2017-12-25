<template>
  <modal class="vue-dialog"
         :name="name"
         height="auto"
         :classes="['v--modal', 'vue-dialog']"
         :width="400"
         :pivot-y="0.3"
         :adaptive="true"
         :scrollable="true"
         :clickToClose="clickToClose"
         @before-open="$emit('beforeOpen')"
         @before-close="$emit('beforeClose')"
         @closed="$emit('closed')"
         @opened="$emit('opened')">

    <div class="dialog-content">
      <div class="dialog-c-title">
        {{ title }}
        <div class="g-font-size-22 g-pos-abs g-right-10 g-top-3 g-cursor-pointer" @click="$modal.hide(name)">
          <i class="fal fa-times"></i>
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
      buttons: Array,
      clickToClose: {
        type: Boolean,
        default: true
      },
      name: String,
      title: null
    }
  }
</script>

<style scoped>

</style>
