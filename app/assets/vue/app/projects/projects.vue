<template>
  <div>
    <respondeco-breadcrumbs :heading="$tc('common.project', 2)"></respondeco-breadcrumbs>
    <respondeco-portfolio :items="list"></respondeco-portfolio>
  </div>
</template>

<script>
  import RespondecoBreadcrumbs from 'app/main/breadcrumbs'
  import RespondecoPortfolio from 'app/main/portfolio'
  import ProjectService from 'app/projects/projects-service'


  export default {
    name: 'projects',

    components: {
      'respondecoPortfolio': RespondecoPortfolio,
      'respondecoBreadcrumbs': RespondecoBreadcrumbs
    },

    created () {
      ProjectService.init(this);
      this.fetchData();
    },

    data() {
      return {
        filter: '',
        list: [],
        projects: []
      }
    },

    methods: {
      fetchData () {
        ProjectService.all().then(response => {
          this.projects = response.body;
          this.updateList();
        });
      },
      updateList (filter) {
        if (filter) {
          this.list = this.projects.filter(organisation => organisation.name.indexOf(filter) > -1).map(toPortfolioItem);
        } else {
          this.list = this.projects.map(toPortfolioItem);
        }
      }
    }
  }

  function toPortfolioItem (item) {
    return {
      title: item.name,
      description: item.description,
      image: '/assets/images/img3.jpg'
    }
  }
</script>

<style lang="css">
</style>
