<template>
  <form @submit.prevent="submit">
    <admin-page>
      <admin-card :loader="loader">
        <span slot="header">{{ title }}</span>

        <slot></slot>

        <div class="text-right" slot="footer">
          <button @click="back" type="button" class="btn btn-link">Abbrechen</button>
          <admin-button class="btn-primary">
            Speichern
          </admin-button>
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
        if (this.routeBack != null) {
          this.$router.push(this.routeBack)
        } else {
          this.$emit('back')
        }
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
