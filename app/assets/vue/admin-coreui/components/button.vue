<template>
  <button @click="click" :disabled="$isLoading(loader)" type="submit" class="btn d-inline-flex align-items-center">
    <transition name="fade" mode="out-in">
      <spinner style="margin-right: 8px" size="small" v-if="$isLoading(loader)"></spinner>
    </transition>

    <span>
      <slot></slot>
    </span>
  </button>
</template>

<script>
  export default {
    name: 'admin-button',

    methods: {
      click (event) {
        this.$emit('click', event)
      }
    },

    props: ['loader']
  }
</script>

<style scoped>

</style>
