<template>
  <div id="nav-1-1-default-hor-left-underline">
    <!-- Edit Profile -->
    <div class="tab-pane fade active show" id="nav-1-1-default-hor-left-underline--1" role="tabpanel"
         aria-expanded="true">
      <h2 class="h4 g-font-weight-300">{{ $t('organisation.settings.profile.title') }}</h2>
      <p>{{ $t('organisation.settings.profile.description') }}</p>

      <form @submit.prevent="save">
        <unify-list>
          <!-- name -->
          <unify-list-item>
            <unify-list-item-label for="name">{{ $t('organisation.settings.name') }}</unify-list-item-label>

            <unify-list-item-content>
              <unify-form-input id="name" v-model="organisation.name"></unify-form-input>
            </unify-list-item-content>
          </unify-list-item>

          <!-- email -->
          <unify-list-item>
            <unify-list-item-label for="email">{{ $t('organisation.settings.contact') }}</unify-list-item-label>

            <unify-list-item-content>
              <unify-form-input id="email" v-model="organisation.email"></unify-form-input>
            </unify-list-item-content>
          </unify-list-item>

          <!-- location -->
          <unify-list-item>
            <unify-list-item-label for="location">{{ $t('organisation.settings.location') }}</unify-list-item-label>

            <unify-list-item-content>
              <unify-form-input id="location" v-model="organisation.location"></unify-form-input>
            </unify-list-item-content>
          </unify-list-item>

          <!-- website -->
          <unify-list-item>
            <unify-list-item-label for="website">{{ $t('organisation.settings.website') }}</unify-list-item-label>

            <unify-list-item-content>
              <unify-form-input id="website" v-model="organisation.website"></unify-form-input>
            </unify-list-item-content>
          </unify-list-item>

          <!-- website -->
          <unify-list-item>
            <unify-list-item-label for="description">{{ $t('organisation.settings.description') }}
            </unify-list-item-label>

            <unify-list-item-content>
              <unify-textarea id="description" rows="10" v-model="organisation.description"></unify-textarea>
            </unify-list-item-content>
          </unify-list-item>
        </unify-list>

        <div class="text-sm-right">
          <unify-button class="u-btn-darkgray g-mr-10" size="medium" @click="reset">
            {{ $t('common.cancel') }}
          </unify-button>
          <unify-button class="u-btn-primary g-mr-10" loading="global-loader" size="medium"
                        type="submit">
            {{ $t('common.save') }}
          </unify-button>
        </div>
      </form>
    </div>
    <!-- End Edit Profile -->

  </div>
</template>

<script>
  import { mapActions } from 'vuex'
  import { Notifications } from 'common/utils'
  import OrganisationsService from 'app/organisations/organisations-service'

  export default {
    name: "OrganisationSettingsProfile",

    data () {
      return {
        organisation: Object.assign({}, this.$store.state.organisation.current)
      }
    },

    methods: {
      ...mapActions(['current']),

      reset () {
        this.organisation = Object.assign({}, this.$store.state.organisation.current)
      },

      save () {
        Notifications.startLoading(this)

        OrganisationsService.update(this.organisation).then(
          Notifications.success(this, response => {
            this.current(this.organisation)
            this.reset()
          }),

          Notifications.error(this)
        )
      }
    }
  }
</script>

<style scoped>
  .unify-list-item-label {
    flex-basis: 200px;
  }

  .unify-list-item-content {
    flex-grow: 1;
  }
</style>
