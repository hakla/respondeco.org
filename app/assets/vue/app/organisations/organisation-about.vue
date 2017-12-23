<template>
  <div class="col-lg-12">
    <!-- User Block -->
    <div class="g-brd-around g-brd-gray-light-v4 g-pa-20 g-mb-40">
      <div class="row">
        <div class="col-lg-12">
          <!-- User Details -->
          <div class="d-flex align-items-center justify-content-sm-between g-mb-5">
            <h2 class="g-font-weight-300 g-mr-10">{{ organisation.name }}</h2>
            <ul class="list-inline mb-0">
              <li class="list-inline-item g-mx-2">
                <a class="u-icon-v1 u-icon-size--sm u-icon-slide-up--hover g-color-gray-light-v1 g-bg-gray-light-v5 g-color-gray-light-v1--hover rounded-circle"
                  href="#">
                  <i class="g-font-size-default g-line-height-1 u-icon__elem-regular fa fa-facebook"></i>
                  <i class="g-font-size-default g-line-height-0_8 u-icon__elem-hover fa fa-facebook"></i>
                </a>
              </li>
              <li class="list-inline-item g-mx-2">
                <a class="u-icon-v1 u-icon-size--sm u-icon-slide-up--hover g-color-gray-light-v1 g-bg-gray-light-v5 g-color-gray-light-v1--hover rounded-circle"
                  href="#">
                  <i class="g-font-size-default g-line-height-1 u-icon__elem-regular fa fa-twitter"></i>
                  <i class="g-font-size-default g-line-height-0_8 u-icon__elem-hover fa fa-twitter"></i>
                </a>
              </li>
              <li class="list-inline-item g-mx-2">
                <a class="u-icon-v1 u-icon-size--sm u-icon-slide-up--hover g-color-gray-light-v1 g-bg-gray-light-v5 g-color-gray-light-v1--hover rounded-circle"
                  href="#">
                  <i class="g-font-size-default g-line-height-1 u-icon__elem-regular fa fa-dribbble"></i>
                  <i class="g-font-size-default g-line-height-0_8 u-icon__elem-hover fa fa-dribbble"></i>
                </a>
              </li>
              <li class="list-inline-item g-mx-2">
                <a class="u-icon-v1 u-icon-size--sm u-icon-slide-up--hover g-color-gray-light-v1 g-bg-gray-light-v5 g-color-gray-light-v1--hover rounded-circle"
                  href="#">
                  <i class="g-font-size-default g-line-height-1 u-icon__elem-regular fa fa-linkedin"></i>
                  <i class="g-font-size-default g-line-height-0_8 u-icon__elem-hover fa fa-linkedin"></i>
                </a>
              </li>
            </ul>
          </div>
          <!-- End User Details -->

          <!-- User Info -->
          <ul class="list-inline g-font-weight-300">
            <li class="list-inline-item g-mr-20">
              <i class="icon-location-pin g-pos-rel g-top-1 g-color-gray-dark-v5 g-mr-5"></i> {{ organisation.location }}
            </li>
            <li class="list-inline-item g-mr-20">
              <i class="icon-envelope g-pos-rel g-top-1 g-color-gray-dark-v5 g-mr-5"></i>
              <a class="g-color-main g-color-primary--hover" href="#">{{ organisation.email }}</a>
            </li>
          </ul>
          <!-- End User Info -->

          <hr class="g-brd-gray-light-v4 g-my-20">

          <p class="lead g-line-height-1_8">
            {{ organisation.description }}
          </p>
        </div>
      </div>
    </div>
    <!-- End User Block -->
  </div>
</template>
<script>
  import { mapGetters } from 'vuex'
  import Utils from 'common/utils'

  export default {
    name: 'OrganisationAbout',

    computed: {
      ...mapGetters(['organisation'])
    },

    created () {
      console.log(this)
    },

    methods: {
      ...Utils.methods()
    },

    props: ['model']
  }

</script>
