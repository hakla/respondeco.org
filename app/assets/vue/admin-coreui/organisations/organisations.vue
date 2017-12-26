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
          <th>Name</th>
          <th>Beschreibung</th>
          <th></th>
        </tr>

        <tbody>
          <tr v-for="item in list" :key="item.id" @click="open(item.id)">
            <td>{{ item.id }}</td>
            <td>
              <a>{{ item.name }}</a>
            </td>
            <td>
              {{ item.description }}
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
  import { ObjectNormaliser } from 'common/utils'
  import Organisations from 'common/services/organisations'

  import Overview from '../mixins/overview'

  export default {
    name: 'organisations',

    data () {
      return {
        normaliser: ObjectNormaliser.organisation,
        route: 'organisation',
        service: Organisations,
        title: 'Alle Organisationen'
      }
    },

    mixins: [Overview]
  }
</script>

<style scoped>
  tr {
    cursor: pointer;
  }
</style>
