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
        heading: 'common.organisation'
      }
    },

    methods: {
      fetchData (page, pageSize) {
        return OrganisationService.all({
          page: this.page,
          pageSize: this.pageSize
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
