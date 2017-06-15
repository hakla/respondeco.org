<template>
  <!--=== Header ===-->
  <div class="header">
    <div class="container">
      <!-- Logo -->
      <router-link to="/" class="logo">
        <img src="/assets/images/logo-small.png" alt="Logo">
      </router-link>
      <!--  End Logo -->

      <!-- Topbar -->
      <respondeco-topbar :user="user"></respondeco-topbar>
      <!--  End Topbar -->

      <!-- Toggle get grouped for better mobile display -->
      <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-responsive-collapse">
        <span class="sr-only">Toggle navigation</span>
        <span class="fa fa-bars"></span>
      </button>
      <!-- /end container -->
      <!--  End Toggle -->
    </div>

    <!-- Collect the nav links, forms, and other content for toggling -->
    <respondeco-menu></respondeco-menu>
  </div>
  <!--=== End Header ===-->
</template>

<script>
import RespondecoMenu from './menu'
import RespondecoTopbar from './topbar'
import Authentication from 'common/authentication'

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
    RespondecoMenu,
    RespondecoTopbar
  }
}
</script>

<style lang="css">
</style>
