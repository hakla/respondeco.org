<template>
  <form @submit.prevent.stop="save">
    <article class="g-mb-100 position-relative" v-if="editable">
      <i class="g-cursor-pointer position-absolute g-top-minus-30 g-right-0 g-font-size-20"
         @click="deleteComment(comment.id)" v-if="editable && comment.id != null">
        <respondeco-icon icon="fal times"></respondeco-icon>
      </i>

      <image-dialog :file="comment ? comment.image : undefined" :name="dialogName" :viewMode="1"
                    @changed="setNewImage"></image-dialog>

      <transition name="fade" mode="out-in">
        <img class="img-fluid w-100 g-rounded-5 g-mb-25" :src="previewImageUrl" v-if="previewImageUrl"
             alt="Image Description" @click="openFileChooser">

        <div
          class="d-flex align-items-center g-cursor-pointer justify-content-center image-placeholder img-fluid w-100 g-rounded-5 g-mb-25 g-height-200 g-brd-1 g-brd-teal g-brd-style-solid"
          @click="openFileChooser" v-else>
          {{ $t('project.new.chooseImage') }}
        </div>
      </transition>

      <div class="px-4">
        <ul
          class="d-flex justify-content-start align-items-end list-inline g-color-gray-dark-v5 g-font-size-13 g-mt-minus-45 g-mb-25">
          <li class="list-inline-item mr-5">
            <img class="g-width-40 g-height-40 g-brd-around g-brd-2 g-brd-white rounded-circle mb-2"
                 :src="imageUrl(activeUser.image)" v-if="activeUser.image"
                 alt="Image Description">
            <span class="d-block g-width-40 g-height-40" v-else></span>
            <h4 class="h6 g-font-weight-600 g-font-size-13 mb-0">
              {{ activeUser.name }}
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
          <button
            class="btn u-btn-outline-teal g-font-weight-600 g-letter-spacing-0_5 g-brd-2 g-rounded-0--md">
            <span v-if="comment.id">{{ $t('common.save') }}</span>
            <span v-else>{{ $t('common.add') }}</span>
          </button>
        </div>
      </div>
    </article>
    <article class="g-mb-100 position-relative" v-else>
      <i class="g-cursor-pointer position-absolute g-top-minus-30 g-right-0 g-font-size-20"
         @click="deleteComment(comment.id)" v-if="editable">
        <respondeco-icon icon="fal times"></respondeco-icon>
      </i>

      <img class="img-fluid w-100 g-rounded-5 g-mb-25" :src="imageUrl(comment.image)" v-if="comment.image"
           alt="Image Description">

      <div class="px-4">
        <ul
          class="d-flex justify-content-start align-items-end list-inline g-color-gray-dark-v5 g-font-size-13 g-mt-minus-45 g-mb-25">
          <li class="list-inline-item mr-5">
            <img class="g-width-40 g-height-40 g-brd-around g-brd-2 g-brd-white rounded-circle mb-2"
                 :src="imageUrl(comment.author.data.image)" v-if="comment.author.data.image"
                 alt="Image Description">
            <span class="d-block g-width-40 g-height-40" v-else></span>
            <h4 class="h6 g-font-weight-600 g-font-size-13 mb-0">
              <router-link :to="{ name: 'organisation-projects', params: { id: comment.author.data.id } }"
                           class="g-color-gray-dark-v4" href="#">{{ comment.author.data.name }}
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
  import { mapGetters } from 'vuex'
  import { ImageMixin, Notifications, ObjectNormaliser } from '../../common/utils'
  import Comments from '../../common/services/comments'
  import ImageDialog from '../main/image-dialog'
  import Images from '../../common/services/images'

  export default {
    name: 'project-comment',

    components: {
      ImageDialog
    },

    computed: {
      ...mapGetters(['activeUser']),

      dialogName () {
        let id = this.comment.id || 'new'

        return 'image-dialog-' + id
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
        Comments.remove(id).then(
          Notifications.success(this, () => {
            this.$emit('change')
          }),
          Notifications.error(this)
        )
      },

      openFileChooser () {
        this.$modal.show(this.dialogName)
      },

      save () {
        if (this.previewImage) {
          Images.save(this.previewImage).then(response => {
            let comment = Object.assign({}, this.comment, {
              author: this.activeUser.id,
              image: response.body
            })

            Comments.save(comment, 'project', this.projectId).then(
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
          let comment = Object.assign({}, this.comment, {
            author: this.comment.author.data.id
          })

          Comments.update(comment, 'project', this.projectId).then(
            Notifications.success(this, () => {
              this.$emit('change')
            }),
            Notifications.error(this)
          )
        }
      },

      setNewImage (blob) {
        this.previewImage = blob
        this.previewImage.name = 'image.png'

        let fileReader = new FileReader()
        fileReader.onload = (event) => {
          this.previewImageUrl = event.target.result
        }
        fileReader.readAsDataURL(blob)
      },

      unsetImage () {
        this.previewImage = undefined
        this.previewImageUrl = undefined
      }
    },

    mixins: [ImageMixin],

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
</style>
