<template>
  <div>
    <croppa
      style="border: 1px solid #ddd"
      :canvas-color="canvasColor"
      :height="height"
      :initial-image="initial"
      initial-size="contain"
      initial-position="center"
      :placeholder="placeholder"
      :placeholder-font-size="16"
      remove-button-color="black"
      v-model="croppa"
      :width="width"
      :zoom-speed="3"
      :quality="quality"
      @file-choose="update"
      @file-size-exceed="update"
      @file-type-mismatch="update"
      @image-remove="update"
      @move="update"
      @zoom="update"
    ></croppa>
  </div>
</template>

<script>
  export default {
    name: 'admin-file-chooser',

    data () {
      return {
        croppa: {}
      }
    },

    methods: {
      update () {
        this.$emit('input', this.croppa)
      }
    },

    props: {
      canvasColor: {
        type: null,
        default: '#fff'
      },
      height: {
        type: Number,
        default: 300
      },
      initial: String,
      quality: Number,
      placeholder: {
        type: String,
        default: 'Wähle ein Bild'
      },
      width: {
        type: Number,
        default: 300
      }
    }
  }
</script>

<style scoped>

</style>
