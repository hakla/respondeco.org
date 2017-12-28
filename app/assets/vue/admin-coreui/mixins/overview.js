import { Notifications } from 'common/utils'
import LoaderHelper from '../../common/mixins/loader-helper'

const Overview = {
  name: 'admin-overview',

  created () {
    this.fetchData()
  },

  data () {
    return {
      loader: ['card'],

      list: [],

      // Used to normalise an entry - overridden
      normaliser: (i) => i,

      // Route to open when clicking on a line - overridden
      route: '',

      // Service used to query objects - overridden
      service: {}
    }
  },

  methods: {
    fetchData () {
      this.promiseLoading(
        this.service.all().then(response => {
          this.list = response.body.items.map(this.normaliser)
        })
      )
    },

    open (id) {
      this.$router.push({
        name: this.route,
        params: {id}
      })
    },

    remove (id) {
      this.$startLoading(`deleting-${id}`)

      this.service.remove(id).then(
        Notifications.success(this, () => {
          this.$endLoading(`deleting-${id}`)

          this.list = this.list.filter(item => item.id !== id)
        }),
        Notifications.error(this)
      )
    }
  },

  mixins: [LoaderHelper]
}

export default Overview
