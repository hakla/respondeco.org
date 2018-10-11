<script>
  import OrganisationService from 'app/organisations/organisations-service'
  import Paginated from 'app/mixins/paginated'
  import Utils from 'app/utils'

  import { ObjectNormaliser } from 'common/utils'

  let toPortfolioItem = Utils.toPortfolioItem("organisations")

  export default {
    name: 'organisations',

    data () {
      return {
        filter: false,
        heading: 'common.organisation'
      }
    },

    methods: {
      fetchData (page, pageSize, query, categories) {
        return OrganisationService.all({
          page,
          pageSize,
          query,
          categories: categories.join(',')
        }).then(response => {
          let organisations = response.body.items.map(organisation => ObjectNormaliser.organisation(organisation))

          this.updateList(organisations, response.body.total)
        })
      },

      toPortfolioItem
    },

    mixins: [Paginated],
  }

</script>

<style lang="css">
</style>
