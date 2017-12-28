<script>
  import Paginated from 'app/mixins/paginated'
  import Projects from 'common/services/projects'
  import { ImageHelper } from '../../common/utils'

  function toPortfolioItem (item) {
    return {
      description: item.description,
      href: `/projects/${item.id}`,
      id: item.id,
      image: item.image ?('/api/v1/images/' + item.image) : '/assets/images/demo-square.jpg',
      title: item.name
    }
  }

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
