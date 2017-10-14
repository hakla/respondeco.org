<template>
  <div>
    <div v-if="type == 'edit'">
      <div class="form-group">
        <div class="col-xs-12 text-center rating-title">
          <h4>Bewertung von {{ name }} </h4>
        </div>
      </div>
      <!-- liked -->
      <div class="form-group">
        <div class="col-xs-12">
          <label class="control-label col-md-3 col-sm-3 col-xs-12" :for="name">Like <span class="required">*</span>
          </label>
          <div class="col-md-6 col-sm-6 col-xs-12">
            <div class="checkbox">
              <input type="checkbox" :id="name" :checked="value.liked" v-model="value.liked" @change="update()">
            </div>
          </div>
        </div>
      </div>

      <!-- Bewertung -->
      <div class="form-group">
        <label class="control-label col-md-3 col-sm-3 col-xs-12" for="first-name">Bewertung <span
          class="required">*</span>
        </label>
        <div class="col-md-6 col-sm-6 col-xs-12">
          <input type="text" id="first-name" required="required" class="form-control col-md-7 col-xs-12"
                 v-model="value.testimonial" @input="update()">
        </div>
      </div>
    </div>
    <div v-if="type == 'public'">

    </div>
  </div>
</template>

<script>
  export default {
    name: "Rating",

    methods: {
      update() {
        this.$emit('input', this.value)
      }
    },

    mounted () {
      this.$emit('input', this.value)
    },

    props: {
      name: String,
      value: {
        type: Object,
        default: () => {
          return {
            id: null,
            liked: false,
            testimonial: ""
          }
        }
      },
      type: {
        type: String,
        default: () => {
          return "edit"
        }
      }
    }
  }
</script>

<style scoped>
  .checkbox input[type=checkbox] {
    margin-left: 0;
  }

  .rating-title {
    margin-top: 20px;
  }
</style>
