<template>
  <div class="container-fluid">
    <div class="animated fadeIn">
      <div class="row">
        <slot></slot>
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    name: 'admin-page',

    props: ['title']
  }
</script>

<style scoped>

</style>
