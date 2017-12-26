<template>
  <form @submit.prevent="submit">
    <admin-page>
      <admin-card :loader="loader">
        <span slot="header">{{ title }}</span>

        <slot></slot>

        <div class="text-right" slot="footer">
          <button @click="back" type="button" class="btn btn-link">Abbrechen</button>
          <button :disabled="$isLoading('global-loader')" type="submit"
                  class="btn btn-primary d-inline-flex align-items-center">
            <transition name="fade" mode="out-in">
              <spinner style="margin-right: 8px" size="small" v-if="$isLoading('global-loader')"></spinner>
            </transition>

            <span>
              Speichern
            </span>
          </button>
        </div>
      </admin-card>
    </admin-page>
  </form>
</template>

<script>
  export default {
    name: 'admin-page-item',

    computed: {
      isNew () {
        return !this.item.id
      },

      method () {
        return this.isNew ? 'save' : 'update'
      }
    },

    methods: {
      back () {
        this.$router.push(this.routeBack)
      },

      submit (event) {
        this.$emit('submit', event, this.method)
      }
    },

    props: {
      item: {
        type: Object,
        default: () => {}
      },
      loader: {
        type: Array | String,
        default: 'global-loader'
      },
      routeBack: null,
      title: String
    }
  }
</script>

<style scoped>

</style>
