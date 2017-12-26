<template>
  <admin-page>
    <admin-card>
      <span slot="header">Projekt {{ item.name }} bearbeiten</span>

      <form @submit.prevent="save">
        <div class="row">
          <div class="col-md-9">
            <!-- Name -->
            <div class="form-group">
              <label for="name">Name des Projekts</label>
              <input type="text" id="name" required="required" class="form-control" v-model="item.name">
            </div>

            <!-- Organisation -->
            <div class="form-group">
              <label for="organisation">Organisation</label>

              <select class="form-control" id="organisation" v-model="item.organisation">
                <option :value="organisation.id" v-for="organisation in organisations">{{ organisation.name }}</option>
              </select>
            </div>

            <!-- Beschreibung -->
            <div class="form-group">
              <label for="description">Beschreibung</label>

              <textarea id="description" required="required" rows="10" class="form-control" v-model="item.description">
              </textarea>
            </div>

            <!-- Kategorie -->
            <div class="form-group">
              <label for="category">Kategorie</label>

              <select class="form-control" id="category" v-model="item.category">
                <option>---</option>
                <option v-for="category in categories">{{ category }}</option>
              </select>
            </div>

            <!-- Unterkategorie -->
            <div class="form-group">
              <label for="subcategory">Unterkategorie</label>

              <select class="form-control" id="subcategory" v-model="item.subcategory">
                <option>---</option>
                <option v-for="subcategory in subcategories">{{ subcategory }}</option>
              </select>
            </div>

            <!-- Preis -->
            <div class="form-group">
              <label for="price">Preis</label>

              <input type="number" id="price" required="required" class="form-control" v-model.number="item.price">
            </div>

            <!-- Standort -->
            <div class="form-group">
              <label for="location">Standort</label>

              <input type="text" id="location" required="required" class="form-control" v-model="item.location">
            </div>

            <!-- Vorteile -->
            <div class="form-group">
              <label for="benefits">Vorteile</label>

              <input type="text" id="benefits" required="required" class="form-control" v-model="item.benefits">
            </div>

            <!-- Youtube-Banner -->
            <div class="form-group">
              <label for="banner_video">Banner Video</label>

              <input type="text" id="banner_video" class="form-control" v-model="item.video">
            </div>

            <!-- Begin -->
            <div class="form-group">
              <label>Beginn</label>
              <div class="input-group">
                <flat-pickr v-model="item.start" :config="dateConfig" class="form-control" placeholder="Wähle ein Datum"
                            name="date">
                </flat-pickr>
                <div class="input-group-btn">
                  <button class="btn btn-default" type="button" title="Toggle" data-toggle>
                    <respondeco-icon icon="fal calendar"></respondeco-icon>
                  </button>
                  <button class="btn btn-default" type="button" title="Clear" data-clear>
                    <respondeco-icon icon="fal times"></respondeco-icon>
                  </button>
                </div>
              </div>
            </div>

            <!-- End -->
            <div class="form-group">
              <label>Ende</label>
              <div class="input-group">
                <flat-pickr v-model="item.end" :config="dateConfig" class="form-control" placeholder="Wähle ein Datum"
                            name="date">
                </flat-pickr>
                <div class="input-group-btn">
                  <button class="btn btn-default" type="button" title="Toggle" data-toggle>
                    <respondeco-icon icon="fal calendar"></respondeco-icon>
                  </button>
                  <button class="btn btn-default" type="button" title="Clear" data-clear>
                    <respondeco-icon icon="fal times"></respondeco-icon>
                  </button>
                </div>
              </div>
            </div>
          </div>
          <div class="col-md-3">
            <div class="form-group">
              <label>Logo</label>

              <admin-file-chooser :initial="imageUrl(item.image)" :quality="2" v-model="image"></admin-file-chooser>
            </div>
          </div>
        </div>
      </form>

      <div class="text-right" slot="footer">
        <button @click="back" type="button" class="btn btn-link">Abbrechen</button>
        <button @click="save" :disabled="$isLoading('global-loader')" type="submit"
                class="btn btn-primary d-inline-flex align-items-center">
          <transition name="fade" mode="out-in">
            <spinner style="margin-right: 8px" size="small" v-if="$isLoading('global-loader')"></spinner>
          </transition>

          <span>
            Speichern
          </span>
        </button>
      </div>
    </admin-card>
  </admin-page>
</template>

<script>
  import {
    Categories, DateConfig, ImageHelper, ImageMixin, Notifications, ObjectNormaliser,
    Subcategories
  } from '../../common/utils'

  import Organisations from 'common/services/organisations'
  import Projects from 'common/services/projects'

  export default {
    name: 'project',

    computed: {
      isNew () {
        return this.id === 'new'
      },

      method () {
        return this.isNew ? 'save' : 'update'
      },

      subcategories () {
        return Subcategories[this.item.category]
      }
    },

    created () {
      this.fetchData(this.id)
    },

    data () {
      return {
        activeId: this.id,
        categories: Categories,
        dateConfig: DateConfig,
        image: {},
        item: ObjectNormaliser.project(),
        organisations: []
      }
    },

    methods: {
      back () {
        this.$router.push({
          name: 'projects'
        })
      },

      fetchData (id = this.activeId) {
        let loading = this.isNew ? 1 : 0

        this.$startLoading('card')

        Organisations.all().then(response => {
          this.organisations = response.body.items

          if (++loading === 2) {
            this.$endLoading('card')
          }
        })

        if (!this.isNew) {
          Projects.byId(id).then(response => {
            this.item = response.body

            if (++loading === 2) {
              this.$endLoading('card')
            }
          })
        }
      },

      save () {
        this.$startLoading('global-loader')

        ImageHelper.saveFromCroppa(this.image, 'image.png', 'image').then((value) => {
            let item = Object.assign({
              price: 0
            }, this.item, value)

            if (!item.end) {
              item.end = undefined
            }

            if (!item.start) {
              item.start = undefined
            }

            Projects[this.method](item).then(
              Notifications.success(this, (response) => {
                if (this.isNew) {
                  this.$router.push({
                    name: 'project',
                    params: {
                      id: response.body.id
                    }
                  }, () => {
                    this.activeId = response.body.id
                    this.item = response.body
                  })
                }
              }),
              Notifications.error(this)
            )
          },
          Notifications.error(this, 'Fehler beim Bildupload')
        )
      }
    },

    mixins: [
      ImageMixin
    ],

    props: ['id']
  }
</script>
