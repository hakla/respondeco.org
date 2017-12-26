<script src="../mixins/item-page.js"></script>
<template>
  <div>
    <!-- liked -->
    <div class="form-group">
      <label>
        <strong>{{ label }}</strong>

        -

        <label class="mr-1">
          Like
        </label>
        <label class="switch switch-text switch-pill switch-primary">
          <input type="checkbox" class="switch-input" :checked="value.liked" v-model="value.liked" @change="update()">
          <span class="switch-label" data-on="Ja" data-off="Nop"></span>
          <span class="switch-handle"></span>
        </label>
      </label>

      <div>
        <textarea class="form-control" rows="5" v-model="value.testimonial" @input="update()"></textarea>
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    name: 'admin-rating',

    methods: {
      update () {
        this.$emit('input', this.value)
      }
    },

    mounted () {
      this.$emit('input', this.value)
    },

    props: {
      label: String,
      type: {
        type: String,
        default: () => {
          return 'edit'
        }
      },
      value: {
        type: Object,
        default: () => {
          return {
            id: null,
            liked: true,
            testimonial: ''
          }
        }
      }
    }
  }
</script>

<style scoped>
</style>
