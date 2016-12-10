<template>
<!-- Collect the nav links, forms, and other content for toggling -->
<div class="collapse navbar-collapse navbar-responsive-collapse">
  <div class="menu-container">
    <ul class="nav navbar-nav">
      <li>
        <router-link to="/projects">{{ $tc('common.project', 2) }}</router-link>
      </li>
      <li>
        <router-link to="/organisations">{{ $tc('common.organisation', 2) }}</router-link>
      </li>
    </ul>
  </div>
</div>
<!--/navbar-collapse-->
</template>

<script>
export default {
  name: 'menu'
}
</script>

<style lang="css">
</style>
