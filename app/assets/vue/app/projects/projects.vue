<script>
  import Paginated from 'app/mixins/paginated'
  import Projects from 'common/services/projects'
  import Utils from '../utils'
  import Organisations from '../../common/services/organisations'

  export default {
    name: 'projects',

    created () {
      this.fetchOrganisations()
    },

    data () {
      return {
        heading: 'common.project',
        pageSize: 9,
        suggestions: []
      }
    },

    methods: {
      fetchData (page, pageSize, query, categories, price, status) {
        return Projects.all({
          page,
          pageSize,
          query,
          categories: categories.join(','),
          price,
          status
        }).then(response => {
          let projects = response.body.items

          this.updateList(projects, response.body.total)
        })
      },

      fetchOrganisations () {
        return Organisations.all().then(data => {
          this.suggestions = data.body.items

          return this.suggestions
        })
      },

      toPortfolioItem: Utils.projectToPortfolioItem
    },

    mixins: [Paginated]
  }
</script>

<style lang="css">
</style>
