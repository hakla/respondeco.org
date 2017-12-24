<template>
  <div class="form-group">
    <label class="control-label col-md-3 col-sm-3 col-xs-12" name="label">{{ label }}</label>
    <div class="col-md-6 col-sm-6 col-xs-12">
      <slot name="body"></slot>
    </div>
  </div>
</template>

<script>
  export default {
    name: "form-group",

    props: ['label', 'id']
  }
</script>

<style scoped>

</style>
