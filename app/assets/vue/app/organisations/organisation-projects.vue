<template>
  <div class="col-lg-12">
    <!-- Panel Body -->
    <div class="card-block u-info-v1-1 g-pa-0">
      <ul class="list-unstyled">
        <li @click="openProject(item.id)" class="media g-brd-around g-brd-gray-light-v4 g-brd-left-3 g-brd-blue-left rounded g-pa-20 g-mb-10" :key="index" v-for="(item, index) in projects">
          <div class="d-flex g-mt-2 g-mr-15">
            <img class="g-width-40 g-height-40 rounded-circle" :src="imageUrl(item.image)">
          </div>
          <div class="media-body">
            <div class="d-flex justify-content-between">
              <h5 class="h6 g-font-weight-600 g-color-black">{{ item.name }}</h5>
            </div>
            <p>{{ item.description }}</p>
          </div>
        </li>
      </ul>
    </div>
    <!-- End Panel Body -->
  </div>
  <!-- End Latest Projects Panel -->
</template>

<script>
  import { router } from 'app/router'
  import { mapGetters } from 'vuex'
  import Utils from 'common/utils'

  export default {
    name: 'OrganisationProjects',

    computed: {
      ...mapGetters(['projects'])
    },

    methods: {
      ...Utils.methods(),

      openProject (id) {
        router.push(`/projects/${id}`)
      }
    }
  }

</script>

<style scoped>
  li {
    cursor: pointer;
  }
</style>
