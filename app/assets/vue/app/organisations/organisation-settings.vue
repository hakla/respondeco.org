<template>
  <div class="col-lg-12">
    <ul class="nav nav-justified u-nav-v1-1 u-nav-primary g-brd-bottom--md g-brd-bottom-2 g-brd-primary g-mb-20"
        role="tablist" data-target="nav-1-1-default-hor-left-underline" data-tabs-mobile-type="slide-up-down"
        data-btn-classes="btn btn-md btn-block rounded-0 u-btn-outline-primary g-mb-20">
      <li class="nav-item">
        <router-link :to="{ name: 'organisation-settings-profile', params: { id: organisation.id } }" class="nav-link g-py-10">
          <i class="icon-user"></i> {{ $t('organisation.settings.profile.title') }}
        </router-link>
      </li>
      <li class="nav-item">
        <router-link :to="{ name: 'organisation-settings-security', params: { id: organisation.id } }" class="nav-link g-py-10">
          <i class="icon-key"></i>
          {{ $t('organisation.settings.security.title') }}
        </router-link>
      </li>
    </ul>

    <router-view></router-view>
  </div>
</template>
<script>
  import { mapGetters } from 'vuex'
  import { router } from 'app/router'

  export default {
    name: 'OrganisationSettings',

    computed: {
      ...mapGetters(['organisation'])
    }
  }
</script>
