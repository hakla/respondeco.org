<template>
  <div class="">
    <div class="page-title">
      <div class="title_left">
        <h3>Organisation bearbeiten</h3>
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
            <h2>{{ organisation.name }}</h2>
            <div class="clearfix"></div>
          </div>
          <div class="x_content">
            <br>
            <form id="demo-form2" data-parsley-validate="" class="form-horizontal form-label-left" novalidate
                  @submit.stop.prevent="save()">

              <h2>Banner</h2>
              <div class="banner" :style="backgroundImage(organisation.image)"></div>

              <dropzone id="myVueDropzone"
                        ref="dropzone"
                        :url="Config.ImageBaseUrl"
                        @vdropzone-success="showSuccess"
                        @vdropzone-sending="transformRequest"
                        :use-custom-dropzone-options="true"
                        :dropzone-options="Config.DropzoneOptions">
              </dropzone>
              <hr>

              <h2>Über die Organisation</h2>

              <!-- Name -->
              <div class="form-group">
                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="name">Name der Organisation <span
                  class="required">*</span>
                </label>
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <input type="text" id="name" required="required" class="form-control col-md-7 col-xs-12"
                         v-model="organisation.name">
                </div>
              </div>

              <!-- Beschreibung -->
              <div class="form-group">
                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="description">Beschreibung <span
                  class="required">*</span>
                </label>
                <div class="col-md-6 col-sm-6 col-xs-12">
                <textarea id="description" required="required" rows="10" class="form-control col-md-7 col-xs-12"
                          v-model="organisation.description">
                </textarea>
                </div>
              </div>

              <!-- Kategorie -->
              <div class="form-group">
                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="category">Kategorie <span
                  class="required">*</span>
                </label>
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <select class="form-control" id="category" v-model="organisation.category">
                    <option>---</option>
                    <option v-for="category in categories">{{ category }}</option>
                  </select>
                </div>
              </div>

              <!-- E-Mail -->
              <div class="form-group">
                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="email">E-Mail <span
                  class="required">*</span>
                </label>
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <input type="text" id="email" required="required" class="form-control col-md-7 col-xs-12"
                         v-model="organisation.email">
                </div>
              </div>

              <!-- Website -->
              <div class="form-group">
                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="website">Website <span
                  class="required">*</span>
                </label>
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <input type="text" id="website" required="required" class="form-control col-md-7 col-xs-12"
                         v-model="organisation.website">
                </div>
              </div>

              <!-- Standort -->
              <div class="form-group">
                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="location">Standort <span class="required">*</span>
                </label>
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <input type="text" id="location" required="required" class="form-control col-md-7 col-xs-12"
                         v-model="organisation.location">
                </div>
              </div>

              <!-- Youtube-Banner -->
              <div class="form-group">
                <label class="control-label col-md-3 col-sm-3 col-xs-12" for="banner_video">Banner Video <span
                  class="required">*</span>
                </label>
                <div class="col-md-6 col-sm-6 col-xs-12">
                  <input type="text" id="banner_video" class="form-control col-md-7 col-xs-12"
                         v-model="organisation.video">
                </div>
              </div>

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
  import Dropzone from 'vue2-dropzone'
  import Config from 'common/config';
  import Utils from 'common/utils'
  import TokenHolder from 'common/token-holder';
  import {
    router
  } from '../router'

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
    name: "organisation",

    components: {
      Dropzone
    },

    created() {
      this.resource = this.$resource('organisations{/id}')

      if (this.$route.params.id !== 'new') {
        this.fetchData()
      } else {
        this.isNew = true
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
        organisation: {
          name: "Neue Organisation"
        },
        subcategories: []
      }
    },

    methods: {
      cancel() {
        router.push('/organisations')
      },

      fetchData() {
        this.loading = true

        this.resource.get({
          id: this.$route.params.id
        }).then(response => {
          this.organisation = response.body
          this.updateSubCategories()
          this.loading = false
        })
      },

      backgroundImage: Utils.backgroundImage,

      save() {
        if (this.isNew) {
          this.resource.save(null, this.organisation).then(this.cancel);
        } else {
          this.resource.update({
            id: this.$route.params.id
          }, this.organisation).then(this.cancel);
        }
      },

      showSuccess(_, response) {
        this.organisation.image = response

        this.$refs.dropzone.removeAllFiles()
      },

      transformRequest(_, xhr) {
        TokenHolder.get(token => xhr.setRequestHeader('X-Access-Token', token))
      },

      updateSubCategories() {
        this.subcategories = subcategories[this.organisation.category]
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
