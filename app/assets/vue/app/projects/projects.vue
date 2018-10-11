<script>
  import Paginated from 'app/mixins/paginated'
  import Projects from 'common/services/projects'
  import { ImageHelper } from '../../common/utils'
  import Utils from '../utils'

  export default {
    name: 'projects',

    data () {
      return {
        heading: 'common.project',
        pageSize: 9
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

      toPortfolioItem: Utils.projectToPortfolioItem
    },

    mixins: [Paginated],
  }
</script>

<style lang="css">
</style>
