<template>
  <header id="js-header" class="u-header u-header--static--lg u-header--change-appearance--lg">
    <div class="u-header__section u-header__section--light g-bg-white g-transition-0_3 g-py-10 g-py-0--lg u-shadow-v18">
      <nav class="js-mega-menu navbar navbar-expand-lg py-0">
        <div class="container">
          <!-- Responsive Toggle Button -->
          <button
            class="navbar-toggler navbar-toggler-right btn g-line-height-1 g-brd-none g-pa-0 g-pos-abs g-top-3 g-right-0"
            type="button" aria-label="Toggle navigation" aria-expanded="false" aria-controls="navBar"
            data-toggle="collapse" data-target="#navBar">
              <span class="hamburger hamburger--slider">
            <span class="hamburger-box">
              <span class="hamburger-inner"></span>
              </span>
              </span>
          </button>
          <!-- End Responsive Toggle Button -->

          <!-- Logo -->
          <router-link to="/" class="logo">
            <img src="/assets/images/logo-small.png" alt="Logo">
          </router-link>
          <!-- End Logo -->

          <!-- Navigation -->
          <respondeco-menu></respondeco-menu>
          <!-- End Navigation -->
        </div>
      </nav>
    </div>
  </header>
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

      $(window).on('load', function () {
        // initialization of header
        $.HSCore.components.HSHeader.init($('#js-header'));
        $.HSCore.helpers.HSHamburgers.init('.hamburger');
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
