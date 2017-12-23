<template>
  <button class="btn rounded-0" :class="classes" @click="click" :disabled="$isLoading(loading)" type="button"
          ref="button">
    <div class="d-flex g-flex-centered">
      <transition name="fade">
        <spinner ref="spinner" class="spinner" :size="mapSize(size)" v-if="loading && $isLoading(loading)"></spinner>
      </transition>
      <slot></slot>
    </div>
  </button>
</template>

<script>
  import Spinner from 'vue-simple-spinner'

  let initialWidth

  export default {
    name: "unify-button",

    components: {
      Spinner
    },

    computed: {
      classes () {
        return [this.size || 'small', this.$isLoading(this.loading) ? 'loading' : ''].join(' ')
      }
    },

    methods: {
      click (event) {
        this.$emit('click', event)
      },

      mapSize (size) {
        return ({
          small: 'small',
          medium: 20,
          big: 30
        })[size]
      }
    },

    props: {
      loading: {
        type: String
      },
      size: {
        type: String,
        default: 'small'
      }
    }
  }
</script>

<style scoped lang="stylus">
  button.big {
    padding: 1.78571rem 3.57143rem;

    .spinner {
      left: -25px;
    }
  }

  button.medium {
    padding: 0.85714rem 1.78571rem;

    .spinner {
      left: -10px;
    }
  }

  button.small {
    padding: 0.42857rem 0.85714rem;

    .spinner {
      left: -4px;
    }
  }

  .spinner {
    opacity: 1;
    padding: 0;
    position: relative;
    transition: .2s ease all;
    width: 25px;
  }

  .fade-enter, .fade-leave-to {
    opacity: 0;
    width: 0;
  }

  .fade-enter-to, .fade-leave {
    opacity: 1;
    width: 25px;
  }

  .fade-leave-active {
    transition: .2s ease width;
  }
</style>
