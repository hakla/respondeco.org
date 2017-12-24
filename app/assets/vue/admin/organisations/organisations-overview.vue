<template>
  <admin-page title="Übersicht Organisationen">
    <h3 slot="title">Alle Organisationen ({{ items.length }})</h3>

    <admin-filter slot="title-right" @filter="updateFilter"></admin-filter>

    <router-link class="collapse-link" :to="{ name: 'organisation', params: { id: 'new' } }" slot="action">
      <button class="btn btn-primary">Neue Organisation</button>
    </router-link>

    <table class="table table-striped" slot="body">
      <thead>
        <tr>
          <th style="width: 1%">#</th>
          <th style="width: 20%">Name</th>
          <th>Beschreibung</th>
          <th style="width: 20%"></th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="organisation in list" :key="organisation.id">
          <td @click="openOrganisation(organisation.id)">{{ organisation.id }}</td>
          <td @click="openOrganisation(organisation.id)">
            <a>{{ organisation.name }}</a>
          </td>
          <td @click="openOrganisation(organisation.id)">
            {{ organisation.description }}
          </td>
          <td class="text-right">
            <a href="#" class="btn btn-danger btn-xs" @click.prevent="remove(organisation.id)"><i
              class="fa fa-trash-o"></i> Delete </a>
          </td>
        </tr>
      </tbody>
    </table>
  </admin-page>
</template>

<script>
  import { router } from '../router'
  import Organisations from 'common/services/organisations'

  export default {
    name: 'OrganisationsOverview',

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

        Organisations.all().then(response => {
          this.items = response.body

          this.$endLoading('admin-page-loader')
        })
      },

      openOrganisation (id) {
        router.push(`/organisations/${id}`)
      },

      remove (id) {
        Organisations.remove({id}).then(response => this.fetchData())
      },

      updateFilter (filter) {
        this.filter = filter
      }
    }
  }
</script>

<style>
  tbody tr {
    cursor: pointer;
  }
</style>
