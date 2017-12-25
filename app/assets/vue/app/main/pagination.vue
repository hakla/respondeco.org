<template>
  <nav class="text-center" aria-label="Page Navigation">
    <ul class="list-inline">
      <li class="list-inline-item float-left g-hidden-xs-down">
        <button @click.prevent="set('previous')"
                :disabled="page == 1"
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
           :class="{ 'u-pagination-v1-4--active': value === page }"
           @click.prevent="set(value)">{{ value }}</a>
      </li>
      <li class="list-inline-item float-right g-hidden-xs-down">
        <button
          @click.prevent="set('next')"
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
      this.$emit('page', this.paginatedItems)
    },

    computed: {
      pages () {
        return Math.ceil(this.items.length / this.pageSize)
      },

      paginatedItems () {
        let begin = (this.page - 1) * this.pageSize
        let end = this.page * this.pageSize

        return this.items.filter((item, index) => index >= begin && index < end)
      }
    },

    data () {
      return {
        page: 1,
      }
    },

    methods: {
      set (value) {
        if (value === 'previous') {
          value = this.page - 1
        } else if (value === 'next') {
          value = this.page + 1
        }

        if (value > 0 && value <= this.pages) {
          this.page = value

          $('html, body').animate({
            scrollTop: 0
          })
        }
      }
    },

    props: {
      items: {
        type: Array,
        default: []
      },
      pageSize: Number
    },

    watch: {
      paginatedItems (val) {
        this.$emit('page', val)
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
