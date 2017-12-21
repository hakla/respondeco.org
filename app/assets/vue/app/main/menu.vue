<template>
  <div class="collapse navbar-collapse align-items-center flex-sm-row g-pt-10 g-pt-5--lg" id="navBar">
    <ul class="navbar-nav ml-auto text-uppercase g-font-weight-600 u-main-nav-v3 u-sub-menu-v3">
      <router-link to="/projects" class="nav-item g-mx-2--md g-mx-5--xl g-mb-5 g-mb-0--lg" tag="li">
        <a class="nav-link">{{ translate('common.project', 2) }}</a>
      </router-link>
      <router-link to="/organisations" class="nav-item g-mx-2--md g-mx-5--xl g-mb-5 g-mb-0--lg" tag="li">
        <a class="nav-link">{{ translate('common.organisation', 2) }}</a>
      </router-link>
      <router-link to="/login" class="nav-item g-mx-2--md g-mx-5--xl g-mb-5 g-mb-0--lg" tag="li">
        <a class="nav-link">{{ translate('common.login', 2) }}</a>
      </router-link>
    </ul>
  </div>
</template>

<script>
import Translate from 'mixins/translate'

export default {
  name: 'menu',

  data: () => ({
    routes: [{
      name: ''
    }]
  }),

  mixins: [Translate]
}
</script>

<style lang="css">
</style>
