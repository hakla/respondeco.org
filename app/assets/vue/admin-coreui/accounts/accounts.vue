<template>
  <admin-page>
    <admin-card>
      <span slot="header">{{ title }}</span>

      <admin-table>
        <tr>
          <th>#</th>
          <th>Name</th>
          <th>Rolle</th>
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
  import Accounts from 'common/services/accounts'
  import { ObjectNormaliser } from 'common/utils'
  import Overview from '../mixins/overview'

  export default {
    name: 'accounts',

    data () {
      return {
        service: Accounts,
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

