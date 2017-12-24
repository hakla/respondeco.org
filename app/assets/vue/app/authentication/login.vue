<template>
  <main>
    <section class="dzsparallaxer auto-init height-is-based-on-content use-loading mode-scroll loaded dzsprx-readyall" data-options="{direction: 'reverse', settings_mode_oneelement_max_offset: '150'}">
      <!-- Parallax Image -->
      <div class="divimage dzsparallaxer--target w-100 u-bg-overlay g-bg-size-cover g-bg-bluegray-opacity-0_3--after" style="height: 140%; background-image: url(/assets/images/registration.jpg);"></div>
      <!-- End Parallax Image -->

      <div class="container g-pt-100 g-pb-20">
        <div class="row justify-content-between">

          <div class="col-md-6 flex-md-first align-self-center g-mb-80">
            <div class="mb-5">
              <h1 class="h3 g-color-white g-font-weight-600 mb-3">Profitable contracts,
                <br>invoices &amp; payments for the best cases!</h1>
              <p class="g-color-white-opacity-0_8 g-font-size-12 text-uppercase">Trusted by 31,000+ users globally</p>
            </div>

            <div class="row">
              <div class="col-md-11 col-lg-9">
                <!-- Icon Blocks -->
                <div class="media mb-4">
                  <div class="d-flex mr-4">
                    <span class="align-self-center u-icon-v1 u-icon-size--lg g-color-primary">
                      <i class="icon-finance-168 u-line-icon-pro"></i>
                    </span>
                  </div>
                  <div class="media-body align-self-center">
                    <p class="g-color-white mb-0">Reliable contracts, multifanctionality &amp; best usage of Unify template</p>
                  </div>
                </div>
                <!-- End Icon Blocks -->

                <!-- Icon Blocks -->
                <div class="media mb-5">
                  <div class="d-flex mr-4">
                    <span class="align-self-center u-icon-v1 u-icon-size--lg g-color-primary">
                      <i class="icon-finance-193 u-line-icon-pro"></i>
                    </span>
                  </div>
                  <div class="media-body align-self-center">
                    <p class="g-color-white mb-0">Secure &amp; integrated options to create individual &amp; business websites</p>
                  </div>
                </div>
                <!-- End Icon Blocks -->

                <!-- Testimonials -->
                <blockquote class="u-blockquote-v1 g-color-main rounded g-pl-60 g-pr-30 g-py-25 g-mb-40">Look no further you came to the right place. Unify offers everything you have dreamed of in one package.</blockquote>
                <div class="media">
                  <img class="d-flex align-self-center rounded-circle g-width-40 g-height-40 mr-3" src="/assets/images/registration.jpg"
                    alt="Image Description">
                  <div class="media-body align-self-center">
                    <h4 class="h6 g-color-primary g-font-weight-600 g-mb-0">Alex Pottorf</h4>
                    <em class="g-color-white g-font-style-normal g-font-size-12">Web Developer</em>
                  </div>
                </div>
                <!-- End Testimonials -->
              </div>
            </div>
          </div>

          <div class="col-md-6 col-lg-5 flex-md-unordered align-self-center g-mb-80">
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
