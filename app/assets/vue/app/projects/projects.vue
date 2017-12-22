<template>
  <section class="g-pt-100">
    <respondeco-breadcrumbs :heading="$tc('common.project', 2)"></respondeco-breadcrumbs>
    <respondeco-portfolio :items="list" v-if="!loading"></respondeco-portfolio>
    <vue-simple-spinner class="g-mb-100 g-mt-100" v-else></vue-simple-spinner>
  </section>
</template>

<script>
  import RespondecoBreadcrumbs from 'app/main/breadcrumbs'
  import RespondecoPortfolio from 'app/main/portfolio'
  import ProjectService from 'app/projects/projects-service'
  import Utils from 'app/utils'
  import VueSimpleSpinner from 'vue-simple-spinner'

  let toPortfolioItem = Utils.toPortfolioItem("projects")

  export default {
    name: 'projects',

    components: {
      RespondecoPortfolio,
      RespondecoBreadcrumbs,
      VueSimpleSpinner
    },

    created () {
      ProjectService.init(this);
      this.fetchData();
    },

    data() {
      return {
        filter: '',
        list: [],
        loading: false,
        projects: []
      }
    },

    methods: {
      fetchData () {
        this.loading = true

        ProjectService.all().then(response => {
          this.projects = response.body
          this.updateList()

          this.loading = false
        });
      },
      updateList (filter) {
        if (filter) {
          this.list = this.projects.filter(project => project.name.indexOf(filter) > -1).map(toPortfolioItem);
        } else {
          this.list = this.projects.map(toPortfolioItem);
        }
      }
    }
  }
</script>

<style lang="css">
</style>
