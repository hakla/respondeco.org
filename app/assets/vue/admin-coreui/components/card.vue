<template>
  <div class="col">
    <div class="card">
      <div class="card-header">
        <slot name="header"></slot>
      </div>
      <div class="card-body">
        <transition name="fade" mode="out-in">
          <slot v-if="!$isLoading('card')"></slot>
          <spinner v-else></spinner>
        </transition>
      </div>
      <div class="card-footer" v-if="$slots.footer">
        <slot name="footer"></slot>
      </div>
    </div>
  </div>
</template>

<script>
  import Spinner from 'vue-simple-spinner'

  export default {
    name: 'admin-card',

    components: {
      Spinner
    },

    props: ['title']
  }
</script>

<style scoped>

</style>
