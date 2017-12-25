<template>
  <button class="vue-dialog-button" @click.stop.prevent="click">
    <slot></slot>
  </button>
</template>

<script>
  export default {
    name: "dialog-button",

    methods: {
      click () {
        this.$emit('click')
      }
    }
  }
</script>

<style scoped>
  button {
    flex-grow: 1;
  }
</style>
