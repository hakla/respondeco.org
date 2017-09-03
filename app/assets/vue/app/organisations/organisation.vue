<template>
  <div class="wrapper">
    <div class="breadcrumbs-v3 text-center" :style="backgroundImage(organisation.image)"></div>
    <div class="service-block-v4">
      <div class="container">
        <div class="row">
          <div class="col-xs-12">
            <h1>{{ organisation.name }}</h1>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import Config from 'common/config'
  import Utils from 'common/utils'
  import RespondecoBreadcrumbs from 'app/main/breadcrumbs'
  import {
    router
  } from '../router'

  export default {
    name: 'organisation',

    components: {
      RespondecoBreadcrumbs
    },

    created () {
      this.organisation = {}
      this.resource = this.$resource('organisations{/id}')

      if (this.$route.params.id !== 'new') {
        this.fetchData()
      } else {
        this.isNew = true
      }
    },

    data () {
      return {
        organisation: this.organisation
      }
    },

    methods: {
      backgroundImage: Utils.backgroundImage,

      fetchData() {
        this.loading = true

        this.resource.get({
          id: this.$route.params.id
        }).then(response => {
          this.organisation = response.body
          this.loading = false
        })
      },
    }
  }
</script>

<style>
  .breadcrumbs-v3 {
    background-size: cover;
    background-position-y: 50%;

    height: 400px;
  }
</style>
