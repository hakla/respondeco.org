<script>
  import Paginated from 'app/mixins/paginated'
  import ProjectService from 'app/projects/projects-service'
  import Utils from 'app/utils'

  let toPortfolioItem = Utils.toPortfolioItem("projects")

  export default {
    name: 'projects',

    created () {
      ProjectService.init(this)
      this.fetchData()
    },

    data () {
      return {
        heading: 'common.project'
      }
    },

    methods: {
      fetchData () {
        this.$startLoading('portfolio')

        ProjectService.all().then(response => {
          let projects = response.body

          this.updateList(projects)

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
