<template>
  <form @submit.prevent.stop="save">

    <!-- editable view -->
    <article class="g-mb-100 position-relative" v-if="editable || activeUserOwnsComment">
      <i class="g-cursor-pointer position-absolute g-top-minus-30 g-right-30 g-font-size-20"
         @click="pin()" v-if="editable && comment.id != null && !comment.pinned"
         v-tooltip data-placement="top" :title="$t('project.comment.pin')" ref="pinIcon"
      >
        <respondeco-icon icon="far thumbtack"></respondeco-icon>
      </i>
      <i class="g-cursor-pointer position-absolute g-top-minus-30 g-right-30 g-font-size-20"
         @click="unpin()" v-if="editable && comment.id != null && comment.pinned"
         v-tooltip data-placement="top" :title="$t('project.comment.unpin')" ref="pinIcon"
      >
        <respondeco-icon icon="fas thumbtack"></respondeco-icon>
      </i>

      <i class="g-cursor-pointer position-absolute g-top-minus-30 g-right-0 g-font-size-20"
         @click="deleteComment(comment.id)" v-if="editable && comment.id != null"
         v-tooltip data-placement="top" title="Kommentar löschen" ref="removeIcon"
      >
        <respondeco-icon icon="fal times"></respondeco-icon>
      </i>

      <image-dialog :aspect-ratio="Number.NaN" :file="comment ? comment.image : undefined" :name="dialogName"
                    :viewMode="1"
                    @changed="setNewImage"></image-dialog>

      <transition name="fade" mode="out-in">
        <div class="u-block-hover g-cursor-pointer g-mb-25 g-pos-rel g-rounded-4" @click="openFileChooser" v-if="previewImageUrl">
          <figure>
            <img class="img-fluid w-100 g-rounded-5 g-z-index-1" :src="previewImageUrl"
                 alt="Image Description">
          </figure>
          <figcaption class="u-block-hover__additional--fade g-bg-black-opacity-0_5 g-pa-30">
            <div class="u-block-hover__additional--fade u-block-hover__additional--fade-up g-flex-middle g-flex-centered">
              <ul class="list-inline text-center g-flex-middle-item--bottom g-mb-40">
                <li class="list-inline-item align-middle g-mx-7">
                  <a class="u-icon-v1 u-icon-size--md g-color-white" href="#!" @click.prevent="openImageDialog">
                    <i class="icon-note u-line-icon-pro"></i>
                  </a>
                </li>
              </ul>
            </div>
          </figcaption>
        </div>

        <youtube :video-id="comment.video" v-else-if="comment.video" player-width="100%"></youtube>

        <div
          class="d-flex align-items-center g-cursor-pointer justify-content-center image-placeholder img-fluid w-100 g-rounded-5 g-mb-25 g-height-200 g-brd-1 g-brd-teal g-brd-style-solid"
          @click="openFileChooser" v-else>
          {{ $t('project.new.chooseImage') }}
        </div>
      </transition>

      <div class="px-4 position-relative">
        <ul
          class="d-flex justify-content-start align-items-end list-inline g-color-gray-dark-v5 g-font-size-13 g-mt-minus-65 g-mb-25">
          <li class="list-inline-item mr-5">
            <!-- new image -> show author image -->
            <img class="g-width-80 g-height-80 g-brd-around g-brd-2 g-brd-white rounded-circle mb-2"
                 :src="imageUrl(activeUser.organisationImage)" v-if="activeUser.organisationImage && !comment.author"
                 alt="Image Description">

            <!-- existing comment -> show comment author image -->
            <img class="g-width-80 g-height-80 g-brd-around g-brd-2 g-brd-white rounded-circle mb-2"
                 :src="imageUrl(comment.author.data.image)"
                 v-if="comment.author && comment.author.data && comment.author.data.image"
                 alt="Image Description">
            <span class="d-block g-width-40 g-height-40" v-else></span>
            <h4 class="h6 g-font-weight-600 g-font-size-13 mb-0 text-center">
              <span v-if="isNew">{{ activeUser.name }}</span>
              <span v-if="!isNew && comment.author">{{ comment.author.data.name }}</span>
            </h4>
          </li>
          <li class="list-inline-item">
            <span class="g-font-size-12">{{ new Date() | formatDate }}</span>
          </li>
        </ul>

        <h2 class="h5 g-color-black g-font-weight-600 article--heading">
          <input type="text" class="form-control" v-model="comment.title" :placeholder="$t('project.new.title')">
        </h2>
        <p class="g-color-gray-dark-v4 article--content">
        <textarea class="form-control" :placeholder="$t('project.new.content')" v-autosize
                  v-model="comment.content"></textarea>
        </p>

        <div class="form-group text-right">
          <unify-button
            type="submit"
            class="btn u-btn-outline-teal g-font-weight-600 g-letter-spacing-0_5 g-brd-2 g-rounded-0--md">
            <span v-if="comment.id">{{ $t('comment.save') }}</span>
            <span v-else>{{ $t('comment.add') }}</span>
          </unify-button>
        </div>
      </div>

      <hr class="g-my-100">
    </article>

    <!-- public view -->
    <article class="g-mb-100 position-relative" v-else>
      <img class="img-fluid w-100 g-rounded-5" :src="imageUrl(comment.image)" v-if="comment.image"
           alt="Image Description">

      <youtube :video-id="comment.video" v-if="!comment.image && comment.video" player-width="100%"></youtube>

      <div class="px-4">
        <ul
          class="d-flex justi6fy-content-start align-items-end list-inline g-color-gray-dark-v5 g-font-size-13 g-mt-minus-45 g-mb-25 g-pos-rel g-z-index-1">
          <li class="list-inline-item mr-5">
            <img class="g-width-80 g-height-80 g-brd-around g-brd-2 g-brd-white rounded-circle mb-2"
                 :src="imageUrl(comment.author.data.image)" v-if="comment.author.data.image"
                 alt="Image Description">
            <span class="d-block g-width-40 g-height-40" v-else></span>
            <h4 class="h6 g-font-weight-600 g-font-size-13 mb-0 text-center">
              <router-link :to="{ name: 'organisation-projects', params: { id: comment.author.data.id } }"
                           class="g-color-gray-dark-v4" v-if="comment.author.data.organisation">
                {{ comment.author.data.name }}
              </router-link>

              <router-link
                :to="{ name: 'organisation-projects', params: { id: comment.author.data.user.organisationId } }"
                class="g-color-gray-dark-v4" v-if="comment.author.data.user">
                {{ comment.author.data.name }}
              </router-link>
            </h4>
          </li>
          <li class="list-inline-item">
            <span class="g-font-size-12">{{ comment.date | formatDate }}</span>
          </li>
        </ul>

        <h2 class="h5 g-color-black g-font-weight-600">
          {{ comment.title }}
        </h2>
        <p class="g-color-gray-dark-v4 article--content">
          {{ comment.content }}
        </p>
      </div>
    </article>
  </form>
</template>

<script>
  import $ from 'jquery'
  import { mapGetters } from 'vuex'
  import { ImageMixin, Notifications, ObjectNormaliser } from '../../common/utils'
  import Comments from '../../common/services/comments'
  import ImageDialog from '../main/image-dialog'
  import Images from '../../common/services/images'
  import LoaderHelper from '../../common/mixins/loader-helper'

  export default {
    name: 'project-comment',

    components: {
      ImageDialog
    },

    computed: {
      ...mapGetters(['activeUser']),

      activeUserOwnsComment () {
        return this.comment.id && this.comment.author.data.user && this.activeUser && this.activeUser.organisationId === this.comment.author.data.user.organisationId
      },

      dialogName () {
        let id = this.comment.id || 'new'

        return 'image-dialog-' + id
      },

      isNew () {
        return !this.comment.id
      }
    },

    data () {
      return {
        comment: ObjectNormaliser.comment(this.value),
        previewImage: undefined,
        previewImageUrl: undefined
      }
    },

    methods: {
      deleteComment (id) {
        $(this.$refs.removeIcon).tooltip('hide')

        Comments.remove(id).then(
          Notifications.success(this, () => {
            this.$emit('removed', id)
          }),

          Notifications.error(this)
        )
      },

      openFileChooser () {
        this.$modal.show(this.dialogName)
      },

      pin () {
        $(this.$refs.pinIcon).tooltip('hide')

        Comments.pin(this.comment.id).then(
          Notifications.success(this, () => {
            this.$emit('pinned', this.comment)
          }),
          Notifications.error(this)
        )
      },

      save () {
        this.$startLoading('global-loader')

        let comment = Object.assign({}, this.comment, {
          author: this.comment.author ? this.comment.author.data.id : this.activeUser.id
        })

        let method = this.isNew ? 'save' : 'update'

        if (this.previewImage) {
          Images.save(this.previewImage).then(response => {
            comment.image = response.body

            Comments[method](comment, 'project', this.projectId).then(
              Notifications.success(this, () => {
                this.$emit('change')

                this.comment = ObjectNormaliser.comment()
                this.previewImage = undefined
                this.previewImageUrl = undefined
              }),
              Notifications.error(this)
            )
          })
        } else {
          Comments[method](comment, 'project', this.projectId).then(
            Notifications.success(this, () => {
              this.$emit('change')
            }),
            Notifications.error(this)
          )
        }
      },

      setNewImage (blob) {
        this.previewImage = blob
        this.previewImage.name = 'image.jpg'

        let fileReader = new FileReader()
        fileReader.onload = (event) => {
          this.previewImageUrl = event.target.result
        }
        fileReader.readAsDataURL(blob)
      },

      unpin () {
        $(this.$refs.pinIcon).tooltip('hide')

        Comments.unpin(this.comment.id).then(
          Notifications.success(this, () => {
            this.$emit('unpinned', this.comment)
          }),
          Notifications.error(this)
        )
      },

      unsetImage () {
        this.previewImage = undefined
        this.previewImageUrl = undefined
      }
    },

    mixins: [ImageMixin, LoaderHelper],

    mounted () {
      if (this.comment && this.comment.id) {
        this.previewImageUrl = this.imageUrl(this.comment.image)
      }
    },

    props: {
      editable: {
        type: Boolean,
        default: true
      },
      projectId: Number,
      value: {
        type: Object,
        default: () => {}
      }
    },

    watch: {
      value (newValue) {
        this.comment = newValue
      }
    }
  }
</script>

<style lang="stylus" scoped>
  .article--content {
    white-space: pre-line;
  }

  .card-block {
    background-color: #fff;
    color: #333;

    .form-control {
      border-radius: 0;
    }
  }

  .article--content .form-control, .article--heading .form-control {
    border-color: transparent;
    border-radius: 0;
    transition: .2s ease border-color;

    &:focus {
      border-color: #eee;
    }
  }

  .article--heading .form-control {
    color: #000;
    font-size: 1.25rem;
    font-weight: 600;
    line-height: 1.4;
  }

  .image-container, .image-container img {
    max-width: 100%;
  }

  .image-placeholder {
    background-color: #f2f2f2;
  }

  .image-edit-icon {
    width: 2em;
    margin-bottom: 20px;
  }
</style>
