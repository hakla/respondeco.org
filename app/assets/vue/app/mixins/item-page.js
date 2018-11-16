import LoaderHelper from '../../common/mixins/loader-helper'
import Vue from 'vue'

const ItemPage = {
  data () {
    return {
      // overridden
      item: {},

      original: {},

      // overridden
      service: {},

      unsaved: false
    }
  },

  methods: {
    cancel () {
      this.item = Vue.util.extend({}, this.original)

      this.unsaved = false
    },

    fetchData () {
      this.promiseLoading(
        this.service.byId(this.id).then(result => {
          this.original = result.body
          this.item = Vue.util.extend({}, this.original)

          this.unsaved = false
        }, 'item-page')
      )
    }
  },

  mixins: [LoaderHelper],

  props: ['id']
}

export default ItemPage
