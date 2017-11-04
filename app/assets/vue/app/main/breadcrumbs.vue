<template>
  <div class="breadcrumbs-v3 img-v1" :style="backgroundImage">
    <div class="container text-center">
      <p>{{ breadcrumb }}</p>
      <h1>{{ heading }}</h1>
    </div><!--/end container-->
  </div>
</template>

<script>
  import { getIdFromURL } from 'vue-youtube-embed'
  import Utils from 'common/utils'

  export default {

    name: 'breadcrumbs',

    computed: {

      backgroundImage () {
        return Utils.backgroundImage(this.background)
      }

    },

    props: ['background', 'breadcrumb', 'heading']

  }
</script>
