<template>
  <input class="form-control rounded-0" :value="value" @input="update">
</template>

<script>
  export default {
    name: 'UnifyFormInput',

    methods: {
      update (event) {
        this.$emit('input', event.target.value)
      }
    },

    props: {
      value: null
    }
  }
</script>

<style scoped lang="stylus">
  input {
    border: 1px solid transparent

    &:focus {
      border: 1px solid #ccc
    }
  }
</style>
