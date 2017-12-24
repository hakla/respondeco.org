<template>
  <admin-page title="Account bearbeiten">
    <h2 slot="title">Account: {{ account.name }}</h2>

    <form class="form-horizontal" @submit.prevent="save" slot="body">

      <!-- Name -->
      <form-group id="name" label="Name">
        <input type="text" class="form-control" id="name" slot="body" v-model="account.name">
      </form-group>

      <!--E-Mail-->
      <form-group id="email" label="E-Mail (Login)">
        <input type="text" class="form-control" id="email" slot="body" v-model="account.email">
      </form-group>

      <!--Passwort-->
      <form-group id="password" label="Passwort">
        <input class="form-control" type="password" id="password" v-model="account.password" slot="body">
      </form-group>

      <!--Rolle-->
      <form-group id="role" label="Rolle">
        <select class="form-control" name="role" id="role" v-model="account.role" slot="body">
          <option value="User">User</option>
          <option value="Administrator">Administrator</option>
        </select>
      </form-group>

      <form-group id="organisation" label="Organisation">
        <select class="form-control" name="organisation" id="organisation" v-model="account.organisationId" slot="body">
          <option v-for="organisation in organisations" :key="organisation.id" :value="organisation.id">
            {{ organisation.name }}
          </option>
        </select>
      </form-group>

      <div class="form-group">
        <div class="col-md-6 col-sm-6 col-xs-12 col-md-offset-3 text-right">
          <button type="button" class="btn btn-primary" @click="cancel()">Abbrechen</button>
          <button type="submit" class="btn btn-success">Speichern</button>
        </div>
      </div>

    </form>
  </admin-page>
</template>

<script>
  import Accounts from 'common/services/accounts'
  import Organisations from 'common/services/organisations'
  import { Notifications } from 'common/utils'

  export default {
    name: "account",

    created () {
      this.isNew = this.$route.params.id === 'new'

      this.fetchData()
    },

    data () {
      return {
        account: {
          email: '',
          password: '',
          organisationId: null,
          role: 'User',
          title: 'Neuer Account'
        },
        isNew: false,
        organisations: []
      }
    },

    methods: {
      cancel () {
        this.$router.push('/accounts')
      },

      fetchData () {
        let done = 0
        this.$startLoading('admin-page-loader')

        if (!this.isNew) {
          Accounts.byId(this.$route.params.id).then(response => {
            this.account = response.body

            if (++done === 2) {
              this.$endLoading('admin-page-loader')
            }
          })
        } else {
          ++done
        }

        Organisations.all().then(response => {
          this.organisations = [{
            id: null,
            name: 'Keiner Organisation zuordnen'
          }].concat(response.body)

          if (++done === 2) {
            this.$endLoading('admin-page-loader')
          }
        })
      },

      save () {
        let request

        if (this.isNew) {
          request = Accounts.post('admin/accounts', this.account)
        } else {
          request = Accounts.put(`admin/accounts/${this.account.id}`, this.account)
        }

        request.then(
          Notifications.success(this),
          Notifications.error(this)
        )
      }
    }
  }
</script>

<style scoped>

</style>
