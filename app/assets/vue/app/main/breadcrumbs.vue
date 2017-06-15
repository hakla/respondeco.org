<template>
  <div class="breadcrumbs-v3 img-v1">
    <div class="container text-center">
      <p>{{ breadcrumb }}</p>
      <h1>{{ heading }}</h1>
    </div><!--/end container-->
  </div>
</template>

<script>
  export default {
    name: 'breadcrumbs',
    props: ['breadcrumb', 'heading']
  }
</script>
