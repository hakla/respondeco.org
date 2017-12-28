<template>
  <admin-page>
    <admin-card>
      <div class="d-flex" slot="header">
        <span class="flex-grow-1">Kommentare</span>
        <router-link :to="childRoute('new')" class="btn btn-primary btn-sm" tag="button">Neuer Kommentar</router-link>
      </div>

      <admin-table>
        <tr>
          <th>#</th>
          <th>Autor</th>
          <th>Titel</th>
          <th></th>
        </tr>

        <tbody>
          <tr v-for="item in list" :key="item.id" @click="open(item.id)">
            <td>{{ item.id }}</td>
            <td>
              <a>{{ item.author.data.organisation ? 'Organisation' : 'Benutzer' }}: {{ item.author.data.name }}</a>
            </td>
            <td>
              {{ item.title }}
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
  import Comments from '../../common/services/comments'
  import Overview from '../mixins/overview'

  export default {
    name: 'comments',

    computed: {
      commentType () {
        let type = 'project'

        if (this.type) {
          type = this.type
        }

        // transform "project" to "byProject"
        type = 'by' + type.substring(0, 1).toUpperCase() + type.substring(1)

        return type
      }
    },

    data: function () {
      return {
        loader: ['comments'],
        service: Comments,
        title: 'Kommentare'
      }
    },

    methods: {
      childRoute (id) {
        return {
          name: 'comment',
          params: {
            id,
            type: this.type,
            typeId: this.id || 'new'
          }
        }
      },

      fetchData () {
        this.promiseLoading(
          Comments[this.commentType](this.id).then(result => {
            this.list = result.body
          })
        )
      },

      open (id) {
        this.$router.push(
          this.childRoute(id)
        )
      }
    },

    mixins: [Overview],

    props: ['id', 'type']
  }
</script>

<style scoped>
  tbody tr {
    cursor: pointer;
  }
</style>
