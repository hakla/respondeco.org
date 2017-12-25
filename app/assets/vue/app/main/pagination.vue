<template>
  <nav class="text-center" aria-label="Page Navigation">
    <ul class="list-inline">
      <li class="list-inline-item float-left g-hidden-xs-down">
        <button @click.prevent="set('previous')"
                :disabled="activePage === 1"
                class="u-pagination-v1__item u-pagination-v1-4 g-brd-gray-light-v3 g-brd-primary--hover g-rounded-50 g-pa-7-16"
                type="button"
                aria-label="Previous">
          <span aria-hidden="true">
            <i class="fa fa-angle-left g-mr-5"></i> {{ $t('common.pagination.previous') }}
          </span>
          <span class="sr-only">{{ $t('common.pagination.previous') }}</span>
        </button>
      </li>
      <li class="list-inline-item" :key="value" v-for="value in pages">
        <a class="u-pagination-v1__item u-pagination-v1-4 g-rounded-50 g-pa-7-14"
           href=""
           :class="{ 'u-pagination-v1-4--active': value === activePage }"
           @click.prevent="set(value)">{{ value }}</a>
      </li>
      <li class="list-inline-item float-right g-hidden-xs-down">
        <button
          @click.prevent="set('next')"
          :disabled="activePage === pages"
          class="u-pagination-v1__item u-pagination-v1-4 g-brd-gray-light-v3 g-brd-primary--hover g-rounded-50 g-pa-7-16"
          type="button">
            <span aria-hidden="true">
              {{ $t('common.pagination.next') }}
              <i class="fa fa-angle-right g-ml-5"></i>
            </span>
          <span class="sr-only">{{ $t('common.pagination.next') }}</span>
        </button>
      </li>
    </ul>
  </nav>
</template>

<script>
  export default {
    name: "pagination",

    created () {
      this.$emit('page', this.activePage, this.paginatedItems)
    },

    computed: {
      pages () {
        return Math.ceil(this.items.length / this.pageSize)
      },

      paginatedItems () {
        let begin = (this.activePage - 1) * this.pageSize
        let end = this.activePage * this.pageSize

        return this.items.filter((item, index) => index >= begin && index < end)
      }
    },

    data () {
      return {
        activePage: this.page,
      }
    },

    methods: {
      set (value) {
        if (value === 'previous') {
          value = this.activePage - 1
        } else if (value === 'next') {
          value = this.activePage + 1
        }

        if (value > 0 && value <= this.pages) {
          this.activePage = value

          this.$emit('page', this.activePage, this.paginatedItems)

          if (this.scroll) {
            let scrollTop = 0

            if (this.scroll.$el) {
              // Vue reference
              scrollTop = $(this.scroll.$el).offset().top - 50
            }

            $('html, body').animate({
              scrollTop: scrollTop
            })
          }
        }
      }
    },

    props: {
      items: {
        type: Array,
        default: []
      },
      page: Number,
      pageSize: Number,
      scroll: {
        type: null,
        default: true
      }
    },

    watch: {
      page (val) {
        this.activePage = val
      }
    }
  }
</script>

<style scoped>
  button {
    background-color: transparent;
  }

  button[disabled] {
    cursor: not-allowed;
    opacity: .65;
  }

  button[disabled]:hover {
    border-color: #ddd !important;
    color: #333 !important;
  }
</style>
