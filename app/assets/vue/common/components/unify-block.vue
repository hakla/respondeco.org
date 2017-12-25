<template>
  <div id="shortcode" class="cbp-item identity design">
    <div class="u-block-hover g-parent">
      <img class="img-fluid g-transform-scale-1_1--parent-hover g-transition-0_5 g-transition--ease-in-out" :src="image" alt="Image description">
    </div>
    <div class="u-shadow-v19 g-bg-white text-center g-pa-25 mb-1">
      <h3 class="h5 g-color-black g-font-weight-600 mb-1">{{ title }}</h3>
    </div>
  </div>
</template>

<script>
  export default {
    name: "unify-block",

    props: [
      'image',
      'text',
      'title'
    ]
  }
</script>

<style scoped>

</style>
