<template>
  <header class="app-header navbar">
    <button class="navbar-toggler mobile-sidebar-toggler d-lg-none mr-auto" @click.prevent="toggleMobileSidebar" type="button">
      <span class="navbar-toggler-icon"></span>
    </button>
    <router-link to="/" class="navbar-brand">
      respondeco
    </router-link>
    <button class="navbar-toggler sidebar-toggler d-md-down-none" @click.prevent="toggleSidebar" type="button">
      <span class="navbar-toggler-icon"></span>
    </button>
  </header>
</template>

<script>
  export default {
    name: 'admin-header',

    methods: {
      toggleMobileSidebar () {
        $('.app').toggleClass('sidebar-mobile-show')
      },

      toggleSidebar () {
        $('.app').toggleClass('sidebar-hidden')
      }
    }
  }
</script>

<style scoped>

</style>
