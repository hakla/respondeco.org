<template>
  <section class="main g-min-height-50vh">
    <transition name="fade" mode="out-in">
      <div v-if="isntLoading()">
        <!-- Hero Info #01 -->
        <unify-hero type="1">
          {{ item.name }}
        </unify-hero>

        <div class="container">
          <div class="row">
            <div class="col-lg-9">
              <article class="g-mb-100" v-for="comment in comments">
                <img class="img-fluid w-100 g-rounded-5 g-mb-25" :src="imageUrl(comment.image)" v-if="comment.image" alt="Image Description">

                <div class="px-4">
                  <ul class="d-flex justify-content-start align-items-end list-inline g-color-gray-dark-v5 g-font-size-13 g-mt-minus-45 g-mb-25">
                    <li class="list-inline-item mr-5">
                      <img class="g-width-40 g-height-40 g-brd-around g-brd-2 g-brd-white rounded-circle mb-2" :src="imageUrl(comment.author.data.image)" v-if="comment.author.data.image" alt="Image Description">
                      <span class="d-block g-width-40 g-height-40" v-else></span>
                      <h4 class="h6 g-font-weight-600 g-font-size-13 mb-0">
                        <router-link :to="{ name: 'organisation-projects', params: { id: comment.author.data.id } }" class="g-color-gray-dark-v4" href="#">{{ comment.author.data.name }}</router-link>
                      </h4>
                    </li>
                    <li class="list-inline-item">
                      <span class="g-font-size-12">May 31, 2017</span>
                    </li>
                    <li class="list-inline-item ml-auto">
                      <a class="g-color-gray-dark-v5 g-color-primary--hover g-font-size-default g-transition-0_3 g-text-underline--none--hover" href="#">
                        <i class="align-middle mr-1 icon-medical-022 u-line-icon-pro"></i>
                      </a>
                      <span class="g-color-gray-dark-v5">5k</span>
                    </li>
                    <li class="list-inline-item ml-3">
                      <a class="g-color-gray-dark-v5 g-color-primary--hover g-font-size-default g-transition-0_3 g-text-underline--none--hover" href="#">
                        <i class="align-middle mr-1 icon-finance-206 u-line-icon-pro"></i>
                      </a>
                      <span class="g-color-gray-dark-v5">10</span>
                    </li>
                  </ul>

                  <h2 class="h5 g-color-black g-font-weight-600">
                    {{ comment.title }}
                  </h2>
                  <p class="g-color-gray-dark-v4">
                    {{ comment.content }}
                  </p>
                </div>
              </article>
            </div>

            <div class="col-lg-3 g-brd-left--lg g-brd-gray-light-v4 g-mb-80">
              <div class="g-pl-20--lg">
                <!-- Links -->
                <div class="g-mb-50">
                  <h3 class="h5 g-color-black g-font-weight-600 mb-4">Links</h3>
                  <ul class="list-unstyled g-font-size-13 mb-0">
                    <li>
                      <a class="d-block u-link-v5 g-color-gray-dark-v4 rounded g-px-20 g-py-8" href="#"><i class="mr-2 fa fa-angle-right"></i> People</a>
                    </li>
                    <li>
                      <a class="d-block u-link-v5 g-color-gray-dark-v4 rounded g-px-20 g-py-8" href="#"><i class="mr-2 fa fa-angle-right"></i> News Publications</a>
                    </li>
                    <li>
                      <a class="d-block u-link-v5 g-color-gray-dark-v4 rounded g-px-20 g-py-8" href="#"><i class="mr-2 fa fa-angle-right"></i> Marketing &amp; IT</a>
                    </li>
                    <li>
                      <a class="d-block u-link-v5 g-color-gray-dark-v4 rounded g-px-20 g-py-8" href="#"><i class="mr-2 fa fa-angle-right"></i> Business Strategy</a>
                    </li>
                    <li>
                      <a class="d-block active u-link-v5 g-color-black g-bg-gray-light-v5 g-font-weight-600 g-rounded-50 g-px-20 g-py-8" href="#"><i class="mr-2 fa fa-angle-right"></i> Untold Stories</a>
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
  import { ImageHelper, ImageMixin, ObjectNormaliser } from '../../common/utils'
  import Comments from '../../common/services/comments'
  import Projects from '../../common/services/projects'
  import ItemPage from '../mixins/item-page'

  export default {
    name: 'project',

    computed: {
      ...mapGetters(['activeUser']),
    },

    created () {
      this.fetchData()

      this.promiseLoading(
        Comments.byProject(this.id).then(result => {
          this.comments = result.body
        })
      )
    },

    data () {
      return {
        comments: [],
        item: ObjectNormaliser.project(),
        service: Projects
      }
    },

    mixins: [ImageMixin, ItemPage]
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
