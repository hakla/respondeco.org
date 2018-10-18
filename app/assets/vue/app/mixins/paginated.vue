<template>
  <section class="g-pb-80--md g-pb-150--lg g-pb-60">
    <section
      class="g-mb-150 dzsparallaxer auto-init height-is-based-on-content use-loading mode-scroll loaded dzsprx-readyall"
      data-options="{direction: 'reverse', settings_mode_oneelement_max_offset: '150'}">
      <!-- Parallax Image -->
      <div class="divimage dzsparallaxer--target w-100 g-bg-cover g-bg-white-gradient-opacity-v3--after"
           style="height: 140%; background-image: url('/assets/images/home_bg.jpg'); transform: translate3d(0px, -43.4987px, 0px);"></div>
      <!-- End Parallax Image -->

      <div class="container text-center g-py-100--md g-py-80">
        <h2 class="h1 text-uppercase g-font-weight-300 g-mb-30">{{ $tc(this.heading, 2) }}</h2>

        <!-- Search Form -->
        <form class="g-width-60x--md mx-auto">
          <div class="form-group g-mb-20">
            <div class="input-group u-shadow-v21 rounded g-mb-15">
              <input v-model="query"
                     class="form-control form-control-md g-brd-white g-font-size-16 border-right-0 pr-0 g-py-15"
                     type="text" @input="debounce">
              <div class="clear-query" @click="clearQuery" v-if="query">
                <respondeco-icon icon="fal times"></respondeco-icon>
              </div>
              <div
                class="input-group-addon d-flex align-items-center g-bg-white g-brd-white g-color-gray-light-v1 g-pa-2">
                <button class="btn u-btn-primary g-font-size-16 g-py-15 g-px-20" type="submit">
                  <i class="icon-magnifier g-pos-rel g-top-1"></i>
                </button>
              </div>
            </div>
          </div>
        </form>
        <!-- End Search Form -->
      </div>
    </section>

    <transition name="fade" mode="out-in">
      <section class="container" v-if="!$isLoading('initial')">
        <div class="row">
          <div class="col-lg-3 g-pr-40--lg g-mb-50 g-mb-0--lg" v-if="this.filter">
            <!-- Categories -->
            <h2 class="h5 text-uppercase g-color-gray-dark-v1">{{ $t('paginated.filter.headings.categories') }}</h2>
            <hr class="g-brd-gray-light-v4 g-my-15">
            <ul class="list-unstyled g-mb-40">
              <li class="my-3" v-for="category in categories" :key="category.label">
                <a class="d-flex justify-content-between u-link-v5 g-color-gray-dark-v1 g-parent g-cursor-pointer"
                   @click.prevent="updateChecked(category)"
                   :class="{ 'font-weight-bold': category.checked }">
                  {{ category.label }}
                </a>
              </li>
            </ul>
            <!-- End Categories -->

            <!-- Status -->
            <h2 class="h5 text-uppercase g-color-gray-dark-v1">{{ $t('paginated.filter.headings.status') }}</h2>
            <hr class="g-brd-gray-light-v4 g-my-15">
            <div class="btn-group justified-content g-mb-40">
              <label class="u-check">
                <input class="g-hidden-xs-up g-pos-abs g-top-0 g-left-0" name="radGroupBtn1_1" type="radio" :value="1"
                       v-model="status" @change="update">
                <span
                  class="btn btn-block u-btn-outline-lightgray g-color-white--checked g-bg-primary--checked rounded-0">
                  {{ $t('paginated.filter.status.open') }}
                </span>
              </label>
              <label class="u-check">
                <input class="g-hidden-xs-up g-pos-abs g-top-0 g-left-0" name="radGroupBtn1_1" type="radio" :value="2"
                       v-model="status" @change="update">
                <span
                  class="btn btn-block u-btn-outline-lightgray g-color-white--checked g-bg-primary--checked g-brd-left-none--md rounded-0">
                  {{ $t('paginated.filter.status.done') }}
                </span>
              </label>
            </div>
            <!-- End Status -->

            <!-- Price -->
            <h2 class="h5 text-uppercase g-color-gray-dark-v1">{{ $t('paginated.filter.headings.price') }}</h2>
            <hr class="g-brd-gray-light-v4 g-my-15">
            <div class="btn-group justified-content g-mb-40">
              <label class="u-check">
                <input class="g-hidden-xs-up g-pos-abs g-top-0 g-left-0" name="radGroupBtn2_1" type="radio" :value="1"
                       v-model="price" @change="update">
                <span
                  class="btn btn-block u-btn-outline-lightgray g-color-white--checked g-bg-primary--checked rounded-0">
                  {{ $t('paginated.filter.price.free') }}
                </span>
              </label>
              <label class="u-check">
                <input class="g-hidden-xs-up g-pos-abs g-top-0 g-left-0" name="radGroupBtn2_1" type="radio" :value="2"
                       v-model="price" @change="update">
                <span
                  class="btn btn-block u-btn-outline-lightgray g-color-white--checked g-bg-primary--checked g-brd-left-none--md rounded-0">
                  {{ $t('paginated.filter.price.charged') }}
                </span>
              </label>
            </div>
            <!-- End Price -->

            <button
              class="g-mt-150 btn btn-block u-btn-outline-lightgray g-color-white--checked g-bg-primary--checked rounded-0"
              type="button" @click="resetFilters">
              {{ $t('paginated.filter.reset') }}
            </button>
          </div>
          <div :class="filter ? 'col-lg-9' : 'col-lg-12'">
            <respondeco-pagination class="g-mb-50" :page="page" :pageSize="pageSize" @page="pageChanged"
                                   ref="topPagination" :scroll="false" :total="total"></respondeco-pagination>

            <transition name="fade" mode="out-in">
              <respondeco-portfolio :columns="filter ? 'col-sm-6 col-lg-4' : undefined" :items="items"
                                    v-if="!$isLoading('portfolio')"></respondeco-portfolio>
              <vue-simple-spinner class="g-mb-100 g-mt-100" v-else></vue-simple-spinner>
            </transition>
            <respondeco-pagination class="g-mt-50" :page="page" :pageSize="pageSize" @page="pageChanged"
                                   :scroll="$refs.topPagination"
                                   :total="total"></respondeco-pagination>
          </div>
        </div>
      </section>

      <vue-simple-spinner class="g-mb-100 g-mt-100" v-else></vue-simple-spinner>
    </transition>
  </section>
</template>

<script>
  import RespondecoPagination from 'app/main/pagination'
  import RespondecoPortfolio from 'app/main/portfolio'

  import VueSimpleSpinner from 'vue-simple-spinner'
  import { Categories, debounce } from '../../common/utils'

  export default {
    name: 'paginated',

    beforeRouteUpdate (to, from, next) {
      if (this.runBeforeRouteUpdate) {
        this.page = this.pageFromRoute(to)

        if (!this.$isLoading('portfolio')) {
          this.update()
        }
      }

      next()
    },

    created () {
      this.runBeforeRouteUpdate = this.$route.query.page !== undefined

      this.$startLoading('initial')

      this.update().then(() => {
        this.$endLoading('initial')
      })
    },

    components: {
      RespondecoPagination,
      RespondecoPortfolio,
      VueSimpleSpinner
    },

    data () {
      return {
        categories: Categories.map(category => ({
          checked: false,
          label: category
        })),
        filter: true,
        heading: '',
        items: [],
        page: this.pageFromRoute(this.$route),
        pageSize: 12,
        paginatedItems: [],
        price: 0,
        query: '',
        status: 0,

        // The component will be reused when switching between routes.
        // If we didn't check for that the page would be loaded twice (in created and beforeRouteUpdate).
        runBeforeRouteUpdate: true,
        total: 1
      }
    },

    methods: {
      clearQuery () {
        this.query = ''
        this.update()

        this.$nextTick(() => {
          this.$refs.suggest.hideList()
        })
      },

      debounce: debounce(function () {
        this.update()
      }, 500),

      pageChanged (page, items) {
        this.page = page
        this.paginatedItems = items

        this.$router.push({
          query: {
            page: page
          }
        }, () => {
          this.runBeforeRouteUpdate = true
        })
      },

      pageFromRoute (route) {
        return parseInt(route.query.page) || 1
      },

      resetFilters () {
        this.categories.forEach(category => { category.checked = false })
        this.price = 0
        this.status = 0

        this.update()
      },

      update () {
        this.$startLoading('portfolio')

        return this.fetchData(
          this.page,
          this.pageSize,
          this.query,
          this.categories
            .filter(category => category.checked)
            .map(category => category.label),
          this.price,
          this.status
        ).then(() => {
          this.$endLoading('portfolio')
        })
      },

      updateChecked (category) {
        category.checked = !category.checked

        this.update()
      },

      updateList (items, total, filter) {
        this.total = total

        if (filter) {
          this.items = items.filter(organisation => organisation.name.indexOf(filter) > -1).map(this.toPortfolioItem)
        } else {
          this.items = items.map(this.toPortfolioItem)
        }
      }
    }
  }
</script>

<style>
  .clear-query {
    position: absolute;
    top: 50%;
    transform: translateY(-50%);
    right: 70px;
    font-size: 1.2rem;
    cursor: pointer;
    padding: 15px;
  }
</style>
