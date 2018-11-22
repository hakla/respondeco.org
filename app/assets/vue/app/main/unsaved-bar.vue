<template>
  <div class="bottom-bar g-bottom-0 g-height-75" :class="{ 'alert-warning': visible }">
    <div class="container">
      <div class="row">
        <div class="warning col-6">
          <respondeco-icon class="fa-fw g-mr-10 fa-2x" icon="far exclamation-triangle"></respondeco-icon>
          <span>{{ $t('common.unsavedChanges') }}</span>
        </div>
        <div class="save col-6 text-right">
          <button class="btn btn-link g-color-black" @click.prevent="cancel()">{{ $t('common.cancel') }}</button>
          <unify-button
            @click="save"
            loading="saveDescription"
            class="btn u-btn-outline-teal g-font-weight-600 g-letter-spacing-0_5 g-brd-2 g-rounded-0--md">
            <span>{{ $t('common.save') }}</span>
          </unify-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    name: 'RespondecoUnsavedBar',

    methods: {
      cancel () {
        this.$emit('cancel')
      },

      save () {
        this.$emit('save')
      }
    },

    props: {
      visible: Boolean
    }
  }
</script>

<style lang="stylus" scoped>
  .bottom-bar {
    align-items: center;
    border-bottom: 1px solid #f2f2f2;
    display: flex;
    margin-bottom: -75px;
    padding-right: 1rem;
    transition: .25s ease all;
    transform: translateY(-75px);
    width: 100%;
    z-index: 500;

    &.alert-warning {
      transform: translateY(0);
    }
  }

  .warning {
    align-items: center;
    display: flex;
  }
</style>
