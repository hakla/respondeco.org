<template>
  <div class="">
    <div class="page-title">
      <div class="title_left">
        <h3>Abgeschlossenes Projekt bearbeiten</h3>
      </div>
    </div>
    <div class="clearfix"></div>
    <div class="row">
      <div v-if="loading">
        Loading...
      </div>
      <div class="col-md-12 col-sm-12 col-xs-12" v-if="!loading">
        <div class="x_panel">
          <div class="x_title">
            <h2 v-if="finishedProject.project != null">{{ finishedProject.project.name }}</h2>
            <div class="clearfix"></div>
          </div>
          <div class="x_content">
            <br>
            <form id="demo-form2" data-parsley-validate="" class="form-horizontal form-label-left" novalidate @submit.stop.prevent="save()">

              <!-- Projekt -->
              <div class="form-group">
                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="project">Projekt <span class="required">*</span>
                </label>
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <select class="form-control" id="project" v-model="finishedProject.project">
                    <option :value="project.id" v-for="project in projects">{{ project.name }}</option>
                  </select>
                </div>
              </div>

              <!-- Organisation -->
              <div class="form-group">
                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="organisation">Organisation <span class="required">*</span>
                </label>
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <select class="form-control" id="organisation" v-model="finishedProject.organisation">
                    <option :value="organisation.id" v-for="organisation in organisations">{{ organisation.name }}</option>
                  </select>
                </div>
              </div>

              <!-- Date -->
              <div class="form-group">
                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="date">Datum der Durchführung: <span class="required">*</span>
                </label>
                <div class="col-md-6 col-sm-6 col-xs-12 row">
                  <div class="control-group">
                    <div class="controls">
                      <div class="col-md-12 xdisplay_inputx form-group has-feedback">
                        <input type="text" class="form-control has-feedback-left" id="date" v-model="finishedProject.date">
                        <span class="fa fa-calendar-o form-control-feedback left" aria-hidden="true"></span>
                        <span id="inputSuccess2Status2" class="sr-only">(success)</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Rating Owner -->
              <rating name="Projektbesitzer" v-model="finishedProject.ratingOwner"></rating>

              <!-- Rating Organisation -->
              <rating name="Organisation" v-model="finishedProject.ratingOrganisation"></rating>

              <!-- Actions -->
              <div class="ln_solid"></div>
              <div class="form-group">
                <div class="col-md-6 col-sm-6 col-xs-12 col-md-offset-3">
                  <button type="button" class="btn btn-primary" @click="cancel()">Abbrechen</button>
                  <button type="submit" class="btn btn-success">Speichern</button>
                </div>
              </div>

            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import {
    router
  } from '../router'

  import OrganisationService from 'admin/organisations/organisations-service'
  import ProjectsService from 'admin/projects/projects-service'
  import FinishedProjectsService from 'admin/finishedProjects/finished-projects-service'

  import Rating from 'common/rating'

  import Utils from 'common/utils'

  export default {
    name: "FinishedProject",
    components: {
      Rating
    },
    created() {
      FinishedProjectsService.init(this)
      ProjectsService.init(this)
      OrganisationService.init(this)

      OrganisationService.all().then(response => this.organisations = response.body)
      ProjectsService.all().then(response => this.projects = response.body)

      if (this.$route.params.id !== 'new') {
        this.fetchData();
      } else {
        this.isNew = true;
      }
    },
    data() {
      return {
        isNew: false,
        loading: false,
        finishedProject: {},
        projects: [],
        organisations: []
      }
    },
    methods: {
      cancel() {
        router.push('/finishedProjects')
      },
      fetchData() {
        this.loading = true

        FinishedProjectsService.get(this.$route.params.id).then(response => {
          this.finishedProject = response.body

          this.finishedProject.date= Utils.formatDate(this.finishedProject.date)

          this.loading = false
        })
      },
      save() {
        let o = Object.assign({}, this.finishedProject, {
          date: Utils.convertDate(this.finishedProject.date)
        })

        if (this.isNew) {
          FinishedProjectsService.save(o).then(this.cancel)
        } else {
          FinishedProjectsService.update(this.$route.params.id, o).then(this.cancel)
        }
      },
      setModel(model, value) {
        this[model] = value
      }
    }
  }
</script>

<style>

</style>
