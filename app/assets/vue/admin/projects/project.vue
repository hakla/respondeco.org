<template>
<div class="">
  <div class="page-title">
    <div class="title_left">
      <h3>Projekt bearbeiten</h3>
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
          <h2>{{ project.name }}</h2>
          <div class="clearfix"></div>
        </div>
        <div class="x_content">
          <br>
          <form id="demo-form2" data-parsley-validate="" class="form-horizontal form-label-left" novalidate @submit.stop.prevent="save()">

            <h2>Banner</h2>
            <div class="banner" :style="backgroundImage(project.image)"></div>

            <dropzone id="myVueDropzone"
                      ref="dropzone"
                      :url="Config.ImageBaseUrl"
                      @vdropzone-success="showSuccess"
                      @vdropzone-sending="transformRequest"
                      :use-custom-dropzone-options="true"
                      :dropzone-options="Config.DropzoneOptions">
            </dropzone>
            <hr>

            <!-- Name -->
            <div class="form-group">
              <label class="control-label col-md-3 col-sm-3 col-xs-12" for="name">Name des Projekts <span class="required">*</span>
              </label>
              <div class="col-md-6 col-sm-6 col-xs-12">
                <input type="text" id="name" required="required" class="form-control col-md-7 col-xs-12" v-model="project.name">
              </div>
            </div>

            <!-- Organisation -->
            <div class="form-group">
              <label class="control-label col-md-3 col-sm-3 col-xs-12" for="organisation">Organisation <span class="required">*</span>
              </label>
              <div class="col-md-6 col-sm-6 col-xs-12">
                <select class="form-control" id="organisation" v-model="project.organisation">
                  <option v-bind:value="organisation.id" v-for="organisation in organisations">{{ organisation.name }}</option>
                </select>
              </div>
            </div>

            <!-- Beschreibung -->
            <div class="form-group">
              <label class="control-label col-md-3 col-sm-3 col-xs-12" for="description">Beschreibung <span class="required">*</span>
              </label>
              <div class="col-md-6 col-sm-6 col-xs-12">
                <textarea id="description" required="required" rows="10" class="form-control col-md-7 col-xs-12" v-model="project.description">
                </textarea>
              </div>
            </div>

            <!-- Kategorie -->
            <div class="form-group">
              <label class="control-label col-md-3 col-sm-3 col-xs-12" for="category">Kategorie <span class="required">*</span>
              </label>
              <div class="col-md-6 col-sm-6 col-xs-12">
                <select class="form-control" id="category" v-model="project.category" @change="updateSubCategories()">
                  <option>---</option>
                  <option v-for="category in categories">{{ category }}</option>
                </select>
              </div>
            </div>

            <!-- Unterkategorie -->
            <div class="form-group">
              <label class="control-label col-md-3 col-sm-3 col-xs-12" for="subcategory">Unterkategorie <span class="required">*</span>
              </label>
              <div class="col-md-6 col-sm-6 col-xs-12">
                <select class="form-control" id="subcategory" v-model="project.subcategory">
                  <option>---</option>
                  <option v-for="subcategory in subcategories">{{ subcategory }}</option>
                </select>
              </div>
            </div>

            <!-- Standort -->
            <div class="form-group">
              <label class="control-label col-md-3 col-sm-3 col-xs-12" for="location">Standort <span class="required">*</span>
              </label>
              <div class="col-md-6 col-sm-6 col-xs-12">
                <input type="text" id="location" required="required" class="form-control col-md-7 col-xs-12" v-model="project.location">
              </div>
            </div>

            <!-- Vorteile -->
            <div class="form-group">
              <label class="control-label col-md-3 col-sm-3 col-xs-12" for="benefits">Vorteile <span class="required">*</span>
              </label>
              <div class="col-md-6 col-sm-6 col-xs-12">
                <input type="text" id="benefits" required="required" class="form-control col-md-7 col-xs-12" v-model="project.benefits">
              </div>
            </div>

            <!-- Preis -->
            <div class="form-group">
              <label class="control-label col-md-3 col-sm-3 col-xs-12" for="price">Preis <span class="required">*</span>
              </label>
              <div class="col-md-6 col-sm-6 col-xs-12">
                <input type="text" id="price" required="required" class="form-control col-md-7 col-xs-12" v-model="project.price">
              </div>
            </div>

            <!-- Beginn -->
            <div class="form-group">
              <label class="control-label col-md-3 col-sm-3 col-xs-12" for="beginning">Beginn <span class="required">*</span>
              </label>
              <div class="col-md-6 col-sm-6 col-xs-12">
                <div class="control-group">
                  <div class="controls">
                    <div class="col-md-12 xdisplay_inputx form-group has-feedback">
                      <input type="text" data-type="daterange" class="form-control has-feedback-left" id="beginning" placeholder="Beginn" aria-describedby="inputSuccess2Status2" v-model="project.start">
                      <span class="fa fa-calendar-o form-control-feedback left" aria-hidden="true"></span>
                      <span id="inputSuccess2Status1" class="sr-only">(success)</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Ende -->
            <div class="form-group">
              <label class="control-label col-md-3 col-sm-3 col-xs-12" for="end">Ende <span class="required">*</span>
              </label>
              <div class="col-md-6 col-sm-6 col-xs-12">
                <div class="control-group">
                  <div class="controls">
                    <div class="col-md-12 xdisplay_inputx form-group has-feedback">
                      <input type="text" data-type="daterange" class="form-control has-feedback-left" id="end" placeholder="Ende" aria-describedby="inputSuccess2Status2" v-model="project.end">
                      <span class="fa fa-calendar-o form-control-feedback left" aria-hidden="true"></span>
                      <span id="inputSuccess2Status2" class="sr-only">(success)</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <!-- Youtube-Banner -->
            <div class="form-group">
              <label class="control-label col-md-3 col-sm-3 col-xs-12" for="banner_video">Banner Video <span
                class="required">*</span>
              </label>
              <div class="col-md-6 col-sm-6 col-xs-12">
                <input type="text" id="banner_video" class="form-control col-md-7 col-xs-12"
                       v-model="project.video">
              </div>
            </div>

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

// import jQuery from 'jquery';

import OrganisationService from 'admin/organisations/organisations-service'
import ProjectService from './projects-service'

import Dropzone from 'vue2-dropzone'
import Config from 'common/config';
import Utils from 'common/utils'
import TokenHolder from 'common/token-holder';

// import dateRangePickerOptions from 'common/daterangepicker-options';

let subcategories = {
  "Ökonomie": [
    'Produkt',
    'Beschäftigungsverhältnisse',
    'Arbeitsbedingungen und Sozialschutz',
    'Gesundheit und Sicherheit am Arbeitsplatz',
    'Menschliche Entwicklung am Arbeitsplatz',
    'Schulungen und Weiterbildungen ',
    'Korruptionsbekämpfung',
    'Kundendienst, Beschwerdemanagement',
    'Schutz und Vertraulichkeit von Kundendaten'
  ],
  "Umwelt": [
    'Inanspruchnahme natürlicher Ressourcen',
    'Ressourcenmanagement',
    'Klimarelevante Emissionen'
  ],
  "Gesellschaft": [
    'Demokratie/Menschenrechte',
    'Einbindung der Gemeinschaft',
    'Bildung/ Kultur',
    'Schaffen von Arbeitsplätzen und berufliche Qualifizierung ',
    'Chancengerechtigkeit',
    'Anti-Diskriminierung/ schutzbedürftige Gruppen',
    'Stadtteil',
    'Unterstützung von Social Businesses'
  ]
}

export default {
  name: "project",

  components: {
    Dropzone
  },

  created() {
    ProjectService.init(this)
    OrganisationService.init(this)

    OrganisationService.all().then(response => this.organisations = response.body)

    if (this.$route.params.id !== 'new') {
      this.fetchData();
    } else {
      this.isNew = true;
    }
  },

  data() {
    return {
      categories: [
        'Ökonomie',
        'Umwelt',
        'Gesellschaft'
      ],
      Config,
      isNew: false,
      loading: false,
      project: {
        name: "Neues Projekt"
      },
      organisations: [],
      subcategories: []
    }
  },
  methods: {
    backgroundImage: Utils.backgroundImage,

    cancel() {
      router.push('/projects')
    },

    fetchData() {
      this.loading = true

      ProjectService.get(this.$route.params.id).then(response => {
        this.project = response.body

        this.project.start = Utils.formatDate(this.project.start)
        this.project.end = Utils.formatDate(this.project.end)

        this.updateSubCategories()
        this.loading = false
      })
    },

    save() {
      let o = Object.assign({}, this.project, {
        end: Utils.convertDate(this.project.end),
        price: parseInt(this.project.price),
        start: Utils.convertDate(this.project.start)
      })

      if (this.isNew) {
        ProjectService.save(o).then(this.cancel)
      } else {
        ProjectService.update(this.$route.params.id, o).then(this.cancel)
      }
    },

    setModel(model, value) {
      this[model] = value
    },

    showSuccess(_, response) {
      this.project.image = response

      this.$refs.dropzone.removeAllFiles()
    },

    transformRequest(_, xhr) {
      TokenHolder.get(token => xhr.setRequestHeader('X-Access-Token', token))
    },

    updateSubCategories() {
      this.subcategories = subcategories[this.project.category]
    }
  }
}
</script>

<style scoped>
  .banner {
    height: 300px;
    width: 100%;

    margin-bottom: 50px;

    background-repeat: no-repeat;
    background-size: cover;
    background-position-y: 50%;
  }
</style>
