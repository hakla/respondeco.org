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
                <option v-for="category in categories">{{ category }}</option>
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
        <button @click="back" type="button" class="btn btn-link">Abbrechen</button>
        <button @click="save" :disabled="$isLoading('global-loader')" type="submit" class="btn btn-primary d-inline-flex align-items-center">
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
  import { Categories, ImageHelper, ImageMixin, Notifications, ObjectNormaliser } from '../../common/utils'
  import Organisations from 'common/services/organisations'
  import Spinner from 'vue-simple-spinner'

  export default {
    name: 'organisation',

    components: {
      Spinner,
    },

    created () {
      this.fetchData()
    },

    data () {
      return {
        banner: {},
        categories: Categories,
        image: {},
        item: ObjectNormaliser.organisation(),
        logo: {}
      }
    },

    methods: {
      back () {
        this.$router.back()
      },

      fetchData () {
        this.$startLoading('card')

        Organisations.byId(this.id).then(response => {
          this.item = response.body

          this.$endLoading('card')
        })
      },

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

          Organisations.update(item).then(
            Notifications.success(this),
            Notifications.error(this)
          )
        }, Notifications.error(this, "Fehler beim Bildupload"))
      }
    },

    mixins: [
      ImageMixin
    ],

    props: ['id']
  }
</script>
