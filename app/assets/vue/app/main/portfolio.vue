<template>
  <div class="container g-py-100--md g-py-40">
    <respondeco-pagination :items="items" :pageSize="9" @page="pageChanged" class="g-mb-100"></respondeco-pagination>

    <div v-masonry transition-duration="0.3s" column-width=".masonry-grid-item" item-selector=".item" class="masonry-grid row g-mb-70">
      <div class="masonry-grid-item col-sm-6 col-lg-4 g-mb-30" :key="index" v-for="(item, index) in paginatedItems" v-masonry-tile>
        <article class="u-shadow-v11">
          <router-link :to="item.href" class="u-link-v5 g-color-black g-color-primary--hover g-cursor-pointer" href="#">
            <img class="img-fluid w-100" :src="item.image">
            <div class="g-bg-white g-pa-30">
              <h2 class="h5 g-color-black g-font-weight-600 mb-3">
                {{ item.title }}
              </h2>
            </div>
          </router-link>
        </article>
      </div>
    </div>

    <respondeco-pagination :items="items" :pageSize="9" @page="pageChanged"></respondeco-pagination>
  </div>
</template>

<script>
  import RespondecoPagination from './pagination'

  export default {
    name: 'portfolio',

    components: {
      RespondecoPagination
    },

    data: () => ({
      paginatedItems: []
    }),

    methods: {
      pageChanged (items) {
        this.paginatedItems = items
      }
    },

    props: ['items']
  }

</script>

<style>
  .masonry-grid {
    height: auto !important;
  }
</style>
