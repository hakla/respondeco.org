<template>
  <admin-page-item :item="item" :loader="['card', 'accounts', 'organisations']" :routeBack="routeBack"
                   :title="`Account bearbeiten`" @back="back" @submit="save">
    <div class="row">
      <div class="col-9">
        <!-- author -->
        <div class="form-group">
          <label>Autor</label>
          <multiselect
            v-model="item.author.data"
            placeholder=""
            deselectLabel=""
            selectLabel=""
            selectedLabel="Ausgewählt"
            :options="authors"
            track-by="name"
            label="name"
            group-label="category"
            group-values="values"
            @select="setAuthor"
          ></multiselect>
        </div>

        <div class="form-group">
          <label for="title">Titel</label>
          <input type="text" class="form-control" name="title" id="title" v-model="item.title">
        </div>

        <!-- content -->
        <div class="form-group">
          <label for="content">Inhalt</label>
          <textarea name="content" id="content" rows="5" class="form-control" v-autosize
                    v-model="item.content"></textarea>
        </div>

        <!-- date -->
        <div class="form-group">
          <label>Datum</label>
          <div class="input-group">
            <flat-pickr v-model="item.date" :config="dateConfig" class="form-control" placeholder="Wähle ein Datum"
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

        <!-- video -->
        <div class="form-group">
          <label for="video">Youtube Video ID</label>
          <input class="form-control" id="video" name="video" type="text" v-model="item.video">
        </div>
      </div>
      <div class="col-3">
        <div class="form-group">
          <label>Bild</label>
          <admin-file-chooser :height="168" :initial="imageUrl(item.image)" :quality="2"
                              v-model="image"></admin-file-chooser>
        </div>
      </div>
    </div>
  </admin-page-item>
</template>

<script>
  import Comments from '../../common/services/comments'
  import ItemPage from '../mixins/item-page'
  import LoaderHelper from '../../common/mixins/loader-helper'
  import { DateHelper, ImageHelper, ImageMixin, Notifications, ObjectNormaliser } from '../../common/utils'

  import Multiselect from 'vue-multiselect'
  import { AdminAccounts } from '../../common/services/accounts'
  import Organisations from '../../common/services/organisations'

  export default {
    name: 'comment',

    components: {
      Multiselect
    },

    created () {
      this.fetchData()
      this.loadUsers()
      this.loadOrganisations()
    },

    data () {
      return {
        authors: [],
        image: {},
        item: ObjectNormaliser.comment(),
        routeBack: {
          name: this.type,
          params: {
            id: this.typeId
          }
        },
        service: Comments,
        value: {}
      }
    },

    methods: {
      loadOrganisations () {
        this.promiseLoading(
          Organisations.all().then(response => {
            this.authors.push({
              category: 'Unternehmen',
              values: response.body.items.map(item => ({
                id: item.id,
                name: item.name,
                organisation: item
              }))
            })
          })
        )
      },

      loadUsers () {
        this.promiseLoading(
          AdminAccounts.all().then(response => {
            this.authors.push({
              category: 'Benutzer',
              values: response.body.items.map(item => ({
                id: item.id,
                name: item.name,
                user: item
              }))
            })
          }),

          'accounts'
        )
      },

      save () {
        if (this.item.author.data == null) {
          this.$notify({
            title: 'Autor muss ausgewählt sein',
            type: 'error'
          })
        } else {
          ImageHelper.saveFromCroppa(this.image, 'image.jpeg', 'image', ['image/jpeg', 0.9]).then(value => {
            let item = Object.assign({}, this.item, {
              author: this.item.author.data.id,
              date: DateHelper.formatAsDateTime(this.item.date)
            }, value)

            this.promiseLoading(
              Comments[this.method](item, this.type, this.typeId).then(
                Notifications.success(this, response => {
                  this.updateRoute(response.body)
                }),
                Notifications.error(this)
              )
            )
          })
        }
      },

      setAuthor (author) {
        this.item.author.data = author
      }
    },

    mixins: [ImageMixin, ItemPage, LoaderHelper],

    props: ['type', 'typeId']
  }
</script>

<style scoped>

</style>
