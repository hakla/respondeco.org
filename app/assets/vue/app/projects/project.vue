<template>
  <section class="main g-min-height-50vh g-pb-100">
    <transition name="fade" mode="out-in">
      <div v-if="isntLoading()">
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
              <project-comment @change="loadComments" :editable="activeUserIsOwner" :project-id="item.id" :value="comment" v-for="comment in comments" :key="comment.id"></project-comment>
            </div>

            <div class="col-lg-3 g-brd-left--lg g-brd-gray-light-v4 g-mb-80">
              <div class="g-pl-20--lg">
                <!-- Links -->
                <div class="g-mb-50">
                  <h3 class="h5 g-color-black g-font-weight-600 mb-4">Links</h3>
                  <ul class="list-unstyled g-font-size-13 mb-0">
                    <li>
                      <a class="d-block u-link-v5 g-color-gray-dark-v4 rounded g-px-20 g-py-8" href="#"><i
                        class="mr-2 fa fa-angle-right"></i> People</a>
                    </li>
                    <li>
                      <a class="d-block u-link-v5 g-color-gray-dark-v4 rounded g-px-20 g-py-8" href="#"><i
                        class="mr-2 fa fa-angle-right"></i> News Publications</a>
                    </li>
                    <li>
                      <a class="d-block u-link-v5 g-color-gray-dark-v4 rounded g-px-20 g-py-8" href="#"><i
                        class="mr-2 fa fa-angle-right"></i> Marketing &amp; IT</a>
                    </li>
                    <li>
                      <a class="d-block u-link-v5 g-color-gray-dark-v4 rounded g-px-20 g-py-8" href="#"><i
                        class="mr-2 fa fa-angle-right"></i> Business Strategy</a>
                    </li>
                    <li>
                      <a
                        class="d-block active u-link-v5 g-color-black g-bg-gray-light-v5 g-font-weight-600 g-rounded-50 g-px-20 g-py-8"
                        href="#"><i class="mr-2 fa fa-angle-right"></i> Untold Stories</a>
                    </li>
                  </ul>
                </div>
                <!-- End Links -->
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
    },

    data () {
      return {
        comments: [],
        item: ObjectNormaliser.project(),
        service: Projects
      }
    },

    methods: {
      loadComments () {
        this.promiseLoading(
          Comments.byProject(this.id).then(result => {
            this.comments = result.body
          })
        )
      },
    },

    mixins: [DateFilter, ImageMixin, ItemPage]
  }
</script>

<style lang="stylus" scoped>
</style>
