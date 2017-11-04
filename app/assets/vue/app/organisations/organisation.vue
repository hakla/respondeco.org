<template>
  <div class="wrapper">
    <youtube
      v-if="this.organisation.video"
      :video-id="this.youtubeId(this.organisation)"
      player-height="400px"
      player-width="100%"
      :player-vars="this.playerVars"></youtube>
    <div v-if="!this.organisation.video" class="breadcrumbs-v3 text-center" :style="backgroundImage(organisation.image)">
    </div>
    <div class="service-block-v4">
      <div class="container">
        <div class="row">
          <div class="col-xs-12">
            <div class="logo" :style="backgroundImage(organisation.logo, expanded)"></div>
            <h1>{{ organisation.name }}</h1>
            <!--<i :class="className()" @click="expand()"></i>-->
          </div>
        </div>
      </div>
    </div>
    <div class="container">
      <div class="row">
        <div class="col-xs-12">
          <ul class="timeline-v1">
            <li v-for="entry in timeline">
              <div class="timeline-badge primary"><i class="glyphicon glyphicon-record"></i></div>
              <div class="timeline-panel">
                <div class="timeline-heading">
                  <img class="img-responsive" :src="imageUrl(entry.image)" alt=""/>
                </div>
                <div class="timeline-body text-justify">
                  <h2><a href="#">{{ entry.title }}</a></h2>
                  <p>
                    {{ entry.content }}
                  </p>
                  <a :href="entry.href" class="btn-u btn-u-sm">Mehr Infos</a>
                </div>
                <div class="timeline-footer">
                  <ul class="list-unstyled list-inline blog-info">
                    <li><i class="fa fa-clock-o"></i> {{ entry.date | formatDate }}</li>
                    <li><i class="fa fa-comments-o"></i> <a href="#">{{ entry.comments }} Kommentare</a></li>
                  </ul>
                  <a class="likes" href="#"><i class="fa fa-heart"></i>{{ entry.likes }}</a>
                </div>
              </div>
            </li>
            <li class="clearfix" style="float: none;"></li>
          </ul>
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

  import 'unify/css/pages/shortcode_timeline1.css';

  export default {
    name: 'organisation',

    components: {
      RespondecoBreadcrumbs
    },

    created () {
      this.organisation = {}
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
        playerVars: {
          autoplay: 0,
          controls: 1,
          iv_load_policy: 3,
          modestbranding: 1,
          rel: 0,
          showinfo: 0
        },
        organisation: this.organisation,
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

        this.resource.get({
          id: this.$route.params.id
        }).then(response => {
          this.organisation = response.body
          this.loading = false
        })

        this
          .$http
          .get(`organisations/${this.$route.params.id}/projects/finished`)
          .then(response => {
            this.timeline = this.timeline.concat(
              response
                .data
                .map(_ => ({
                  comments: _.comments,
                  content: _.project.description,
                  date: _.date,
                  href: this.$router.resolve({ name: 'finishedProject', params: { id: _.id } }).href,
                  image: _.project.image,
                  likes: 198,
                  title: _.project.name
                }))
            )
          })
      },

      imageUrl: Utils.imageUrl,

      youtubeId (organisation) {
        return getIdFromURL(organisation.video)
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
</style>
