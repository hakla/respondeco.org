<template>
  <main>
    <respondeco-header></respondeco-header>

    <section class="dzsparallaxer auto-init height-is-based-on-content use-loading mode-scroll loaded dzsprx-readyall" data-options="{direction: 'reverse', settings_mode_oneelement_max_offset: '150'}">
      <!-- Parallax Image -->
      <div class="divimage dzsparallaxer--target w-100 u-bg-overlay g-bg-size-cover g-bg-bluegray-opacity-0_3--after" style="height: 140%; background-image: url(/assets/images/registration.jpg);"></div>
      <!-- End Parallax Image -->

      <div class="container g-pt-100 g-pb-20">
        <div class="row justify-content-between">
          <div class="col-md-6 col-lg-5 flex-md-unordered align-self-center g-mb-80">
            <div class="u-shadow-v21 g-bg-white rounded g-pa-50">
              <header class="text-center mb-4">
                <h2 class="h2 g-color-black g-font-weight-600">{{ 'common.register' | translate }}</h2>
              </header>

              <!-- Form -->
              <form class="g-py-15">
                <div class="mb-4">
                  <input class="form-control g-color-black g-bg-white g-bg-white--focus g-brd-gray-light-v4 g-brd-primary--hover rounded g-py-15 g-px-15"
                    type="text" :placeholder="translate('common.organisation')">
                </div>

                <div class="mb-4">
                  <input class="form-control g-color-black g-bg-white g-bg-white--focus g-brd-gray-light-v4 g-brd-primary--hover rounded g-py-15 g-px-15"
                    type="email" :placeholder="translate('common.email')">
                </div>

                <div class="g-mb-30">
                  <input class="form-control g-color-black g-bg-white g-bg-white--focus g-brd-gray-light-v4 g-brd-primary--hover rounded g-py-15 g-px-15"
                    type="tel" :placeholder="translate('common.password')">
                </div>

                <div class="text-center mb-5">
                  <button class="btn btn-block u-btn-primary rounded g-py-13" type="button">{{ translate('registration.go') }}</button>
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
        </div>
      </div>
    </section>

    <respondeco-footer></respondeco-footer>
  </main>
</template>

<script>
import 'unify/vendor/dzsparallaxer/dzsparallaxer'
import 'unify/vendor/dzsparallaxer/dzsparallaxer.css'

import RespondecoHeader from '../main/header'
import RespondecoFooter from '../main/footer'

import Translate from 'mixins/translate'

export default {
  name: 'Registration',

  components: {
    RespondecoHeader,
    RespondecoFooter
  },

  mixins: [Translate]
}
</script>

<style>

</style>
