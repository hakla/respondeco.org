<template>
  <main>
    <section class="dzsparallaxer auto-init height-is-based-on-content use-loading mode-scroll loaded dzsprx-readyall"
             data-options="{direction: 'reverse', settings_mode_oneelement_max_offset: '150'}">
      <!-- Parallax Image -->
      <div class="divimage dzsparallaxer--target w-100 u-bg-overlay g-bg-size-cover g-bg-bluegray-opacity-0_3--after"
           style="height: 140%; background-image: url(/assets/images/registration.jpg);"></div>
      <!-- End Parallax Image -->

      <div class="container g-pt-100 g-pb-20">
        <div class="row justify-content-between">
          <div class="col-md-6 col-lg-5 flex-md-unordered align-self-center g-mb-80">
            <div class="u-shadow-v21 g-bg-white rounded g-pa-50">
              <header class="text-center mb-4">
                <h2 class="h2 g-color-black g-font-weight-600">{{ 'common.register' | translate }}</h2>
              </header>

              <!-- Form -->
              <form class="g-py-15" @submit.prevent="submit">
                <div class="mb-4" :class="{ 'u-has-error-v1': errors.has('organisation') }">
                  <input
                    class="form-control g-color-black g-bg-white--focus g-brd-gray-light-v4 g-brd-primary--hover rounded g-py-15 g-px-15"
                    :class="{ 'danger': errors.has('organisation') }"
                    type="text" name="organisation" v-model="organisation" v-validate="'required'"
                    :placeholder="translate('common.organisation')">
                  <small v-show="errors.has('organisation')" class="form-control-feedback">Pflichtfeld!
                  </small>
                </div>

                <div class="mb-4" :class="{ 'u-has-error-v1': errors.has('email') }">
                  <input
                    class="form-control g-color-black g-bg-white--focus g-brd-gray-light-v4 g-brd-primary--hover rounded g-py-15 g-px-15"
                    :class="{ 'danger': errors.has('email') }"
                    type="email" name="email" v-model="email" v-validate="'required|email'"
                    :placeholder="translate('common.email')">
                  <small v-show="errors.has('email')" class="form-control-feedback">Pflichtfeld!
                  </small>
                </div>

                <div class="g-mb-30" :class="{ 'u-has-error-v1': errors.has('password') }">
                  <input
                    class="form-control g-color-black g-bg-white--focus g-brd-gray-light-v4 g-brd-primary--hover rounded g-py-15 g-px-15"
                    :class="{ 'danger': errors.has('password') }"
                    type="password" name="password" v-model="password" v-validate="'required'"
                    :placeholder="translate('common.password')">
                  <small v-show="errors.has('password')" class="form-control-feedback">Pflichtfeld!
                  </small>
                </div>

                <div class="g-mb-30" :class="{ 'u-has-error-v1': errors.has('passwordConfirmation') }">
                  <input
                    class="form-control g-color-black g-bg-white--focus g-brd-gray-light-v4 g-brd-primary--hover rounded g-py-15 g-px-15"
                    :class="{ 'danger': errors.has('passwordConfirmation') }"
                    type="password" name="passwordConfirmation" v-model="passwordConfirmation" v-validate="'required'"
                    :placeholder="translate('common.passwordConfirmation')">
                  <small v-show="errors.has('passwordConfirmation')" class="form-control-feedback">Pflichtfeld!
                  </small>
                </div>

                <div class="text-center mb-5">
                  <unify-button class="btn btn-block u-btn-primary rounded g-py-13" type="submit" loading="registering">{{
                    translate('registration.go') }}
                  </unify-button>
                </div>
              </form>
              <!-- End Form -->

              <footer class="text-center">
                <p class="g-color-gray-dark-v5 mb-0">{{ translate('registration.existingAccount') }}
                  <router-link to='login' class="g-font-weight-600">{{ translate('registration.login') }}</router-link>
                </p>
              </footer>
            </div>
          </div>
        </div>
      </div>
    </section>
  </main>
</template>

<script>
  import 'unify/vendor/dzsparallaxer/dzsparallaxer'
  import 'unify/vendor/dzsparallaxer/dzsparallaxer.css'
  import Component, { mixins } from 'vue-class-component'
  import Accounts from 'common/services/accounts'

  import Translate from 'mixins/translate'
  import { Notifications } from 'common/utils'

  @Component
  export default class Registration extends mixins(Translate) {
    email = ''
    organisation = ''
    password = ''
    passwordConfirmation = ''

    get isLoading () {
      return Notifications.isLoading(this)
    }

    submit () {
      this.$validator.validateAll().then((result) => {
        if (result) {
          Notifications.startLoading(this, 'registering')

          Accounts.save({
            email: this.email,
            name: this.organisation,
            password: this.password
          }).then(
            Notifications.success(this, () => {
              this.$router.push('/login')
            }, 'registering'),
            Notifications.error(this, {
              400: {
                'organisation.exists': this.$t('registration.errors.organisation_exists'),
                'user.already.registered.with.email': this.$t('registration.errors.user_already_registered'),
                'user.already.registered.with.name': this.$t('registration.errors.user_already_registered'),
                'user.creation.failed': this.$t('registration.errors.user_creation_failed')
              }
            }, 'registering')
          )
        }
      })
    }
  }
</script>

<style>

</style>
