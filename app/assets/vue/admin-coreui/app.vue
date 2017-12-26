<template>
  <section class="app header-fixed sidebar-fixed aside-menu-fixed aside-menu-hidden">
    <notifications position="top center"></notifications>

    <admin-header></admin-header>

    <admin-body>
      <transition name="fade" mode="out-in">
        <router-view></router-view>
      </transition>
    </admin-body>

    <admin-footer></admin-footer>
  </section>
</template>

<script>
  import 'libs/core-ui/css/style.css'

  import AdminBody from './components/body'
  import AdminFooter from './components/footer'
  import AdminHeader from './components/header'

  export default {
    name: 'app',

    components: {
      AdminBody,
      AdminFooter,
      AdminHeader
    },

    data () {
      let isDev = window.respondeco === 'dev'

      return {
        isDev,
        mode: isDev ? 'Entwicklung' : 'Produktion'
      }
    }
  }
</script>

<style>
  .flex-grow-1 {
    flex-grow: 1;
  }
</style>
