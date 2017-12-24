<template>
  <admin-page @filter="updateFilter" title="Übersicht Accounts">
    <h2 slot="title">Alle Accounts ({{ list.length }})</h2>

    <admin-filter slot="title-right" @filter="updateFilter"></admin-filter>

    <router-link class="collapse-link" :to="{ name: 'account', params: { id: 'new' } }" slot="action">
      <button class="btn btn-primary">Neuer Account</button>
    </router-link>

    <table class="table table-striped projects" slot="body">
      <thead>
        <tr>
          <th>ID</th>
          <th>Name</th>
          <th>E-Mail</th>
          <th>Rolle</th>
          <th>Organisation ID</th>
          <th></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="account in list" :key="account.id" @click="openAccount(account.id)">
          <td>{{ account.id }}</td>
          <td>{{ account.name }}</td>
          <td>{{ account.email }}</td>
          <td>{{ account.role }}</td>
          <td>{{ account.organisationId }}</td>
          <td class="text-right" @click.stop.prevent>
            <a href="#" class="btn btn-danger btn-xs" @click.prevent.stop="remove(account.id)"><i
              class="fa fa-trash-o"></i> Delete </a>
          </td>
        </tr>
      </tbody>
    </table>
  </admin-page>
</template>

<script>
  import Accounts from 'common/services/accounts'
  import { router } from 'admin/router'

  export default {
    name: "accounts",

    computed: {
      list () {
        return this.items.filter(item => item.name.toLowerCase().indexOf(this.filter) > -1)
      }
    },

    created () {
      this.fetchData()
    },

    data () {
      return {
        filter: '',
        items: []
      }
    },

    methods: {
      fetchData () {
        this.$startLoading('admin-page-loader')

        Accounts.all().then(response => {
          this.items = response.body

          this.$endLoading('admin-page-loader')
        })
      },

      openAccount (id) {
        router.push({
          name: 'account',
          params: { id }
        })
      },

      remove (id) {
        console.log(id)
      },

      updateFilter (filter) {
        this.filter = filter.toLowerCase()
      }
    }
  }
</script>

<style scoped>
  tbody tr {
    cursor: pointer;
  }
</style>
