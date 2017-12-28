<template>
  <div class="col">
    <div class="card">
      <div class="card-header">
        <slot name="header"></slot>
      </div>
      <div class="card-body">
        <transition name="fade" mode="out-in">
          <slot v-if="!isLoading()"></slot>
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
  import LoaderHelper from '../../common/mixins/loader-helper'

  export default {
    name: 'admin-card',

    mixins: [LoaderHelper],

    props: {
      loader: {
        type: Array | String,
        default: 'card'
      },
      title: String
    }
  }
</script>

<style scoped>

</style>
