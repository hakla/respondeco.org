<template>
  <div v-masonry transition-duration="0.3s" column-width=".masonry-grid-item" item-selector=".item" class="masonry-grid row g-mb-70--md g-mb-50">
    <div :class="columns" class="masonry-grid-item g-mb-30" :key="index" v-for="(item, index) in items" v-masonry-tile>
      <article class="u-shadow-v11">
        <router-link :to="item.href" class="u-link-v5 g-color-black g-color-primary--hover g-cursor-pointer" href="#">
          <img class="img-fluid w-100" :src="item.image">
          <div class="g-bg-white g-pa-30">
            <h2 class="h5 g-color-black g-font-weight-600 mb-3 ellipsis">
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

<style>
  .masonry-grid {
    height: auto !important;
  }

  .ellipsis {
    text-overflow: ellipsis;
    width: 100%;
    white-space: nowrap;
    overflow: hidden;
  }

  .empty-list {
    font-size: 18px;
  }
</style>
