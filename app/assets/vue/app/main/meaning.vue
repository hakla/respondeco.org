<template>
  <div>
    <div class="container g-mt-50">
      <div class="row">
        <div class="col-md-6">
          <div class="u-heading-v2-3--bottom g-brd-primary g-mb-30">
            <h2 class="h3 text-uppercase g-font-weight-300 u-heading-v2__title">{{ $t('home.meaning.vision.title')
              }}</h2>
          </div>
          <p class="lead">{{ $t('home.meaning.vision.description') }}</p>
        </div>

        <div class="col-md-6">
          <img class="img-fluid" src="/assets/images/home_vision.jpg" alt="Image Description">
        </div>
      </div>
    </div>

    <div class="container g-mt-170">
      <div class="row">
        <div class="col-md-6">
          <img class="img-fluid" src="/assets/images/home_mission.jpg" alt="Image Description">
        </div>

        <div class="col-md-6">
          <div class="u-heading-v2-3--bottom g-brd-primary g-mb-30">
            <h2 class="h3 text-uppercase g-font-weight-300 u-heading-v2__title">{{ $t('home.meaning.mission.title')
              }}</h2>
          </div>
          <p class="lead">
            {{ $t('home.meaning.mission.description') }}
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    name: 'respondeco-meaning'
  }
</script>

<style scoped>

</style>
