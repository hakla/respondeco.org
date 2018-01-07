import LoaderHelper from '../../common/mixins/loader-helper'

const ItemPage = {
  created () {
    this.fetchData()
  },

  data () {
    return {
      // overridden
      item: {},

      // overridden
      service: {}
    }
  },

  methods: {
    fetchData () {
      this.promiseLoading(
        this.service.byId(this.id).then(result => {
          this.item = result.body
        }, 'item-page')
      )
    }
  },

  mixins: [LoaderHelper],

  props: ['id']
}

export default ItemPage
