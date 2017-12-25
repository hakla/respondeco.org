<template>
  <main>
    <notifications position="top center"></notifications>

    <!--=== Header v6 ===-->
    <respondeco-header v-if="$route.meta.header !== false"></respondeco-header>
    <!--=== End Header v6 ===-->

    <!--=== Content ===-->
    <transition name="fade" mode="out-in">
      <router-view></router-view>
    </transition>
    <!--=== End Content ===-->

    <!--=== Footer Version 1 ===-->
    <respondeco-footer v-if="$route.meta.footer !== false"></respondeco-footer>
  </main>
</template>

<script>
  import RespondecoHeader from './main/header'
  import RespondecoFooter from './main/footer'

  export default {
    name: 'app',
    components: {
      RespondecoHeader,
      RespondecoFooter
    }
  }
</script>

<style>
  @import url('https://fonts.googleapis.com/css?family=Open+Sans:300,400,600');

  .fade-enter-active,
  .fade-leave-active {
    transition: opacity .2s
  }

  .fade-enter,
  .fade-leave-active {
    opacity: 0
  }

  .notifications {
    margin-top: 30px;
  }

  .notification {
    font-size: 1rem;
    padding: 1rem;
  }
</style>
