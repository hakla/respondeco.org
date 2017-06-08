<template>

  <div class="">
    <div class="page-title">
      <div class="title_left">
        <h3>Abgeschlossene Projekte</h3>
      </div>

      <div class="title_right">
        <div class="col-md-5 col-sm-5 col-xs-12 form-group pull-right top_search">
          <div class="input-group">
            <input type="text" class="form-control" placeholder="Search for..." v-model="filter" @input="updateList(filter)">
            <span class="input-group-btn">
              <button class="btn btn-default" type="button">Go!</button>
          </span>
          </div>
        </div>
      </div>
    </div>

    <div class="clearfix"></div>

    <div class="row">
      <div class="col-md-12">
        <div class="x_panel">
          <div class="x_title">
            <h2>Abgeschlossene Projekte</h2>
            <ul class="nav navbar-right panel_toolbox">
              <li>
                <router-link class="collapse-link" :to="{ name: 'finishedProject', params: { id: 'new' } }">
                  <button class="btn btn-primary">Neues abgeschlossenes Projekt</button>
                </router-link>
              </li>
            </ul>
            <div class="clearfix"></div>
          </div>
          <div class="x_content">

            <p>Übersicht über abgeschlossene Projekte</p>

            <!-- start project list -->
            <table class="table table-striped projects">
              <thead>
              <tr>
                <th style="width: 1%">#</th>
                <th style="width: 20%">Projekt</th>
                <th>Organisation</th>
                <th>Durchgeführt am</th>
                <th style="width: 20%"></th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="finishedProject in list">
                <td @click="gotoEdit(finishedProject.id)">{{ finishedProject.id }}</td>
                <td @click="gotoEdit(finishedProject.id)">
                  <a>{{ finishedProject.project.name }}</a>
                </td>
                <td @click="gotoEdit(finishedProject.id)">
                  <a>{{ finishedProject.organisation.name }}</a>
                </td>
                <td @click="gotoEdit(finishedProject.id)">
                  {{ formattedDate(finishedProject.date) }}
                </td>
                <td class="text-right">
                  <a href="#" class="btn btn-danger btn-xs" @click.prevent="remove(finishedProject.id)"><i class="fa fa-trash-o"></i> Delete </a>
                </td>
              </tr>
              </tbody>
            </table>
            <!-- end project list -->
          </div>
        </div>
      </div>
    </div>
  </div>

</template>

<script>

  import { router } from 'admin/router'
  import FinishedProjectsService from 'admin/finishedProjects/finished-projects-service'
  import Utils from 'common/utils'

  export default {
    name: 'FinishedProjects',

    created() {
      FinishedProjectsService.init(this)

      this.fetchData();
    },

    data() {
      return {
        filter: '',
        list: [],
        finishedProjects: []
      }
    },

    methods: {
      fetchData() {
        FinishedProjectsService.all().then(response => {
          this.finishedProjects = response.body
          this.updateList()
        });
      },
      gotoEdit (id) {
        router.push(`/finishedProjects/${id}`)
      },
      remove(id) {
        FinishedProjectsService.remove(id).then(response => this.fetchData())
      },
      formattedDate(date) {
        return Utils.formatDate(date)
      },
      updateList (filter) {
        if (filter) {
          this.list = this.finishedProjects.filter(finishedProject =>
            finishedProject.project.name.indexOf(filter) > -1 ||
            finishedProject.organisation.name.indexOf(filter) > -1
          )
        } else {
          this.list = this.finishedProjects
        }
      }
    }
  }

</script>
