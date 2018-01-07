<template>
  <div class="container-fluid">
    <div class="row">
      <div class="col">
        <router-link :to="{ name: 'organisations' }">
          <div class="card text-white bg-primary">
            <div class="card-body pb-0">
              <h4 class="mb-0">{{ amount.organisations }}</h4>
              <p>Organisationen registriert</p>
            </div>
            <div class="chart-wrapper px-3" style="height:70px;">
              <respondeco-line-chart
                :data="charts.data.organisations"
                :height="70"
                :options="charts.options.line"
              ></respondeco-line-chart>
            </div>
          </div>
        </router-link>
      </div>

      <div class="col">
        <router-link :to="{ name: 'projects' }">
          <div class="card text-white bg-danger">
            <div class="card-body pb-0">
              <h4 class="mb-0">{{ amount.projects }}</h4>
              <p>Projekte erfasst</p>
            </div>
            <div class="chart-wrapper px-3" style="height:70px;">
              <respondeco-bar-chart
                :data="charts.data.projects"
                :height="70"
                :options="charts.options.bar"
              ></respondeco-bar-chart>
            </div>
          </div>
        </router-link>
      </div>

      <div class="col">
        <router-link :to="{ name: 'accounts' }">
          <div class="card text-white bg-info">
            <div class="card-body pb-0">
              <h4 class="mb-0">{{ amount.accounts }}</h4>
              <p>Accounts</p>
            </div>
            <div class="chart-wrapper px-3" style="height:70px;">
              <respondeco-line-chart
                :data="charts.data.accounts"
                :height="70"
                :options="charts.options.line"
              ></respondeco-line-chart>
            </div>
          </div>
        </router-link>
      </div>

      <div class="col">
        <router-link :to="{ name: 'finishedProjects' }">
          <div class="card text-white bg-success">
            <div class="card-body pb-0">
              <h4 class="mb-0">{{ amount.finishedProjects }}</h4>
              <p>Projekte wurden durchgeführt</p>
            </div>
            <div class="chart-wrapper px-3" style="height:70px;">
              <respondeco-line-chart
                :data="charts.data.finishedProjects"
                :height="70"
                :options="charts.options.line"
              ></respondeco-line-chart>
            </div>
          </div>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script>
  import RespondecoBarChart from 'common/components/respondeco-bar-chart'
  import RespondecoLineChart from 'common/components/respondeco-line-chart'
  import Organisations from '../../common/services/organisations'
  import Projects from '../../common/services/projects'
  import Accounts from '../../common/services/accounts'
  import FinishedProjects from '../../common/services/finished-projects'

  export default {
    name: 'dashboard',

    components: {
      RespondecoBarChart,
      RespondecoLineChart
    },

    created () {
      Accounts.all().then(_ => this.amount.accounts = _.body.total)
      FinishedProjects.all().then(_ => this.amount.finishedProjects = _.body.total)
      Organisations.all().then(_ => this.amount.organisations = _.body.total)
      Projects.all().then(_ => this.amount.projects = _.body.total)
    },

    data () {
      const rand = (min, max) => {
        if (max === undefined) {
          max = min
          min = 0
        }

        const v = Math.floor(Math.random() * (max - min + 1) + min)
        console.log(v)

        return v
      }

      const labels = (length = 10) => (Array.apply(null, { length }))
      const data = (length = 10, min = 5, max = 25) => ({
        labels: labels(length),
        datasets: [
          {
            backgroundColor: $.brandInfo,
            borderColor: 'rgba(255,255,255,.55)',
            data: (Array.apply(null, { length })).map(_ => rand(min, max))
          }
        ]
      })

      return {
        amount: {
          accounts: 32,
          finishedProjects: 32,
          organisations: 32,
          projects: 32
        },
        charts: {
          data: {
            accounts: data(),
            finishedProjects: data(),
            organisations: data(),
            projects: data(15, 15, 30)
          },

          options: {
            bar: {
              maintainAspectRatio: false,
              legend: {
                display: false
              },
              scales: {
                xAxes: [{
                  display: false,
                  barPercentage: 1,
                }],
                yAxes: [{
                  display: false,
                }]
              },
              tooltips: {
                enabled: false
              },
              title: {
                display: false
              }
            },
            line: {
              elements: {
                line: {
                  borderWidth: 2
                },
                point: {
                  radius: 0,
                  hitRadius: 10,
                  hoverRadius: 4
                }
              },
              legend: {
                display: false
              },
              maintainAspectRatio: false,
              scales: {
                xAxes: [{
                  gridLines: {
                    color: 'transparent',
                    zeroLineColor: 'transparent'
                  },
                  ticks: {
                    fontSize: 2,
                    fontColor: 'transparent'
                  }
                }],
                yAxes: [{
                  display: false,
                  ticks: {
                    display: false,
                    min: 0,
                    max: 30
                  }
                }]
              },
              title: {
                display: false
              },
              tooltips: {
                enabled: false
              }
            }
          }
        }
      }
    }
  }
</script>

<style scoped>
  a:hover {
    text-decoration: none;
  }
</style>
