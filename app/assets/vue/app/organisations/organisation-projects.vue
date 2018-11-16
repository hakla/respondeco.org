<template>
  <div>
    <div class="col-lg-12 add-project" v-if="activeUserIsOwner">
      <router-link :to="{ name: 'project', params: { id: 'new' }}">
        <unify-button class="btn u-btn-outline-teal g-font-weight-600 g-letter-spacing-0_5 g-brd-2 g-rounded-0--md">
          <respondeco-icon class="g-mr-8" icon="fal plus"></respondeco-icon>
          {{ $t('project.add') }}
        </unify-button>
      </router-link>
    </div>
    <div class="col-lg-12">
      <!-- Panel Body -->
      <div class="card-block u-info-v1-1 g-pa-0">
        <ul class="list-unstyled">
          <li @click="openProject(item.id)"
              class="media g-brd-around g-brd-gray-light-v4 g-brd-left-3 g-brd-blue-left rounded g-pa-20 g-mb-10"
              :key="index" v-for="(item, index) in allProjects">
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
    <!-- End Latest Projects Panel --></div>
</template>

<script>
  import { router } from 'app/router'
  import { mapActions, mapGetters } from 'vuex'
  import Utils from 'common/utils'

  export default {
    name: 'OrganisationProjects',

    computed: {
      ...mapGetters(['activeUser', 'allProjects', 'organisation']),

      activeUserIsOwner () {
        return !!(this.activeUser && this.organisation && (this.activeUser.role === 'Administrator' || this.activeUser.organisationId === this.organisation.id))
      },
    },

    methods: {
      ...Utils.methods(),
      ...mapActions(['current']),

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

  .add-project {
    margin-bottom: 32px;
    text-align: right;
  }
</style>
