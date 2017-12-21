<template>
  <div class="container g-py-100">
    <div v-masonry transition-duration="0.3s" column-width=".masonry-grid-item" item-selector=".item" class="masonry-grid row g-mb-70">
      <div v-masonry-tile class="masonry-grid-item col-sm-6 col-lg-4 g-mb-30" :key="index" v-for="(item, index) in paginatedItems">
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

    <!-- Pagination -->
    <nav class="text-center" aria-label="Page Navigation">
      <ul class="list-inline">
        <li class="list-inline-item float-left g-hidden-xs-down">
          <a class="u-pagination-v1__item u-pagination-v1-4 g-brd-gray-light-v3 g-brd-primary--hover g-rounded-50 g-pa-7-16" href="#"
            aria-label="Previous" @click.prevent="set('previous')">
            <span aria-hidden="true">
              <i class="fa fa-angle-left g-mr-5"></i> Zurück
            </span>
            <span class="sr-only">Zurück</span>
          </a>
        </li>
        <li class="list-inline-item" :key="value" v-for="value in pages">
          <a class="u-pagination-v1__item u-pagination-v1-4 g-rounded-50 g-pa-7-14" :class="{ 'u-pagination-v1-4--active': value === page }"
            href="#" @click.prevent="set(value)">{{ value }}</a>
        </li>
        <li class="list-inline-item float-right g-hidden-xs-down">
          <a class="u-pagination-v1__item u-pagination-v1-4 g-brd-gray-light-v3 g-brd-primary--hover g-rounded-50 g-pa-7-16" href="#"
            aria-label="Next" @click.prevent="set('next')">
            <span aria-hidden="true">
              Weiter
              <i class="fa fa-angle-right g-ml-5"></i>
            </span>
            <span class="sr-only">Weiter</span>
          </a>
        </li>
      </ul>
    </nav>
    <!-- End Pagination -->
  </div>
  <!-- End Blog Classic Blocks -->
</template>

<script>
  export default {
    name: 'portfolio',

    computed: {
      pages() {
        return Math.ceil(this.items.length / this.pageSize)
      },

      paginatedItems() {
        let begin = (this.page - 1) * this.pageSize
        let end = this.page * this.pageSize

        return this.items.filter((item, index) => index >= begin && index < end)
      }
    },

    data: () => ({
      page: 1,
      pageSize: 12
    }),

    methods: {
      set(value) {
        if (value === 'previous') {
          value = this.page - 1
        } else if (value === 'next') {
          value = this.page + 1
        }

        if (value > 0 && value <= this.pages) {
          this.page = value
        }
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
