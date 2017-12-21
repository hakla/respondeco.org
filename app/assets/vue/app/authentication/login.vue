<template>
  <main>
    <respondeco-header type="static"></respondeco-header>
    <section class="clearfix">
      <div class="row no-gutters align-items-center">
        <div class="col-lg-6">
        </div>

        <div class="col-lg-6">
          <div class="g-pa-50 g-mx-70--xl">
            <!-- Form -->
            <form class="g-py-15" v-on:submit.prevent="login">
              <h2 class="h3 g-color-black mb-4">Login</h2>

              <div class="mb-4">
                <div class="input-group g-brd-primary--focus">
                  <span class="input-group-addon g-width-45 g-brd-gray-light-v4 g-color-primary">
                      <i class="icon-finance-067 u-line-icon-pro"></i>
                    </span>
                  <input
                    class="form-control g-color-black g-brd-left-none g-bg-white g-brd-gray-light-v4 g-pl-0 g-pr-15 g-py-15"
                    type="email" placeholder="Username" v-model="user">
                </div>
              </div>

              <div class="mb-4">
                <div class="input-group g-brd-primary--focus">
                  <span class="input-group-addon g-width-45 g-brd-gray-light-v4 g-color-primary">
                      <i class="icon-media-094 u-line-icon-pro"></i>
                    </span>
                  <input
                    class="form-control g-color-black g-brd-left-none g-bg-white g-brd-gray-light-v4 g-pl-0 g-pr-15 g-py-15"
                    type="password" placeholder="Password" v-model="password">
                </div>
              </div>

              <div class="row justify-content-between mb-4">
                <div class="col align-self-center">
                  <label class="form-check-inline u-check g-pl-25 mb-0">
                    <input class="g-hidden-xs-up g-pos-abs g-top-0 g-left-0" type="checkbox">
                    <div class="u-check-icon-checkbox-v6 g-absolute-centered--y g-left-0">
                      <i class="fa g-rounded-2" data-check-icon=""></i>
                    </div>
                    Keep signed in
                  </label>
                </div>
                <div class="col align-self-center text-right">
                  <a class="g-color-gray-dark-v4 g-color-primary--hover" href="#">Forgot password?</a>
                </div>
              </div>

              <div class="g-mb-50">
                <button class="btn btn-md btn-block u-btn-primary rounded text-uppercase g-py-13" type="button">Login
                </button>
              </div>

              <div class="text-center g-pos-rel pb-2 g-mb-50">
                <div class="d-inline-block g-width-200 g-height-1 g-bg-gray-light-v4"></div>
                <span
                  class="u-icon-v2 u-icon-size--lg g-brd-gray-light-v4 g-color-gray-dark-v4 g-bg-white g-font-size-default rounded-circle text-uppercase g-absolute-centered g-pa-24">OR</span>
              </div>

              <div class="row justify-content-between no-gutters mb-4">
                <div class="col-6 text-right">
                  <button class="btn u-btn-outline-facebook rounded text-uppercase g-py-13 g-px-30 mr-1" type="button">
                    <i class="mr-1 fa fa-facebook"></i>
                    Facebook
                  </button>
                </div>
                <div class="col-6">
                  <button class="btn u-btn-outline-twitter rounded text-uppercase g-py-13 g-px-30 ml-1" type="button">
                    <i class="mr-1 fa fa-twitter"></i>
                    Twitter
                  </button>
                </div>
              </div>

              <p class="g-font-size-13 text-center mb-0">Don't have an account? <a class="g-font-weight-600"
                                                                                   href="page-signup-11.html">signup</a>
              </p>
            </form>
            <!-- End Form -->
          </div>
        </div>
      </div>
    </section>
  </main>
<!--/container-->
</template>

<script>
  import Authentication from 'common/authentication'
  import RespondecoHeader from 'app/main/header';
  import {
    router
  } from '../router';

  export default {
    name: 'Login',

    components: {
      RespondecoHeader
    },

    created() {
      let route = router.currentRoute.query.route || '/'

      Authentication
        .get()
        .error(error => this.error = error)
        .loggedIn(() => { router.push(route) })
    },

    data() {
      return {
        error: undefined,
        user: 'admin@respondeco.org',
        password: 'admin'
      }
    },

    methods: {
      fetchData () {

      },

      login() {
        Authentication
          .get()
          .login(this.user, this.password);
      }
    }
  }
</script>

<style lang="css">
    body {
      margin-bottom: 0 !important;
    }
</style>
