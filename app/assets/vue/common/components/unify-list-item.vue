<template>
  <li class="d-flex align-items-center justify-content-between g-brd-bottom g-brd-gray-light-v4 g-py-15">
    <slot name="label"></slot>
    <slot></slot>
  </li>
</template>

<script>
  export default {
    name: "unify-list-item",

    data () {
      return {
        beingEdited: false
      }
    },

    methods: {
      save () {
        this.$emit('input', this.model)
      }
    }
  }
</script>

<style scoped>

</style>
