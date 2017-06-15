<template>
<!-- Collect the nav links, forms, and other content for toggling -->
  <div class="topbar">
    <ul class="loginbar pull-right">
      <li class="hoverSelector">
        <i class="fa fa-globe"></i>
        <a>Languages</a>
        <ul class="languages hoverSelectorBlock">
          <li class="active">
            <a href="#">Deutsch <i class="fa fa-check"></i></a>
          </li>
          <li>
            <a href="#">Englisch</a>
          </li>
        </ul>
      </li>
      <li class="topbar-devider"></li>
      <li v-if="user == undefined">
        <router-link to="/login">Login</router-link>
      </li>
      <li v-if="user != undefined" class="hoverSelector">
        <router-link to="/profile">{{ user.email }}</router-link>
        <router-link to="/logout"> ({{ $t('common.logout') }})</router-link>
      </li>
    </ul>
  </div>
<!--/navbar-collapse-->
</template>

<script>
export default {
  name: 'topbar',
  props: {
    user: Object
  }
}
</script>

<style lang="css">
</style>
