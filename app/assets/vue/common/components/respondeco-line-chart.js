import { Line } from 'vue-chartjs'

export default {
  extends: Line,

  mounted () {
    this.renderChart(this.data, this.options)
  },

  props: ['data', 'options']
}
