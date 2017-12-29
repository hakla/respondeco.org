<template>
  <button class="vue-dialog-button" type="button" @click="click">
    <slot></slot>
  </button>
</template>

<script>
  export default {
    name: "dialog-button",

    methods: {
      click (event) {
        this.$emit('click', event)
      }
    }
  }
</script>

<style scoped>
  button {
    flex-grow: 1;
  }
</style>
