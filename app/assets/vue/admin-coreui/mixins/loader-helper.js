const LoaderHelper = {
  methods: {
    isLoading () {
      let loader = this.loader

      if (typeof this.loader === 'string') {
        loader = [this.loader]
      }

      return loader.filter(loader => this.$isLoading(loader)).length > 0
    }
  },

  props: {
    loader: {
      type: Array | String,
      default: 'global-loader'
    },
  }
}

export default LoaderHelper
