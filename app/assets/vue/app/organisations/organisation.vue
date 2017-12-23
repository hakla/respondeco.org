<template>
  <section class="main g-min-height-50vh">
    <vue-simple-spinner class="g-mb-100 g-mt-100" v-if="loading"></vue-simple-spinner>

    <!-- Hero Info #01 -->
    <section class="text-center g-pt-100--md g-pb-100--md g-pt-50 g-pb-30" v-if="!loading" ref="hero">
      <div class="container">
        <div class="u-heading-v2-5--bottom g-brd-primary">
          <h2 class="u-heading-v2__title text-uppercase g-font-weight-300 mb-0">{{ organisation.name }}</h2>
        </div>
      </div>
    </section>
    <!-- End Hero Info #01 -->

    <div class="container" v-if="!loading">
      <div class="row">
        <!-- Profile Sidebar -->
        <div class="col-lg-3">
          <!-- User Image -->
          <div class="u-block-hover g-pos-rel">
            <figure class="g-mb-10">
              <img class="img-fluid w-100" :src="imageUrl(organisation.logo)" alt="Image Description">
            </figure>
          </div>
          <!-- User Image -->

          <!-- Sidebar Navigation -->
          <div class="list-group list-group-border-0 g-mb-40">
            <!-- Profile -->
            <router-link :to="{ name: 'organisation-about', params: { id: organisation.id } }" class="list-group-item list-group-item-action justify-content-between">
              <span><i class="icon-cursor g-pos-rel g-top-1 g-mr-8"></i> {{ $tc('common.about_organisation') }}</span>
            </router-link>
            <!-- End Profile -->

            <!-- My Projects -->
            <router-link :to="{ name: 'organisation-projects', params: { id: organisation.id } }" class="list-group-item list-group-item-action justify-content-between">
              <span><i class="icon-layers g-pos-rel g-top-1 g-mr-8"></i> {{ $tc('common.projects') }}</span>
              <span class="u-label g-font-size-11 g-bg-primary g-rounded-20 g-px-10">{{ projects.length }}</span>
            </router-link>
            <!-- End My Projects -->

            <!-- Comments -->
            <router-link :to="{ name: 'organisation-comments', params: { id: organisation.id } }" class="list-group-item list-group-item-action justify-content-between">
              <span><i class="icon-bubbles g-pos-rel g-top-1 g-mr-8"></i> {{ $tc('common.comments') }}</span>
              <span class="u-label g-font-size-11 g-bg-pink g-rounded-20 g-px-8">24</span>
            </router-link>
            <!-- End Comments -->

            <!-- Reviews -->
            <router-link :to="{ name: 'organisation-ratings', params: { id: organisation.id } }" class="list-group-item list-group-item-action justify-content-between">
              <span><i class="icon-heart g-pos-rel g-top-1 g-mr-8"></i> {{ $tc('common.ratings') }}</span>
            </router-link>
            <!-- End Reviews -->

            <!-- Settings -->
            <router-link :to="{ name: 'organisation-settings-profile', params: { id: organisation.id } }" v-if="activeUserIsOwner" active-class="active" class="list-group-item list-group-item-action justify-content-between">
              <span><i class="icon-settings g-pos-rel g-top-1 g-mr-8"></i> {{ $tc('common.settings') }}</span>
            </router-link>
            <!-- End Settings -->
          </div>
          <!-- End Sidebar Navigation -->
        </div>
        <!-- End Profile Sidebar -->

        <!-- Profile Content -->
        <div class="col-lg-9">
          <router-view></router-view>
        </div>
        <!-- End Profle Content -->
      </div>
    </div>
  </section>
</template>

<script>
  import Utils from 'common/utils'
  import RespondecoBreadcrumbs from 'app/main/breadcrumbs'
  import { getIdFromURL } from 'vue-youtube-embed'
  import { mapGetters, mapActions } from 'vuex'
  import VueSimpleSpinner from 'vue-simple-spinner'

  export default {
    name: 'organisation',

    components: {
      RespondecoBreadcrumbs,
      VueSimpleSpinner
    },

    computed: {
      ...mapGetters(['organisation', 'projects', 'activeUser']),

      activeUserIsOwner () {
        return this.activeUser && this.organisation && this.activeUser.organisationId === this.organisation.id
      }
    },

    created () {
      this.resource = this.$resource('organisations{/id}')

      if (this.$route.params.id !== 'new') {
        this.fetchData()
      } else {
        this.isNew = true
      }
    },

    data () {
      return {
        expanded: false,
        loading: false,
        playerVars: {
          autoplay: 0,
          controls: 1,
          iv_load_policy: 3,
          modestbranding: 1,
          rel: 0,
          showinfo: 0
        },
        timeline: []
      }
    },

    methods: {
      backgroundImage: Utils.backgroundImage,

      bannerImage(image) {
        return Utils.backgroundImage(image) + `; height: ${this.expanded ? "400px" : "800px"};`
      },

      className() {
        return this.expanded ? "fa fa-compress" : "fa fa-expand"
      },

      expand() {
        this.expanded = !this.expanded
      },

      fetchData() {
        this.loading = true

        let actions = mapActions(['finishedProjects', 'projects'])

        this.resource.get({
          id: this.$route.params.id
        }).then(response => {
          this.current(response.body)
          this.loading = false
        })

        this
          .$http
          .get(`organisations/${this.$route.params.id}/projects/finished`)
          .then(response => {
            actions.finishedProjects.bind(this)(response.body)
          })

        this
          .$http
          .get(`organisations/${this.$route.params.id}/projects`)
          .then(response => {
            actions.projects.bind(this)(response.body)
          })
      },

      imageUrl: Utils.imageUrl,

      youtubeId (organisation) {
        return getIdFromURL(organisation.video)
      },

      ...mapActions({
        current: 'current'
      })
    },

    watch: {
      '$route.params.id' (newValue, oldValue) {
        if (newValue !== oldValue) {
          this.fetchData()
        }
      }
    }
  }
</script>

<style lang="stylus" scoped>
  .wrapper > div:first-child
    height: 400px

  .breadcrumbs-v3 {
    background-size: cover;
    background-position-y: 50%;

    height: 800px;
  }

  iframe {
    margin-bottom: 0
  }

  .service-block-v4
    position: relative
    padding: 20px

    .logo
      height: 200px
      width: 200px

      position: absolute;
      left: -50px
      top -400px
      z-index: 100

      border-radius: 100px;
      padding: 10px

      background-color: rgba(255, 255, 255, .5);
      background-repeat: no-repeat;
      background-size: 80%;
      background-position: 50%;

      + h1
        position: relative
        z-index: 101

  .main
    margin-bottom: 200px
</style>
