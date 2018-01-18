<template>
  <div>
    <admin-page-item :loader="['card', 'organisations']" :item="item" :routeBack="routeBack" :title="`Projekt bearbeiten`" @submit="save">
      <div class="row">
        <div class="col-md-9">
          <!-- Name -->
          <div class="form-group">
            <label for="name">Name des Projekts</label>
            <input type="text" id="name" required class="form-control" v-model="item.name">
          </div>

          <!-- Organisation -->
          <div class="form-group">
            <label for="organisation">Organisation</label>

            <select class="form-control" id="organisation" v-model="item.organisation.id">
              <option :value="organisation.id" v-for="organisation in organisations" :key="organisation.id">{{ organisation.name }}</option>
            </select>
          </div>

          <!-- Beschreibung -->
          <div class="form-group">
            <label for="description">Beschreibung</label>

            <textarea id="description" rows="10" class="form-control" v-model="item.description">
              </textarea>
          </div>

          <!-- Kategorie -->
          <div class="form-group">
            <label for="category">Kategorie</label>

            <select class="form-control" id="category" v-model="item.category">
              <option>---</option>
              <option v-for="(category, index) in categories" :key="index">{{ category }}</option>
            </select>
          </div>

          <!-- Unterkategorie -->
          <div class="form-group">
            <label for="subcategory">Unterkategorie</label>

            <select class="form-control" id="subcategory" v-model="item.subcategory">
              <option>---</option>
              <option v-for="(subcategory, index) in subcategories" :key="index">{{ subcategory }}</option>
            </select>
          </div>

          <!-- Preis -->
          <div class="form-group">
            <label for="price">Preis</label>

            <input type="number" id="price" class="form-control" v-model.number="item.price">
          </div>

          <!-- Standort -->
          <div class="form-group">
            <label for="location">Standort</label>

            <input type="text" id="location" class="form-control" v-model="item.location">
          </div>

          <!-- Vorteile -->
          <div class="form-group">
            <label for="benefits">Vorteile</label>

            <input type="text" id="benefits" class="form-control" v-model="item.benefits">
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
    </admin-page-item>

    <!-- Comments -->
    <admin-comments :id="activeId" type="project" v-if="!isNew"></admin-comments>
  </div>
</template>

<script>
  import {
    Categories, DateConfig, ImageHelper, ImageMixin, Notifications, ObjectNormaliser,
    Subcategories
  } from '../../common/utils'

  import AdminComments from '../comments/comments'

  import Organisations from 'common/services/organisations'
  import Projects from 'common/services/projects'
  import ItemPage from '../mixins/item-page'
  import LoaderHelper from '../../common/mixins/loader-helper'

  export default {
    name: 'project',

    components: {
      AdminComments
    },

    computed: {
      subcategories () {
        return Subcategories[this.item.category]
      }
    },

    created () {
      this.promiseLoading(
        Organisations.all().then(response => {
          this.organisations = response.body.items
        }),

        'organisations'
      )
    },

    data () {
      return {
        activeId: this.id,
        categories: Categories,
        dateConfig: DateConfig,
        image: {},
        item: ObjectNormaliser.project(),
        routeBack: {
          name: 'projects'
        },
        organisations: [],
        service: Projects
      }
    },

    methods: {
      save () {
        this.$startLoading('global-loader')

        ImageHelper.saveFromCroppa(this.image, 'image.png', 'image').then(value => {
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

    mixins: [ItemPage, ImageMixin, LoaderHelper]
  }
</script>
