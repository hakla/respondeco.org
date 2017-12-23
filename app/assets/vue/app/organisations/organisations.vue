<template>
  <section class="g-pt-100--md g-pt-40">
    <respondeco-breadcrumbs :heading="$tc('common.organisation', 2)"></respondeco-breadcrumbs>
    <respondeco-portfolio :items="list" v-if="!loading"></respondeco-portfolio>
    <vue-simple-spinner class="g-mb-100 g-mt-100" v-else></vue-simple-spinner>
  </section>
</template>

<script>
  import RespondecoBreadcrumbs from 'app/main/breadcrumbs'
  import RespondecoPortfolio from 'app/main/portfolio'
  import OrganisationService from 'app/organisations/organisations-service'
  import Utils from 'app/utils'
  import VueSimpleSpinner from 'vue-simple-spinner'

  import { mapGetters, mapActions } from 'vuex'

  let toPortfolioItem = Utils.toPortfolioItem("organisations")

  export default {
    name: 'organisations',

    components: {
      RespondecoPortfolio,
      RespondecoBreadcrumbs,
      VueSimpleSpinner
    },

    computed: {
      ...mapGetters(['organisations'])
    },

    created () {
      OrganisationService.init(this);
      this.fetchData();
    },

    data() {
      return {
        filter: '',
        list: [],
        loading: false
      }
    },

    methods: {
      ...mapActions(['all']),

      fetchData () {
        this.loading = true

        OrganisationService.all().then(response => {
          this.all(response.body)
          this.updateList()

          this.loading = false
        });
      },

      updateList (filter) {
        if (filter) {
          this.list = this.organisations.filter(organisation => organisation.name.indexOf(filter) > -1).map(toPortfolioItem);
        } else {
          this.list = this.organisations.map(toPortfolioItem);
        }
      }
    }
  }

</script>

<style lang="css">
</style>
