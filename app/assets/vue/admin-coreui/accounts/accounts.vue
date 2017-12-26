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
          <th>Rolle</th>
          <th>Organisation</th>
          <th></th>
        </tr>

        <tbody>
          <tr v-for="item in list" :key="item.id" @click="open(item.id)">
            <td>{{ item.id }}</td>
            <td>
              <a>{{ item.name }}</a>
            </td>
            <td>
              {{ item.role }}
            </td>
            <td>
              {{ item.organisationId }}
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
  import Overview from '../mixins/overview'
  import { AdminAccounts } from '../../common/services/accounts'

  export default {
    name: 'accounts',

    data () {
      return {
        route: 'account',
        service: AdminAccounts,
        title: 'Alle Accounts'
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

