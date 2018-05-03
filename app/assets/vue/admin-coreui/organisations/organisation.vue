<template>
  <admin-page>
    <admin-card>
      <span slot="header">Organisation {{ item.name }} bearbeiten</span>

      <form @submit.prevent="save">
        <div class="row">
          <div class="col">
            <!-- Name -->
            <div class="form-group">
              <label for="name">Name der Organisation</label>
              <input type="text" id="name" required="required" class="form-control" v-model="item.name">
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
                <option v-for="category in categories" :key="category">{{ category }}</option>
              </select>
            </div>

            <!-- E-Mail -->
            <div class="form-group">
              <label for="email">E-Mail</label>

              <input type="text" id="email" required="required" class="form-control" v-model="item.email">
            </div>

            <!-- Website -->
            <div class="form-group">
              <label for="website">Website</label>

              <input type="text" id="website" required="required" class="form-control" v-model="item.website">
            </div>

            <!-- Standort -->
            <div class="form-group">
              <label for="location">Standort</label>

              <input type="text" id="location" required="required" class="form-control" v-model="item.location">
            </div>

            <!-- Youtube-Banner -->
            <div class="form-group">
              <label for="banner_video">Banner Video</label>

              <input type="text" id="banner_video" class="form-control" v-model="item.video">
            </div>
          </div>

          <!-- Bilder -->
          <div class="col-md-3">
            <div class="form-group">
              <label class="mr-1">
                Verifiziert
              </label>
              <label class="switch switch-text switch-pill switch-primary">
                <input type="checkbox" class="switch-input" :checked="item.verified" v-model="item.verified">
                <span class="switch-label" data-on="Ja" data-off="Nop"></span>
                <span class="switch-handle"></span>
              </label>
            </div>

            <div class="form-group">
              <label>Logo</label>

              <admin-file-chooser :initial="imageUrl(item.logo)" :quality="2" v-model="logo"></admin-file-chooser>
            </div>

            <div class="form-group">
              <label>Banner</label>

              <admin-file-chooser :initial="imageUrl(item.image)" canvas-color="#fff" :height="169" :quality="8" v-model="image"></admin-file-chooser>
            </div>
          </div>
        </div>
      </form>

      <div class="text-right" slot="footer">
        <admin-button @click="back" type="button" class="btn-link">
          Abbrechen
        </admin-button>
        <admin-button class="btn-primary" loader="global-loader" @click="save">
          Speichern
        </admin-button>
      </div>
    </admin-card>
  </admin-page>
</template>

<script>
  import { Categories, ImageHelper, ImageMixin, Notifications, ObjectNormaliser } from '../../common/utils'
  import Organisations from 'common/services/organisations'
  import ItemPage from '../mixins/item-page'

  export default {
    name: 'organisation',

    data () {
      return {
        banner: {},
        categories: Categories,
        image: {},
        item: ObjectNormaliser.organisation(),
        logo: {},
        routeBack: {
          name: 'organisations'
        },
        service: Organisations
      }
    },

    methods: {
      save () {
        this.$startLoading('global-loader')

        let promises = [
          ImageHelper.saveFromCroppa(this.image, 'image.jpg', 'image', ['image/jpeg', 0.9]),
          ImageHelper.saveFromCroppa(this.logo, 'logo.png', 'logo')
        ]

        Promise.all(promises).then((values) => {
          let item = Object.assign({}, this.item)

          for (let i = 0; i < values.length; ++i) {
            item = Object.assign({}, item, values[i])
          }

          Organisations[this.method](item).then(
            Notifications.success(this, response => {
              this.updateRoute(response.body)
            }),
            Notifications.error(this)
          )
        }, Notifications.error(this, "Fehler beim Bildupload"))
      }
    },

    mixins: [
      ImageMixin,
      ItemPage
    ],

    props: ['id']
  }
</script>
