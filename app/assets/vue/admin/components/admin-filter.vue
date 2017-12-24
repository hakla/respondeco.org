<template>
  <div class="col-md-5 col-sm-5 col-xs-12 form-group pull-right top_search">
    <div class="input-group">
      <input type="text" class="form-control" placeholder="Search for..." v-model="filterValue" @input="updateFilter">
      <span class="input-group-btn">
              <button class="btn btn-default" type="button">Go!</button>
          </span>
    </div>
  </div>
</template>

<script>
  export default {
    name: "admin-filter",

    data () {
      return {
        filterValue: ''
      }
    },

    methods: {
      updateFilter () {
        this.$emit('filter', this.filterValue)
      }
    },
  }
</script>

<style scoped>

</style>
