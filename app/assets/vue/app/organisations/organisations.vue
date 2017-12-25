<script>
  import OrganisationService from 'app/organisations/organisations-service'
  import Paginated from 'app/mixins/paginated'
  import Utils from 'app/utils'

  import { ObjectNormaliser } from 'common/utils'

  import { mapActions, mapGetters } from 'vuex'

  let toPortfolioItem = Utils.toPortfolioItem("organisations")

  export default {
    name: 'organisations',

    created () {
      this.fetchData()
    },

    data () {
      return {
        heading: 'common.organisation'
      }
    },

    methods: {
      ...mapActions(['all']),

      fetchData () {
        this.$startLoading('portfolio')

        OrganisationService.all().then(response => {
          let organisations = response.body.map(organisation => ObjectNormaliser.organisation(organisation))

          this.all(organisations)
          this.updateList(organisations)

          this.$endLoading('portfolio')
        })
      },

      toPortfolioItem
    },

    mixins: [Paginated],
  }

</script>

<style lang="css">
</style>
