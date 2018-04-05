<template>
  <section class="main g-min-height-50vh g-pb-100">
    <transition name="fade" mode="out-in">
      <div v-if="isntLoading('project')">
        <!-- Hero Info #01 -->
        <unify-hero type="1">
          {{ item.name }}
        </unify-hero>

        <div class="container">
          <div class="row">
            <div class="col-lg-9">
              <!-- new entry -->
              <project-comment @change="loadComments" :project-id="item.id" v-if="activeUserIsOwner"></project-comment>

              <!-- list of comments -->
              <project-comment @change="loadComments" @pinned="loadComments" @removed="loadComments"
                               @unpinned="loadComments" :editable="activeUserIsOwner" :project-id="item.id"
                               :value="comment" v-for="comment in comments" :key="comment.id"></project-comment>
            </div>

            <div class="col-lg-3 g-brd-left--lg g-brd-gray-light-v4 g-mb-80">
              <div class="g-pl-20--lg">
                <!-- Description -->
                <div class="g-mb-50">
                  <h5 class="mb-4">{{ $t('project.description.title') }}</h5>
                  <div v-if="activeUserIsOwner" class="text-right">
                    <textarea v-autosize class="form-control" v-model="item.description"></textarea>

                    <unify-button
                      @click="save"
                      loading="saveDescription"
                      class="btn u-btn-outline-teal g-font-weight-600 g-letter-spacing-0_5 g-brd-2 g-rounded-0--md g-mr-10 g-mt-20">
                      <span>{{ $t('common.save') }}</span>
                    </unify-button>
                  </div>
                  <span v-else>
                    {{ item.description }}
                  </span>
                </div>

                <!-- Sticky block -->
                <div id="sticky-block" v-stickyblock data-start-point="#sticky-block" data-end-point="footer" class="sidebar--stickyblock">
                  <!-- Share -->
                  <div class="g-mb-50">
                    <h5 class="mb-4">{{ $t('project.share') }} </h5>
                    <div>
                      <a class="u-icon-v3 g-bg-facebook g-color-white g-color-white--hover g-mr-20 g-mb-20" href="#!">
                        <i class="icon-social-facebook"></i>
                      </a>
                      <a class="u-icon-v3 g-bg-linkedin g-color-white g-color-white--hover g-mr-20 g-mb-20" href="#!">
                        <i class="icon-social-linkedin"></i>
                      </a>
                      <a class="u-icon-v3 g-bg-twitter g-color-white g-color-white--hover g-mr-20 g-mb-20" href="#!">
                        <i class="icon-social-twitter"></i>
                      </a>
                    </div>
                  </div>
                </div>

                <div class="g-mt-50">
                  <h5 class="mb-4">{{ $t('project.partners') }}</h5>

                  <ul class="list-unstyled g-font-size-13 mb-0">
                    <li v-for="partner in partners" :key="partner.id">
                      <article class="media g-mb-35">
                        <img class="d-flex g-width-40 g-height-40 rounded-circle mr-3" :src="imageUrl(partner.image)"
                             alt="Image Description">
                        <div class="media-body">
                          <h4 class="g-mt-8 h6 g-color-black g-font-weight-600">{{ partner.name }}</h4>
                        </div>
                      </article>
                    </li>
                  </ul>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <spinner class="g-mb-100 g-mt-100" v-else></spinner>
    </transition>
  </section>
</template>

<script>
  import { mapGetters } from 'vuex'
  import { ImageMixin, Notifications, ObjectNormaliser } from '../../common/utils'
  import Comments from '../../common/services/comments'
  import ImageDialog from '../main/image-dialog'
  import Projects from '../../common/services/projects'
  import ItemPage from '../mixins/item-page'
  import DateFilter from '../../common/mixins/date-filter'

  import ProjectComment from './comment'
  import FinishedProjects from "../../common/services/finished-projects";

  export default {
    name: 'project',

    components: {
      ImageDialog,
      ProjectComment
    },

    computed: {
      ...mapGetters(['activeUser']),

      activeUserIsOwner () {
        return !!(this.activeUser && this.item && (this.activeUser.role === 'Administrator' || this.activeUser.organisationId === this.item.organisation.id))
      }
    },

    created () {
      this.fetchData()
      this.loadComments()
      this.loadPartners()
    },

    data () {
      return {
        comments: [],
        item: ObjectNormaliser.project(),
        loader: ['project'],
        partners: [],
        service: Projects
      }
    },

    methods: {
      loadComments () {
        this.promiseLoading(
          Comments.byProject(this.id).then(result => {
            this.comments = result.body
          }),

          'project'
        )
      },

      loadPartners() {
        this.promiseLoading(
          FinishedProjects.byProject(this.id).then(result => {
            this.partners = result.body.map(_ => _.organisation)
          }),

          'partners'
        )
      },

      save () {
        let project = this.item

        project.organisation = project.organisation.id

        this.promiseLoading(
          Projects.update(project).then(
            Notifications.success(this),
            Notifications.error(this)
          ),

          'saveDescription'
        )
      }
    },

    mixins: [DateFilter, ImageMixin, ItemPage]
  }
</script>

<style lang="stylus" scoped>
  .sidebar--stickyblock
    padding-top: 30px
    background: #fff
    z-index: 1

  .form-control
    border-color: transparent
    border-radius: 0
    margin: -8px
    padding: 8px
    transition: .2s ease border-color

    &:focus
      border-color: #eee
</style>
