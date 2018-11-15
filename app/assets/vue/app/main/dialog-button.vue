<template>
  <button class="vue-dialog-button u-btn-teal" type="button" @click="click">
    <slot></slot>
  </button>
</template>

<script>
  export default {
    name: 'dialog-button',

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

  .u-btn-teal {
    background-color: #18ba9b;
    color: #fff;
  }

  .u-btn-teal:hover, .u-btn-teal.active {
    background-color: #22e3be;
    border-color: #22e3be;
  }

  .u-btn-teal:hover, .u-btn-teal:focus, .u-btn-teal.active {
    color: #fff;
  }
</style>
