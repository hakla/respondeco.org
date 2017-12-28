<template>
  <section class="g-pt-40--md g-pb-80--md g-pb-150--lg g-pt-20 g-pb-60">
    <unify-hero class="g-mb-40">{{ $tc(this.heading, 2) }}</unify-hero>

    <transition name="fade" mode="out-in">
      <section class="container" v-if="!$isLoading('initial')">
        <respondeco-pagination class="g-mb-100--md g-mb-50" :page="page" :pageSize="12" @page="pageChanged" ref="topPagination" :scroll="false" :total="total"></respondeco-pagination>

        <transition name="fade" mode="out-in">
          <respondeco-portfolio :items="items" v-if="!$isLoading('portfolio')"></respondeco-portfolio>
          <vue-simple-spinner class="g-mb-100 g-mt-100" v-else></vue-simple-spinner>
        </transition>
        <respondeco-pagination :page="page" :pageSize="pageSize" @page="pageChanged" :scroll="$refs.topPagination" :total="total"></respondeco-pagination>
      </section>

      <vue-simple-spinner class="g-mb-100 g-mt-100" v-else></vue-simple-spinner>
    </transition>
  </section>
</template>

<script>
  import RespondecoPagination from 'app/main/pagination'
  import RespondecoPortfolio from 'app/main/portfolio'

  import VueSimpleSpinner from 'vue-simple-spinner'

  export default {
    name: "paginated",

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
        items: [],
        page: this.pageFromRoute(this.$route),
        pageSize: 12,
        paginatedItems: [],

        // The component will be reused when switching between routes.
        // If we didn't check for that the page would be loaded twice (in created and beforeRouteUpdate).
        runBeforeRouteUpdate: true,
        total: 1
      }
    },

    methods: {
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

      update () {
        this.$startLoading('portfolio')

        return this.fetchData(
          this.page,
          this.pageSize
        ).then(() => {
          this.$endLoading('portfolio')
        })
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

<style scoped>

</style>
