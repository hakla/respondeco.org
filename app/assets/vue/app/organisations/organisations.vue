<template>
  <section class="g-pt-40--md g-pb-80--md g-pb-150--lg g-pt-20">
    <unify-hero class="g-mb-40">{{ $tc('common.organisation', 2) }}</unify-hero>

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

  import { ObjectNormaliser } from 'common/utils'

  import { mapActions, mapGetters } from 'vuex'

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
      this.fetchData()
    },

    data () {
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
          let organisations = response.body.map(organisation => ObjectNormaliser.organisation(organisation))

          this.all(response.body)
          this.updateList()

          this.loading = false
        })
      },

      updateList (filter) {
        if (filter) {
          this.list = this.organisations.filter(organisation => organisation.name.indexOf(filter) > -1).map(toPortfolioItem)
        } else {
          this.list = this.organisations.map(toPortfolioItem)
        }
      }
    }
  }

</script>

<style lang="css">
</style>
