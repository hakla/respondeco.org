import { Bar } from 'vue-chartjs'

export default {
  extends: Bar,

  mounted () {
    this.renderChart(this.data, this.options)
  },

  props: ['data', 'options']
}
