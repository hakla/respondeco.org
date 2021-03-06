<template>
  <section class="main g-min-height-50vh g-pb-100">
    <transition name="fade" mode="out-in">
      <div v-if="isntLoading('project')">
        <respondeco-unsaved-bar @cancel="cancel" @save="save" :visible="unsaved"></respondeco-unsaved-bar>

        <!-- Hero Info #01 -->
        <unify-hero type="1">
          <div v-if="activeUserIsOwner" class="text-right">
            <textarea class="form-control textarea-title" :placeholder="$t('project.placeholder.name')" v-autosize
                      v-model="item.name" @input="unsaved = true"></textarea>
          </div>
          <span v-else>
            {{ item.name }}
          </span>
        </unify-hero>

        <file-dialog :file="imageUrl(item.image)" fileType="image/png" @error="uploadError"
                     @uploaded="uploaded"></file-dialog>

        <div class="container">
          <div class="row">
            <div class="col-lg-9">
              <!-- new entry -->
              <project-comment @change="loadComments" :project-id="item.id"
                               v-if="activeUserIsOwner || userIsAllowedToWriteComments"></project-comment>

              <!-- list of comments -->
              <project-comment @change="loadComments" @pinned="loadComments" @removed="loadComments"
                               @unpinned="loadComments" :editable="activeUserIsOwner" :project-id="item.id"
                               :value="comment" v-for="comment in comments" :key="comment.id"></project-comment>
            </div>

            <div class="col-lg-3 g-brd-left--lg g-brd-gray-light-v4 g-mb-80">
              <div class="g-pl-20--lg g-mb-20">
                <div class="u-block-hover g-cursor-pointer g-mb-25 g-pos-rel" v-if="activeUserIsOwner">
                  <figure>
                    <img class="d-flex g-max-width-100x mr-3" :src="imageUrl(item.image)"
                         alt="Image Description" v-if="item.image !== undefined">
                    <img src="/assets/images/demo-square.jpg" width="100%" alt="Placeholder image" v-else>
                  </figure>
                  <figcaption class="u-block-hover__additional--fade g-bg-black-opacity-0_5 g-pa-30"
                              @click.prevent="openImageDialog">
                    <div data-v-478dddb2=""
                         class="u-block-hover__additional--fade u-block-hover__additional--fade-up g-flex-middle">
                      <ul class="list-inline text-center g-flex-middle-item--bottom g-mb-20">
                        <li class="list-inline-item align-middle g-mx-7">
                          <a class="u-icon-v1 u-icon-size--md g-color-white" href="#!">
                            <i class="icon-note u-line-icon-pro"></i>
                          </a>
                        </li>
                      </ul>
                    </div>
                  </figcaption>
                </div>
                <img class="d-flex g-max-width-100x mr-3" :src="imageUrl(item.image)"
                     alt="Image Description" v-else-if="item.image !== undefined">
              </div>
              <div class="g-pl-20--lg">
                <!-- Organisation -->
                <h5>Ein Projekt von</h5>
                <ul class="list-unstyled g-font-size-13 mb-0" v-if="item.organisation != null">
                  <li>
                    <router-link :to="`/organisations/${item.organisation.id}`" class="media g-mb-35">
                      <img class="d-flex g-width-40 g-height-40 rounded-circle mr-3"
                           :src="imageUrl(item.organisation.logo)"
                           alt="Image Description"
                           v-if="item.organisation.logo">
                      <div class="media-body">
                        <h4 class="g-mt-8 h6 g-color-black g-font-weight-600">{{ item.organisation.name }}</h4>
                      </div>
                    </router-link>
                  </li>
                </ul>

                <!-- Description -->
                <div class="g-mb-50">
                  <h5 class="mb-4">{{ $t('project.description.title') }}</h5>
                  <div v-if="activeUserIsOwner" class="text-right">
                    <textarea class="form-control" :placeholder="$t('project.placeholder.description')" v-autosize
                              v-model="item.description"
                              @input="unsaved = true"></textarea>
                  </div>
                  <span v-else>
                    {{ item.description }}
                  </span>
                </div>

                <div class="g-mt-50">
                  <h5 class="mb-4">{{ $t('project.partner.listTitle') }}</h5>

                  <ul class="list-unstyled g-font-size-13 mb-0 partner-list">
                    <li v-for="partner in partners" :key="partner.id">
                      <router-link :to="`/organisations/${partner.organisation.id}`" class="media g-mb-35">
                        <img class="d-flex g-width-40 g-height-40 rounded-circle mr-3"
                             :src="imageUrl(partner.organisation.logo)"
                             alt="Image Description" v-if="partner.organisation.logo">
                        <div class="media-body">
                          <h4 class="g-mt-8 h6 g-color-black g-font-weight-600">{{ partner.organisation.name }}</h4>
                        </div>
                      </router-link>
                      <div class="remove-partner" @click="removePartner(partner.id)" v-tooltip
                           :title="$t('project.partner.remove')" data-placement="top" :ref="`removeIcon-${partner.id}`"
                           v-if="activeUserIsOwner">
                        <respondeco-icon icon="fal times"></respondeco-icon>
                      </div>
                    </li>
                  </ul>

                  <div class="text-right">
                    <project-partner-chooser @submit="addPartner" v-if="activeUserIsOwner"></project-partner-chooser>
                  </div>
                </div>

                <!-- Sticky block -->
                <div id="sticky-block" v-stickyblock data-start-point="#sticky-block" data-end-point="footer"
                     class="sidebar--stickyblock">
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
  import { mapActions, mapGetters } from 'vuex'
  import { ImageMixin, Notifications, ObjectNormaliser } from '../../common/utils'
  import Comments from '../../common/services/comments'
  import DateFilter from '../../common/mixins/date-filter'
  import FileDialog from '../main/file-dialog'
  import ImageDialog from '../main/image-dialog'
  import ItemPage from '../mixins/item-page'
  import Multiselect from 'vue-multiselect'
  import Organisations from '../../common/services/organisations'
  import ProjectPartnerChooser from './project-partner-chooser'
  import Projects from '../../common/services/projects'
  import RespondecoUnsavedBar from '../main/unsaved-bar'

  import ProjectComment from './comment'
  import FinishedProjects from '../../common/services/finished-projects'

  import bPopover from 'bootstrap-vue/es/components/popover/popover'

  export default {
    name: 'project',

    components: {
      'b-popover': bPopover,
      FileDialog,
      ImageDialog,
      Multiselect,
      ProjectComment,
      ProjectPartnerChooser,
      RespondecoUnsavedBar
    },

    computed: {
      ...mapGetters(['activeUser', 'organisation']),

      activeUserIsOwner () {
        return !!(this.activeUser && this.item && (this.activeUser.role === 'Administrator' || this.activeUser.organisationId === this.item.organisation.id))
      },

      userIsAllowedToWriteComments () {
        const isActiveUser = this.activeUser !== undefined
        const isPartner = (partner) => partner.organisation.id === this.activeUser.organisationId

        return isActiveUser && this.partners.filter(isPartner).length > 0
      }
    },

    created () {
      if (this.id !== 'new') {
        this.fetchData()
        this.loadComments()
        this.loadPartners()
      } else {
        if (this.organisation.id !== 'empty') {
          // User got here by clicking on organisation -> new project
          this.item.organisation = this.organisation
        }
      }
    },

    data () {
      return {
        comments: [],
        item: ObjectNormaliser.project(),
        loader: ['project'],
        partner: {
          id: -1
        },
        partners: [],
        service: Projects
      }
    },

    methods: {
      ...mapActions(['current']),

      addPartner (organisationId) {
        const finishedProject = ObjectNormaliser.finishedProject({
          organisation: organisationId,
          project: this.item.id
        })

        this.promiseLoading(
          FinishedProjects.save(finishedProject).then(
            () => {
              Notifications.success(this)

              this.loadPartners()
            },
            Notifications.error(this)
          ),
          'partner-chooser'
        )
      },

      loadComments () {
        this.promiseLoading(
          Comments.byProject(this.id).then(result => {
            this.comments = result.body
          }),

          'project'
        )
      },

      loadPartners () {
        this.promiseLoading(
          FinishedProjects.byProject(this.id).then(result => {
            this.partners = result.body
          }),

          'partners'
        )
      },

      openImageDialog () {
        this.$modal.show('file-dialog')
      },

      removePartner (partnerId) {
        $(this.$refs[`removeIcon-${partnerId}`]).tooltip('hide')

        FinishedProjects.remove(partnerId).then(result => {
          this.partners = this.partners.filter(partner => partner.id !== partnerId)
        })
      },

      save () {
        let method = 'update'
        let project = Object.assign({}, this.item)

        if (this.id === 'new') {
          method = 'save'
        }

        project.organisation = project.organisation.id

        this.promiseLoading(
          Projects[method](project).then(
            (result) => {
              this.unsaved = false
              Notifications.success(this)(result)

              this.$router.push({name: 'project', params: {id: result.body.id}})
            },
            Notifications.error(this)
          ),

          'saveDescription'
        )
      },

      uploadError (error) {
        let message = this.$t('common.error.upload')

        if (error === 'denied') {
          message = this.$t('common.error.unauthenticated')
        }

        Notifications.error(this, message)()
      },

      uploaded (file) {
        this.item = Object.assign({}, this.item, {
          image: file
        })

        this.$modal.hide('file-dialog')
        this.unsaved = true
      }
    },

    watch: {
      activeUser (activeUser) {
        // wait for the activeUser to change (undefined on refresh)
        Organisations.byId(activeUser.organisationId).then(
            result => {
              this.current(result.body)
              this.item.organisation = result.body
            }
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

  .partner-list
    li
      position relative

  .remove-partner
    background: #fff;
    position: absolute;
    right: 0;
    top: 0;
    transform: translateX(100%);
    height: 100%;
    display: flex;
    align-items: center;
    padding: 6px;
    cursor: pointer;

  .textarea-title
    font-size: 28px
    height: 60px
    text-align: center

</style>
