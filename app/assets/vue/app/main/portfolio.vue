<template>
  <div v-masonry transition-duration="0.3s" column-width=".masonry-grid-item" item-selector=".item" class="masonry-grid row g-mb-70--md g-mb-50">
    <div :class="columns" class="masonry-grid-item g-mb-30" :key="index" v-for="(item, index) in items" v-masonry-tile>
      <article class="u-shadow-v11">
        <router-link :to="item.href" class="u-link-v5 g-color-black g-color-primary--hover g-cursor-pointer" href="#">
          <div class="background-image img-fluid w-100" :style="'background-image: url(' + item.image + ')'">
          </div>
          <div class="g-bg-white g-pa-20">
            <h2 class="h6 g-color-black mb-0 ellipsis">
              {{ item.title }}
            </h2>
          </div>
        </router-link>
      </article>
    </div>

    <div class="col-12 text-center g-mt-50 g-mb-20 empty-list" v-if="items.length === 0">
      {{ $t('common.emptyList') }}
    </div>

    <div class="col-12 g-mt-150 text-center" v-if="callToAction">
      <router-link :to="callToAction.link">
        <unify-button
          class="btn btn-lg u-btn-outline-teal g-font-weight-600 g-letter-spacing-0_5 g-brd-2 g-rounded-0--md">
          {{ callToAction.value }}
        </unify-button>
      </router-link>
    </div>
  </div>
</template>

<script>
  export default {
    name: 'portfolio',

    props: {
      columns: {
        type: String,
        default: 'col-sm-6 col-lg-3'
      },
      callToAction: {
        type: Object
      },
      items: null,
    }
  }

</script>

<style scoped>
  .background-image {
    display: block;
    height: 200px;
    background-position: center;
    background-repeat: no-repeat;
    background-size: cover;
  }

  .h6 {
    align-items: center;
    display: flex;
    height: 60px;
  }
</style>

<style>
  .masonry-grid {
    height: auto !important;
  }

  .ellipsis {
    text-overflow: ellipsis;
    width: 100%;
    overflow: hidden;
  }

  .empty-list {
    font-size: 18px;
  }
</style>
