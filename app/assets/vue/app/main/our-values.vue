<template>

  <div class="shortcode-html">
    <!-- Icon Blocks -->
    <div class="row">
      <div class="col-lg-6 g-pb-40">
        <h2 class="h3 g-color-black g-font-weight-600 text-uppercase mb-0">{{ $t('home.our_values.heading') }}</h2>
        <span class="d-block g-color-primary g-font-weight-300 g-font-size-20 g-mb-30"></span>
        <div class="row">
          <div class="col-md-4 g-mb-30 g-mb-0--md">
            <!-- Icon Blocks -->
            <div class="g-bg-gray-light-v4 g-color-black text-center g-rounded-10 g-py-25">
              <!--<i class="d-block display-2 g-line-height-1 fa fa-finance g-mb-10"></i>-->
              <respondeco-icon class="d-block display-2 g-line-height-1 g-mb-10" icon="fal bullhorn"></respondeco-icon>
              <span class="g-font-weight-700 text-uppercase">{{ $t('home.our_values.icon1.title') }}</span>
            </div>
            <!-- Icon Blocks -->
          </div>
          <div class="col-md-8">
            <!-- Icon Blocks Info -->
            <p>{{ $t('home.our_values.icon1.message') }}</p>
            <ul class="list-unstyled mb-0">
              <li class="g-mb-5">
                <respondeco-icon class="g-color-primary g-mr-3" icon="fal angle-right"></respondeco-icon>
                {{ $t('home.our_values.icon1.list.item1') }}
              </li>
              <li class="g-mb-5">
                <respondeco-icon class="g-color-primary g-mr-3" icon="fal angle-right"></respondeco-icon>
                {{ $t('home.our_values.icon1.list.item2') }}
              </li>
              <li class="g-mb-5">
                <respondeco-icon class="g-color-primary g-mr-3" icon="fal angle-right"></respondeco-icon>
                {{ $t('home.our_values.icon1.list.item3') }}
              </li>
            </ul>
            <!-- End Icon Blocks Info -->
          </div>
        </div>
      </div>
      <div class="col-lg-6 g-pb-40">
        <!-- Icon Blocks -->
        <div class="media g-mb-20">
          <div class="d-flex mr-4">
            <span class="u-icon-v3 u-icon-size--lg g-bg-gray-light-v4 g-color-black g-rounded-10">
              <respondeco-icon icon="fal search"></respondeco-icon>
            </span>
          </div>
          <div class="media-body">
            <h3 class="h5 g-mb-5">{{ $t('home.our_values.icon2.title') }}</h3>
            <p class="g-color-gray-dark-v4">{{ $t('home.our_values.icon2.message') }}</p>
          </div>
        </div>
        <!-- End Icon Blocks -->

        <!-- Icon Blocks -->
        <div class="media g-mb-20">
          <div class="d-flex mr-4">
            <span class="u-icon-v3 u-icon-size--lg g-bg-gray-light-v4 g-color-black g-rounded-10">
              <respondeco-icon icon="fal presentation"></respondeco-icon>
            </span>
          </div>
          <div class="media-body">
            <h3 class="h5 g-mb-5">{{ $t('home.our_values.icon3.title') }}</h3>
            <p class="g-color-gray-dark-v4">{{ $t('home.our_values.icon3.message') }}</p>
          </div>
        </div>
        <!-- End Icon Blocks -->

        <!-- Icon Blocks -->
        <div class="media">
          <div class="d-flex mr-4">
            <span class="u-icon-v3 u-icon-size--lg g-bg-gray-light-v4 g-color-black g-rounded-10">
              <respondeco-icon icon="fal handshake"></respondeco-icon>
            </span>
          </div>
          <div class="media-body">
            <h3 class="h5 g-mb-5">{{ $t('home.our_values.icon4.title') }}</h3>
            <p class="g-color-gray-dark-v4">{{ $t('home.our_values.icon4.message') }}</p>
          </div>
        </div>
        <!-- End Icon Blocks -->
      </div>
    </div>
    <!-- End Icon Blocks -->
  </div>

</template>

<script>
  export default {
    name: 'respondeco-our-values'
  }
</script>

<style scoped>

</style>
