<template>
  <admin-page>
    <admin-card>
      <span slot="header">{{ title }}</span>

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
              <a href="#" class="btn btn-danger btn-xs" @click.prevent="remove(item.id)"><i
                class="fa fa-trash-o"></i> Delete </a>
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
