<template>
<div class="">
  <div class="page-title">
    <div class="title_left">
      <h3>Alle Organisationen ({{ organisations.length }})</h3>
    </div>

    <div class="title_right">
      <div class="col-md-5 col-sm-5 col-xs-12 form-group pull-right top_search">
        <div class="input-group">
          <input type="text" class="form-control" placeholder="Search for..." v-model="filter" @input="updateList(filter)">
          <span class="input-group-btn">
              <button class="btn btn-default" type="button">Go!</button>
          </span>
        </div>
      </div>
    </div>
  </div>

  <div class="clearfix"></div>

  <div class="row">
    <div class="col-md-12">
      <div class="x_panel">
        <div class="x_title">
          <h2>Organisationen</h2>
          <ul class="nav navbar-right panel_toolbox">
            <li>
              <router-link class="collapse-link" :to="{ name: 'organisation', params: { id: 'new' } }">
                <button class="btn btn-primary">Neue Organisation</button>
              </router-link>
            </li>
          </ul>
          <div class="clearfix"></div>
        </div>
        <div class="x_content">

          <p>Übersicht aller registrierten Organisationen</p>

          <!-- start project list -->
          <table class="table table-striped projects">
            <thead>
              <tr>
                <th style="width: 1%">#</th>
                <th style="width: 20%">Name</th>
                <th>Beschreibung</th>
                <th style="width: 20%"></th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="organisation in list">
                <td @click="gotoOrganisation(organisation.id)">{{ organisation.id }}</td>
                <td @click="gotoOrganisation(organisation.id)">
                  <a>{{ organisation.name }}</a>
                </td>
                <td @click="gotoOrganisation(organisation.id)">
                  {{ organisation.description }}
                </td>
                <td class="text-right">
                  <a href="#" class="btn btn-danger btn-xs" @click.prevent="remove(organisation.id)"><i class="fa fa-trash-o"></i> Delete </a>
                </td>
              </tr>
            </tbody>
          </table>
          <!-- end project list -->
        </div>
      </div>
    </div>
  </div>
</div>
</template>

<script>
import { router } from '../router';

export default {
  name: 'OrganisationsOverview',

  created() {
    this.resource = this.$resource('organisations{/id}');
    this.fetchData();
  },

  data() {
    return {
      filter: '',
      list: [],
      organisations: []
    }
  },

  methods: {
    fetchData() {
      this.resource.get().then(response => {
        this.organisations = response.body;
        this.updateList();
      });
    },
    gotoOrganisation (id) {
      router.push(`/organisations/${id}`)
    },
    remove(id) {
      this.resource.delete({id}).then(response => this.fetchData())
    },
    updateList (filter) {
      if (filter) {
        this.list = this.organisations.filter(organisation => organisation.name.indexOf(filter) > -1);
      } else {
        this.list = this.organisations;
      }
    }
  }
}
</script>

<style>
  tbody tr {
    cursor: pointer;
  }
</style>
