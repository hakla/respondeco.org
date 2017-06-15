<template>
  <div>
    <respondeco-breadcrumbs :heading="$tc('common.organisation', 2)"></respondeco-breadcrumbs>
    <respondeco-portfolio :items="list"></respondeco-portfolio>
  </div>
</template>

<script>
  import RespondecoBreadcrumbs from 'app/main/breadcrumbs'
  import RespondecoPortfolio from 'app/main/portfolio'
  import OrganisationService from 'app/organisations/organisations-service'
  import Utils from 'app/utils'

  export default {
    name: 'organisations',

    components: {
      'respondecoPortfolio': RespondecoPortfolio,
      'respondecoBreadcrumbs': RespondecoBreadcrumbs
    },

    created () {
      OrganisationService.init(this);
      this.fetchData();
    },

    data() {
      return {
        filter: '',
        list: [],
        organisations: []
      }
    },

    methods: {
      fetchData () {
        OrganisationService.all().then(response => {
          this.organisations = response.body;
          this.updateList();
        });
      },
      updateList (filter) {
        if (filter) {
          this.list = this.organisations.filter(organisation => organisation.name.indexOf(filter) > -1).map(Utils.toPortfolioItem);
        } else {
          this.list = this.organisations.map(Utils.toPortfolioItem);
        }
      }
    }
  }

</script>

<style lang="css">
</style>
