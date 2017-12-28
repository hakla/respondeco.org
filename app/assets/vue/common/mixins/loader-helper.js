let normalise = (loader = ['global-loader']) => Array.isArray(loader) ? loader : [loader]

const LoaderHelper = {
  methods: {
    endLoading (loader = this.loader) {
      normalise(loader).forEach(loader => this.$endLoading(loader))
    },

    isLoading (loader = this.loader) {
      return normalise(loader).filter(loader => this.$isLoading(loader)).length > 0
    },

    isntLoading (loader = this.loader) {
      return !this.isLoading(loader)
    },

    promiseLoading (promise, loader = this.loader) {
      this.startLoading(loader)

      promise.then(() => {
        this.endLoading(loader)
      })
    },

    startLoading (loader = this.loader) {
      normalise(loader).forEach(loader => this.$startLoading(loader))
    }
  },
  //
  // props: {
  //   loader: {
  //     type: Array | String,
  //     default: 'global-loader'
  //   },
  // }
}

export default LoaderHelper
