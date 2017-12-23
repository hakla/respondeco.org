<template>
  <div class="collapse navbar-collapse align-items-center flex-sm-row g-pt-10 g-pt-5--lg" id="navBar">
    <ul class="navbar-nav ml-auto text-uppercase g-font-weight-600 u-main-nav-v3 u-sub-menu-v2">
      <router-link to="/projects" active-class="active" class="nav-item g-mx-2--md g-mx-5--xl g-mb-5 g-mb-0--lg" tag="li">
        <a class="nav-link">{{ translate('common.project', 2) }}</a>
      </router-link>
      <router-link to="/organisations" class="nav-item g-mx-2--md g-mx-5--xl g-mb-5 g-mb-0--lg" tag="li">
        <a class="nav-link">{{ translate('common.organisation', 2) }}</a>
      </router-link>
      <router-link to="/login" class="nav-item g-mx-2--md g-mx-5--xl g-mb-5 g-mb-0--lg" tag="li" v-if="!activeUser">
        <a class="nav-link">{{ translate('common.login', 2) }}</a>
      </router-link>
      <li active-class="active" class="hs-has-sub-menu nav-item g-mx-2--md g-mx-5--xl g-mb-5 g-mb-0--lg user-menu" v-else>
        <a class="nav-link" id="nav-link-1" aria-haspopup="true" aria-expanded="false" aria-controls="nav-submenu-1">Angemeldet als {{ activeUser.name }}</a>

        <ul class="hs-sub-menu list-unstyled g-text-transform-none g-brd-top g-brd-primary g-brd-top-2 g-mt-10 g-min-width-300" id="nav-submenu-1" aria-labelledby="nav-link-1">
          <li class="dropdown-item">
            <router-link :to="{ name: 'organisation-settings-profile', params: { id: activeUser.organisationId } }"  class="nav-link" href="#">{{ $t('menu.settings') }}</router-link>
          </li>
          <li class="dropdown-item">
            <router-link to="/logout" class="nav-link" href="#">{{ $t('menu.logout') }}</router-link>
          </li>
        </ul>
      </li>
    </ul>
  </div>
</template>

<script>
import Translate from 'mixins/translate'
import { mapGetters } from 'vuex'

export default {
  name: 'RespondecoMenu',

  computed: {
    ...mapGetters([
      'activeUser'
    ])
  },

  data: () => ({
    routes: [{
      name: ''
    }]
  }),

  mixins: [Translate]
}
</script>

<style lang="css" scoped>
  .navbar .u-main-nav-v3 .hs-sub-menu .nav-link {
    border-bottom: 4px solid transparent !important;
  }

  .user-menu > a {
    cursor: default;
  }
</style>
