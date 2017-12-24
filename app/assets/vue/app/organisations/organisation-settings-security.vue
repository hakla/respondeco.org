<template>
  <!-- Security Settings -->
  <div class="tab-pane" id="nav-1-1-default-hor-left-underline--2">
    <h2 class="h4 g-font-weight-300">{{ $t('organisation.settings.security.title') }}</h2>
    <p class="g-mb-25">{{ $t('organisation.settings.security.description') }}</p>

    <form @submit.prevent="save">
      <unify-list>
        <!-- current password -->
        <unify-list-item>
          <unify-list-item-label for="current">{{ $t('organisation.settings.security.password.current') }}
          </unify-list-item-label>

          <unify-list-item-content>
            <unify-form-input id="current" type="password" v-model="password.oldPassword">
              <i class="icon-lock" slot="addon"></i>
            </unify-form-input>
          </unify-list-item-content>
        </unify-list-item>

        <!-- new password -->
        <unify-list-item>
          <unify-list-item-label for="new">{{ $t('organisation.settings.security.password.new') }}
          </unify-list-item-label>

          <unify-list-item-content>
            <unify-form-input id="new" type="password" v-model="password.newPassword">
              <i class="icon-lock" slot="addon"></i>
            </unify-form-input>
          </unify-list-item-content>
        </unify-list-item>

        <!-- current password -->
        <unify-list-item>
          <unify-list-item-label for="verify">{{ $t('organisation.settings.security.password.verify') }}
          </unify-list-item-label>

          <unify-list-item-content>
            <unify-form-input id="verify" type="password" v-model="password.verifiedPassword">
              <i class="icon-lock" slot="addon"></i>
            </unify-form-input>
          </unify-list-item-content>
        </unify-list-item>
      </unify-list>

      <hr class="g-brd-gray-light-v4 g-my-25">

      <div class="text-sm-right">
        <unify-button class="u-btn-darkgray g-mr-10" size="medium" @click="reset">
          {{ $t('common.cancel') }}
        </unify-button>
        <unify-button class="u-btn-primary g-mr-10" loading="global-loader" size="medium" type="submit">
          {{ $t('common.save') }}
        </unify-button>
      </div>
    </form>
  </div>
  <!-- End Security Settings -->
</template>

<script>
  import Accounts from 'common/services/accounts'
  import { Notifications } from 'common/utils'

  export default {
    name: "OrganisationSettingsSecurity",

    computed: {
      isLoading () {
        return Notifications.isLoading(vm)
      }
    },

    data () {
      return {
        password: {
          oldPassword: '',
          newPassword: '',
          verifiedPassword: ''
        }
      }
    },

    methods: {
      reset () {
        this.password = {
          current: '',
          new: '',
          verify: ''
        }
      },

      save () {
        Notifications.startLoading(this)

        Accounts.updatePassword(this.password).then(
          Notifications.success(this),
          Notifications.error(this, {
            400: this.$t('organisation.settings.security.password.badRequest'),
            403: this.$t('organisation.settings.security.password.forbidden')
          })
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
