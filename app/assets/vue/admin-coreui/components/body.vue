<template>
  <div class="app-body">
    <admin-sidebar></admin-sidebar>

    <!-- Main content -->
    <main class="main">
      <ol class="breadcrumb">
        {{ $route.meta.title }}
      </ol>

      <slot></slot>
    </main>
  </div>
</template>

<script>
  import AdminSidebar from './sidebar'

  export default {
    name: 'admin-body',

    components: {
      AdminSidebar
    }
  }
</script>

<style scoped>

</style>
