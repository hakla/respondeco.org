<template>
  <div class="wrapper">
    <!--=== Header v6 ===-->
    <respondeco-header></respondeco-header>
    <!--=== End Header v6 ===-->

    <!--=== Content ===-->
    <transition name="fade" mode="out-in">
      <router-view></router-view>
    </transition>
    <!--=== End Content ===-->

    <!--=== Footer Version 1 ===-->
    <respondeco-footer></respondeco-footer>
  </div>
</template>

<script>
  import RespondecoHeader from './main/header';
  import RespondecoFooter from './main/footer';

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
</style>
