<template>
  <section class="g-pt-40--md g-pb-80--md g-pb-150--lg g-pt-20">
    <unify-hero class="g-mb-40">{{ $tc(this.heading, 2) }}</unify-hero>

    <section class="container" v-if="!$isLoading('portfolio')">
      <respondeco-pagination class="g-mb-100--md g-mb-50" :items="list" :page="page" :pageSize="12" @page="pageChanged" ref="topPagination" :scroll="false"></respondeco-pagination>
      <respondeco-portfolio :items="paginatedItems"></respondeco-portfolio>
      <respondeco-pagination :items="list" :page="page" :pageSize="12" @page="pageChanged" :scroll="$refs.topPagination"></respondeco-pagination>
    </section>

    <vue-simple-spinner class="g-mb-100 g-mt-100" v-else></vue-simple-spinner>
  </section>
</template>

<script>
  import RespondecoPagination from 'app/main/pagination'
  import RespondecoPortfolio from 'app/main/portfolio'

  import VueSimpleSpinner from 'vue-simple-spinner'

  export default {
    name: "paginated",

    beforeRouteUpdate (to, from, next) {
      this.page = this.pageFromRoute(to)

      next()
    },

    components: {
      RespondecoPagination,
      RespondecoPortfolio,
      VueSimpleSpinner
    },

    data () {
      return {
        list: [],
        page: this.pageFromRoute(this.$route),
        paginatedItems: []
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
        })
      },

      pageFromRoute (route) {
        return parseInt(route.query.page) || 1
      },

      updateList (items, filter) {
        if (filter) {
          this.list = items.filter(organisation => organisation.name.indexOf(filter) > -1).map(this.toPortfolioItem)
        } else {
          this.list = items.map(this.toPortfolioItem)
        }
      }
    }
  }
</script>

<style scoped>

</style>
