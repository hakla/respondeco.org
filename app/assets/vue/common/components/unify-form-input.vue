<template>
  <div>
    <div class="input-group">
      <input class="form-control rounded-0" :type="type" :value="value" @input="update">

      <div class="input-group-addon d-flex align-items-center g-bg-white g-color-gray-light-v1 rounded-0" v-if="$slots.addon">
        <slot name="addon"></slot>
      </div>
    </div>

    <slot name="feedback"></slot>
  </div>
</template>

<script>
  export default {
    name: 'unify-form-input',

    methods: {
      update (event) {
        this.$emit('input', event.target.value)
      }
    },

    props: {
      type: {
        type: String,
        default: 'text'
      },
      value: null
    }
  }
</script>

<style scoped lang="stylus">
  input {
    border: 1px solid #f8f8f8
    border-right: none !important

    &:focus {
      border: 1px solid #ccc

      + .input-group-addon {
        border: 1px solid #ccc
      }
    }

    + .input-group-addon {
      border: 1px solid #f8f8f8
    }
  }
</style>
