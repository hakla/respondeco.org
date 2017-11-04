<template>
  <div>
    <respondeco-breadcrumbs :heading="entry.project.name" background=""></respondeco-breadcrumbs>

    <div class="bg-color-light">
      <div class="container content-sm">
        <div class="news-v3 bg-color-white margin-bottom-60">
          <div class="news-v3-in">
            <ul class="list-inline posted-info">
              <li>Ein Projekt von <strong><a href="#">{{ entry.project.organisation.name }}</a></strong></li>
              <li><strong><a href="#">{{ entry.organisation.name }}</a></strong> hat sich daran beteiligt!</li>
              <li>Stattgefunden am: {{ entry.date | formatDate }}</li>
            </ul>
          </div>
        </div>

        <hr v-if="comments.length > 0">

        <div class="news-v3 bg-color-white margin-bottom-60" v-for="comment in comments">
          <img class="img-responsive full-width" :src="imageUrl(comment.image)" alt="" v-if="comment.image">
          <youtube
            v-if="comment.video"
            :video-id="youtubeId(comment.video)"
            player-height="400px"
            player-width="100%"
            :player-vars="playerVars"></youtube>
          <div class="news-v3-in">
            <h2><a href="#">{{ comment.title }}</a></h2>
            <p>{{ comment.content }}</p>
          </div>
        </div>

        <hr>

        <h2>Kommentieren</h2>

        <form @submit.prevent="submit" method="post" id="sky-form3" class="sky-form comment-style" novalidate>
          <fieldset>
            <div class="sky-space-30">
              <input v-model="comment.title" class="form-control" placeholder="Titel">
            </div>
            <div class="sky-space-30">
              <div>
                <textarea v-model="comment.content" rows="8" name="message" id="message" placeholder="Write comment here ..." class="form-control"></textarea>
              </div>
            </div>
            <div class="sky-space-30">
              <input v-model="comment.video" class="form-control" placeholder="Youtube Video">
            </div>

            <dropzone id="dropzone_logo"
                      ref="dropzone_logo"
                      @vdropzone-file-added="clearImage"
                      @vdropzone-success="uploadedImage"
                      @vdropzone-sending="transformRequest"
                      :url="Config.ImageBaseUrl"
                      :use-custom-dropzone-options="true"
                      :dropzone-options="Config.DropzoneOptions">
            </dropzone>

            <p>
              <button class="btn-u">Submit</button>
            </p>
          </fieldset>

          <div class="message">
            <i class="rounded-x fa fa-check"></i>
            <p>Your comment was successfully posted!</p>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
  import Dropzone from 'vue2-dropzone'
  import { getIdFromURL } from 'vue-youtube-embed'

  import Config from 'common/config'
  import RespondecoBreadcrumbs from 'app/main/breadcrumbs'
  import TokenHolder from 'common/token-holder'
  import Utils from 'common/utils'

  import Authentication from 'common/authentication'

  export default {

    name: 'FinishedProject',

    components: {
      Dropzone,
      RespondecoBreadcrumbs
    },

    created() {
      this.commentsResource = this.$resource('comments{/id}')

      Authentication.activeUser().then((user) => {
        this.comment.author = user.id
      })

      if (this.$route.params.id !== undefined) {
        this.fetchData()
      }
    },

    data() {
      return {
        Config,
        comment: {
          author: undefined,
          title: undefined,
          content: undefined,
          video: undefined,
          image: undefined
        },
        comments: [],
        entry: {
          date: '1990-01-01',
          organisation: {},
          project: {
            organisation: {}
          }
        },
        playerVars: {
          autoplay: 0,
          controls: 1,
          iv_load_policy: 3,
          modestbranding: 1,
          rel: 0,
          showinfo: 0
        }
      }
    },

    methods: {

      clearImage() {
        if (this.comment.image !== undefined) {
          this.comment.image = undefined
          this.$refs[`dropzone_logo`].getAcceptedFiles().forEach(file => this.$refs['dropzone_logo'].removeFile(file))
        }
      },

      fetchData() {
        this
          .$http
          .get(`finishedProjects/${this.$route.params.id}?public`)
          .then(response => {
            this.entry = response.data
          })

        this
          .$http
          .get(`finishedProjects/${this.$route.params.id}/comments`)
          .then(response => {
            this.comments = response.data
          })
      },

      imageUrl: Utils.imageUrl,

      uploadedImage(_, response) {
        this.comment.image = response
      },

      transformRequest(_, xhr) {
        TokenHolder.get(token => xhr.setRequestHeader('X-Access-Token', token))
      },

      submit() {
        this.comment.project_history = this.entry.project.id

        this.commentsResource.save(this.comment)
      },

      youtubeId (video) {
        return getIdFromURL(video)
      }

    }

  }
</script>

<style scoped lang="stylus">
</style>
