<template>
  <textarea class="form-control form-control-md g-resize-none rounded-0 unify-textarea" rows="3" v-model="model" @input="update"></textarea>
</template>

<script>
  export default {
    name: 'unify-textarea',

    data () {
      return {
        model: this.value
      }
    },

    methods: {
      update (event) {
        this.$emit('input', event.target.value)
      }
    },

    props: ['value'],

    watch: {
      value (value) {
        this.model = value
      }
    }
  }
</script>

<style scoped lang="stylus">
  textarea {
    border: 1px solid transparent

    &:focus {
      border: 1px solid #ccc
    }
  }
</style>
