<template>
  <div class="sidebar">
    <nav class="sidebar-nav">
      <ul class="nav">
        <li class="nav-title">
          Administration
        </li>
        <li class="nav-item">
          <router-link active-class="active" class="nav-link" to="/accounts" @click="hideSidebar">
            <i class="icon-people"></i>
            Accounts
          </router-link>
        </li>
        <li class="nav-item">
          <router-link active-class="active" class="nav-link" to="/organisations" @click="hideSidebar">
            <i class="icon-briefcase"></i>
            Organisationen
          </router-link>
        </li>
        <li class="nav-item">
          <router-link active-class="active" class="nav-link" to="/projects" @click="hideSidebar">
            <i class="icon-diamond"></i>
            Projekte
          </router-link>
        </li>
        <li class="nav-item">
          <router-link active-class="active" class="nav-link" to="/finishedProjects" @click="hideSidebar">
            <i class="icon-trophy"></i>
            Abgeschlossene Projekte
          </router-link>
        </li>
        <li class="nav-title">
          Weitere Links
        </li>
        <li class="nav-item">
          <a class="nav-link" href="/app/">Zu respondeco.org</a>
        </li>
      </ul>
    </nav>
  </div>
</template>

<script>
  export default {
    name: 'admin-sidebar',

    methods: {
      hideSidebar () {
        $('.app').removeClass('sidebar-mobile-show')
      }
    }
  }
</script>

<style scoped>

</style>
