<template>
<div class="">
  <div class="page-title">
    <div class="title_left">
      <h3>Organisation bearbeiten</h3>
    </div>
  </div>
  <div class="clearfix"></div>
  <div class="row">
    <div v-if="loading">
      Loading...
    </div>
    <div class="col-md-12 col-sm-12 col-xs-12" v-if="!loading">
      <div class="x_panel">
        <div class="x_title">
          <h2>{{ organisation.name }}</h2>
          <div class="clearfix"></div>
        </div>
        <div class="x_content">
          <br>
          <form id="demo-form2" data-parsley-validate="" class="form-horizontal form-label-left" novalidate @submit.stop.prevent="save()">
            <div class="form-group">
              <label class="control-label col-md-3 col-sm-3 col-xs-12" for="first-name">Name der Organisation <span class="required">*</span>
              </label>
              <div class="col-md-6 col-sm-6 col-xs-12">
                <input type="text" id="first-name" required="required" class="form-control col-md-7 col-xs-12" v-model="organisation.name">
              </div>
            </div>
            <div class="ln_solid"></div>
            <div class="form-group">
              <div class="col-md-6 col-sm-6 col-xs-12 col-md-offset-3">
                <button class="btn btn-primary" @onclick="cancel()">Abbrechen</button>
                <button type="submit" class="btn btn-success">Speichern</button>
              </div>
            </div>

          </form>
        </div>
      </div>
    </div>
  </div>
</div>
</template>

<script>
import {
  router
} from '../router';

export default {
  name: "organisation",
  created() {
    this.resource = this.$resource('organisations/{id}')
    this.fetchData()
  },
  data() {
    return {
      loading: false,
      organisation: null
    }
  },
  methods: {
    cancel () {
      router.push('/organisations')
    },
    fetchData() {
      this.loading = true

      this.resource.get({
        id: this.$route.params.id
      }).then(response => {
        this.organisation = response.body
        this.loading = false
      })
    },
    save() {
      this.resource.update({
        id: this.$route.params.id
      }, this.organisation).then((response) => {
        this.cancel()
      });
    }
  }
}
</script>

<style>

</style>
