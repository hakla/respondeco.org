<template>
  <admin-page-item :loader="['card', 'organisations']" :item="item" :routeBack="routeBack" :title="`Account bearbeiten`" @submit="save">
    <div class="row">
      <div class="col">
        <!-- name -->
        <div class="form-group">
          <label for="name">Benutzername</label>
          <input type="text" class="form-control" id="name" v-model="item.name">
        </div>

        <!-- email -->
        <div class="form-group">
          <label for="email">E-Mail</label>
          <input type="text" class="form-control" id="email" v-model="item.email" required>
        </div>

        <!--Passwort-->
        <div class="form-group">
          <label for="password">Passwort</label>
          <input class="form-control" type="password" id="password" v-model="item.password" required v-if="isNew">
          <input class="form-control" type="password" id="password" v-model="item.password" v-else>
        </div>

        <!--Rolle-->
        <div class="form-group">
          <label for="role">Rolle</label>
          <select class="form-control" name="role" id="role" v-model="item.role" slot="body">
            <option value="User">User</option>
            <option value="Administrator">Administrator</option>
          </select>
        </div>

        <div class="form-group">
          <label for="organisation">Organisation</label>
          <select class="form-control" name="organisation" id="organisation" v-model="item.organisationId" slot="body">
            <option :value="null">---</option>
            <option v-for="organisation in organisations" :key="organisation.id" :value="organisation.id">
              {{ organisation.name }}
            </option>
          </select>
        </div>
      </div>
      <div class="col-md-3">
        <div class="form-group">
          <label>Profilbild</label>

          <admin-file-chooser :initial="imageUrl(item.image)" :quality="2" v-model="image"></admin-file-chooser>
        </div>
      </div>
    </div>
  </admin-page-item>
</template>

<script>
  import { ImageHelper, ImageMixin, Notifications, ObjectNormaliser } from '../../common/utils'
  import { AdminAccounts } from '../../common/services/accounts'
  import ItemPage from '../mixins/item-page'
  import Organisations from '../../common/services/organisations'
  import LoaderHelper from '../../common/mixins/loader-helper'

  export default {
    name: 'account',

    created () {
      this.$startLoading('organisations')

      Organisations.all().then(result => {
        this.organisations = result.body.items

        this.$endLoading('organisations')
      })
    },

    data () {
      return {
        image: {},
        item: ObjectNormaliser.account(),
        organisations: [],
        routeBack: {
          name: 'accounts'
        },
        service: AdminAccounts
      }
    },

    methods: {
      save () {
        this.promiseLoading(
          ImageHelper.saveFromCroppa(this.image, 'image.jpeg', 'image', ['image/jpeg', 0.9]).then(value =>
            AdminAccounts[this.method](Object.assign(this.item, value)).then(
              Notifications.success(this),
              Notifications.error(this)
            )
          )
        )
      }
    },

    mixins: [ItemPage, ImageMixin, LoaderHelper]
  }
</script>

<style scoped>

</style>
