<template>
  <admin-page>
    <admin-card>
      <div class="d-flex" slot="header">
        <span class="flex-grow-1">{{ title }}</span>
        <button class="btn btn-primary btn-sm" @click="open('new')">Neuer Eintrag</button>
      </div>

      <admin-table>
        <tr>
          <th>#</th>
          <th>Projekt</th>
          <th>Organisation</th>
          <th>Durchgeführt am</th>
          <th></th>
        </tr>

        <tbody>
          <tr v-for="item in list" :key="item.id" @click="open(item.id)">
            <td>{{ item.id }}</td>
            <td>
              <a>{{ item.project.name }}</a>
            </td>
            <td>
              {{ item.organisation.name }}
            </td>
            <td>
              {{ item.date | formatDate }}
            </td>
            <td class="text-right" @click.prevent.stop>
              <admin-button class="btn-danger btn-sm" :loader="`deleting-${item.id}`" @click="remove(item.id)">
                <respondeco-icon icon="trash"></respondeco-icon>
                Löschen
              </admin-button>
            </td>
          </tr>
        </tbody>
      </admin-table>
    </admin-card>
  </admin-page>
</template>

<script>
  import Overview from '../mixins/overview'
  import { ObjectNormaliser } from '../../common/utils'
  import FinishedProjects from '../../common/services/finished-projects'
  import DateFilter from '../../common/mixins/DateFilter'

  export default {
    name: 'finished-projects',

    data () {
      return {
        service: FinishedProjects,
        title: 'Abgeschlossene Projekte'
      }
    },

    methods: {
      open (id) {
        this.$router.push({
          name: 'finishedProject',
          params: {
            id
          }
        })
      }
    },

    mixins: [DateFilter, Overview]
  }
</script>

<style scoped>
  tr {
    cursor: pointer;
  }
</style>
