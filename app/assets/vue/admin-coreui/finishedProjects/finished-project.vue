<template>
  <admin-page-item :item="item" :loader="['card', 'organisations', 'projects']" :routeBack="routeBack" @submit="save" title="Abgeschlossenes Projekt">
    <div class="row">
      <div class="col">
        <!-- Projekt -->
        <div class="form-group">
          <label for="project">Projekt</label>
          <select class="form-control" id="project" v-model="item.project">
            <option :value="project.id" v-for="project in projects">{{ project.name }}</option>
          </select>
        </div>

        <!-- Organisation -->
        <div class="form-group">
          <label for="organisation">Organisation</label>
          <select class="form-control" id="organisation" v-model="item.organisation">
            <option :value="organisation.id" v-for="organisation in organisations">{{ organisation.name }}</option>
          </select>
        </div>

        <!-- Date -->
        <div class="form-group">
          <label>Datum der Durchführung</label>
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

        <!-- Bewertung Organisation -->
        <admin-rating label="Bewertung Organisation" v-model="item.ratingOrganisation"></admin-rating>

        <!-- Bewertung Projekt -->
        <admin-rating label="Bewertung Projekt" v-model="item.ratingOwner"></admin-rating>
      </div>
    </div>
  </admin-page-item>
</template>

<script>
  import AdminRating from '../components/rating'
  import ItemPage from '../mixins/item-page'
  import Organisations from '../../common/services/organisations'
  import Projects from '../../common/services/projects'
  import { ObjectNormaliser } from '../../common/utils'
  import FinishedProjects from '../../common/services/finished-projects'

  export default {
    name: 'finished-project',

    components: {
      AdminRating
    },

    created () {
      this.$startLoading('organisations')

      Organisations.all().then(result => {
        this.organisations = result.body.items

        this.$endLoading('organisations')
      })

      Projects.all().then(result => {
        this.projects = result.body.items

        this.$endLoading('projects')
      })
    },

    data () {
      return {
        item: ObjectNormaliser.finishedProject(),
        organisations: [],
        projects: [],
        routeBack: {
          name: 'finishedProjects'
        },
        service: FinishedProjects
      }
    },

    mixins: [ItemPage]
  }
</script>

<style scoped>

</style>
