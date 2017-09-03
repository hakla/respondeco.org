<template>
  <div class="wrapper">
    <youtube
      v-if="this.project.video"
      :video-id="this.youtubeId(this.project)"
      player-height="400px"
      player-width="100%"
      :player-vars="this.playerVars"></youtube>
    <div v-if="!this.project.video" class="breadcrumbs-v3 text-center" :style="backgroundImage(project.image)">
    </div>
    <div class="service-block-v4">
      <div class="container">
        <div class="row">
          <div class="col-xs-12">
            <h1>{{ project.name }}</h1>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import { getIdFromURL } from 'vue-youtube-embed'
  import Config from 'common/config'
  import Utils from 'common/utils'
  import RespondecoBreadcrumbs from 'app/main/breadcrumbs'
  import {
    router
  } from '../router'

  export default {
    name: 'project',

    components: {
      RespondecoBreadcrumbs
    },

    created () {
      this.project = {}
      this.resource = this.$resource('projects{/id}')

      if (this.$route.params.id !== 'new') {
        this.fetchData()
      } else {
        this.isNew = true
      }
    },

    data () {
      return {
        playerVars: {
          autoplay: 0,
          controls: 1,
          iv_load_policy: 3,
          modestbranding: 1,
          rel: 0,
          showinfo: 0
        },
        project: this.project
      }
    },

    methods: {
      backgroundImage: Utils.backgroundImage,

      fetchData() {
        this.loading = true

        this.resource.get({
          id: this.$route.params.id
        }).then(response => {
          this.project = response.body
          this.loading = false
        })
      },

      youtubeId (project) {
        return getIdFromURL(project.video)
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

    height: 400px;
  }

  iframe {
    margin-bottom: 0
  }
</style>
