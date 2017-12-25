<script>
  import Paginated from 'app/mixins/paginated'
  import Projects from 'common/services/projects'
  import Utils from 'app/utils'

  let toPortfolioItem = Utils.toPortfolioItem("projects")

  export default {
    name: 'projects',

    data () {
      return {
        heading: 'common.project'
      }
    },

    methods: {
      fetchData () {
        return Projects.all({
          page: this.page,
          pageSize: this.pageSize
        }).then(response => {
          let projects = response.body.items

          this.updateList(projects, response.body.total)
        })
      },

      toPortfolioItem
    },

    mixins: [Paginated],
  }
</script>

<style lang="css">
</style>
