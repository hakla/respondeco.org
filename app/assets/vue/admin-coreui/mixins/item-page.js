import { DateConfig, Notifications } from '../../common/utils'

const ItemPage = {
  computed: {
    isNew () {
      return this.id === 'new'
    },

    method () {
      return this.isNew ? 'save' : 'update'
    }
  },

  created () {
    this.fetchData()
  },

  data () {
    return {
      activeId: this.id,
      dateConfig: DateConfig,

      // overridden
      item: {},

      // overridden
      routeBack: {},

      // overridden
      service: {}
    }
  },

  methods: {
    back () {
      this.$router.push(this.routeBack)
    },

    fetchData (id = this.activeId) {
      if (!this.isNew) {
        this.$startLoading('card')

        this.service.byId(id).then(response => {
          this.item = response.body

          this.$endLoading('card')
        })
      }
    },

    save () {
      this.$startLoading('global-loader')

      this.service[this.method](this.item).then(
        Notifications.success(this, (response) => {
          if (this.isNew) {
            this.updateRoute(response.body)
          }
        }),
        Notifications.error(this)
      )
    },

    updateRoute (item) {
      this.$router.push({
        name: this.$route.name,
        params: {
          id: item.id
        }
      }, () => {
        this.activeId = item.id
        this.item = item
      })
    }
  },

  props: ['id']
}

export default ItemPage
