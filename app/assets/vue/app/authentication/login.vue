<template>
  <main>
    <section class="dzsparallaxer auto-init height-is-based-on-content use-loading mode-scroll loaded dzsprx-readyall" data-options="{direction: 'reverse', settings_mode_oneelement_max_offset: '150'}">
      <!-- Parallax Image -->
      <div class="divimage dzsparallaxer--target w-100 u-bg-overlay g-bg-size-cover g-bg-bluegray-opacity-0_3--after" style="height: 140%; background-image: url(/assets/images/registration.jpg);"></div>
      <!-- End Parallax Image -->

      <div class="container g-pt-100 g-pb-20">
        <div class="row justify-content-between">

          <div class="col-md-6 offset-md-6 col-lg-5 offset-lg-7 flex-md-unordered align-self-center g-mb-80">
            <div class="u-shadow-v21 g-bg-white rounded g-pa-50">
              <header class="text-center mb-4">
                <h2 class="h2 g-color-black g-font-weight-600">{{ $t('common.login') }}</h2>
              </header>

              <!-- Form -->
              <form class="g-py-15" @submit.prevent="login">
                <alert class="g-bg-red-opacity-0_1 g-color-lightred" v-model="showAlert">{{ error }}</alert>

                <div class="mb-4">
                  <input class="form-control g-color-black g-bg-white g-bg-white--focus g-brd-gray-light-v4 g-brd-primary--hover rounded g-py-15 g-px-15"
                    type="email" v-model="user" :placeholder="$t('common.email')">
                </div>

                <div class="g-mb-30">
                  <input class="form-control g-color-black g-bg-white g-bg-white--focus g-brd-gray-light-v4 g-brd-primary--hover rounded g-py-15 g-px-15"
                    type="password" v-model="password" :placeholder="$t('common.password')">
                </div>

                <div class="text-center mb-5">
                  <unify-button class="u-btn-primary btn-block rounded g-py-13" type="submit" loading="login">
                    {{ $t('login.go') }}
                  </unify-button>
                </div>
              </form>
              <!-- End Form -->

              <footer class="text-center">
                <p class="g-color-gray-dark-v5 mb-0">{{ $t('login.notRegistered') }}
                  <router-link to='/registration' class="g-font-weight-600">{{ $t('login.goToRegistration') }}</router-link>
                </p>
              </footer>
            </div>
          </div>
        </div>
      </div>
    </section>
  </main>
<!--/container-->
</template>

<script>
  import 'unify/vendor/dzsparallaxer/dzsparallaxer'
  import 'unify/vendor/dzsparallaxer/dzsparallaxer.css'
  import VueSimpleSpinner from 'vue-simple-spinner'

  import Authentication from 'common/authentication'
  import Alert from 'app/main/alert'
  import Config from 'app/config'
  import { i18n } from 'app/i18n'

  import UnifyButton from 'common/components/unify-button'

  import { router } from '../router'

  export default {
    name: 'Login',

    components: {
      Alert,
      UnifyButton,
      VueSimpleSpinner
    },

    created() {
      let route = router.currentRoute.query.route || Config.defaultRoute

      Authentication
        .get()
        .error(error => {
          this.$endLoading('login')
          this.error = i18n.t('login.error')
          this.showAlert = true
        })
        .loggedIn(() => {
          this.$endLoading('login')
          router.push(route)
        })
    },

    data() {
      return {
        error: undefined,
        showAlert: false,
        user: 'admin@respondeco.org',
        password: 'admin'
      }
    },

    methods: {
      fetchData () {

      },

      login() {
        this.$startLoading('login')

        Authentication
          .get()
          .login(this.user, this.password)
      }
    }
  }
</script>

<style lang="css" scoped>
  .spinner {
    display: inline-block;
    vertical-align: middle;
  }
</style>
