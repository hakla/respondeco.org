<template>
  <div class="container">
    <respondeco-pagination class="g-mb-100--md g-mb-50" :items="items" :page="page" :pageSize="12" @page="pageChanged" ref="topPagination" :scroll="false"></respondeco-pagination>

    <div v-masonry transition-duration="0.3s" column-width=".masonry-grid-item" item-selector=".item" class="masonry-grid row g-mb-70--md g-mb-50">
      <div class="masonry-grid-item col-sm-6 col-lg-3 g-mb-30" :key="index" v-for="(item, index) in paginatedItems" v-masonry-tile>
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
    </div>

    <respondeco-pagination :items="items" :page="page" :pageSize="12" @page="pageChanged" :scroll="$refs.topPagination"></respondeco-pagination>
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
      page: 1,
      paginatedItems: []
    }),

    methods: {
      pageChanged (page, items) {
        this.page = page
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

  .ellipsis {
    text-overflow: ellipsis;
    width: 100%;
    white-space: nowrap;
    overflow: hidden;
  }
</style>
