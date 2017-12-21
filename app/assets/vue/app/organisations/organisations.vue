<template>
  <section class="g-pt-100">
    <respondeco-breadcrumbs :heading="$tc('common.organisation', 2)"></respondeco-breadcrumbs>
    <respondeco-portfolio :items="list"></respondeco-portfolio>
  </section>
</template>

<script>
  import RespondecoBreadcrumbs from 'app/main/breadcrumbs'
  import RespondecoPortfolio from 'app/main/portfolio'
  import OrganisationService from 'app/organisations/organisations-service'
  import Utils from 'app/utils'

  import { mapGetters, mapActions } from 'vuex'

  let toPortfolioItem = Utils.toPortfolioItem("organisations")

  export default {
    name: 'organisations',

    components: {
      'respondecoPortfolio': RespondecoPortfolio,
      'respondecoBreadcrumbs': RespondecoBreadcrumbs
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
        list: []
      }
    },

    methods: {
      ...mapActions(['all']),

      fetchData () {
        OrganisationService.all().then(response => {
          this.all(response.body)

          this.updateList();
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
