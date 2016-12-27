<template>
<div class="header header-v6 header-classic-white header-sticky">
  <!-- Navbar -->
  <div class="navbar mega-menu" role="navigation">
    <div class="container">
      <!-- Brand and toggle get grouped for better mobile display -->
      <div class="menu-container">
        <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-responsive-collapse">
  				<span class="sr-only">Toggle navigation</span>
  				<span class="icon-bar"></span>
  				<span class="icon-bar"></span>
  				<span class="icon-bar"></span>
  			</button>

        <div class="topbar">
          <ul class="loginbar pull-right">
            <li class="hoverSelector">
              <i class="fa fa-globe"></i>
              <a>Languages</a>
              <ul class="languages hoverSelectorBlock">
                <li class="active">
                  <a href="#">English <i class="fa fa-check"></i></a>
                </li>
                <li><a href="#">Spanish</a></li>
                <li><a href="#">Russian</a></li>
                <li><a href="#">German</a></li>
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

        <!-- Navbar Brand -->
        <div class="navbar-brand">
          <router-link to="/">
            <img class="shrink-logo" src="/assets/unify/assets/img/logo1-default.png" alt="Logo">
          </router-link>
        </div>
        <!-- ENd Navbar Brand -->
      </div>

      <respondeco-menu></respondeco-menu>
    </div>
  </div>
  <!-- End Navbar -->
</div>
</template>

<script>
import RespondecoMenu from './menu';
import Authentication from 'common/authentication';

export default {
  created() {
    Authentication.activeUser().then(user => {
      console.log('blabla');
      console.log(user);
      this.user = user;
    });
  },
  data() {
    return {
      user: undefined
    }
  },
  name: 'RespondecoHeader',
  components: {
    RespondecoMenu
  }
}
</script>

<style lang="css">
</style>
