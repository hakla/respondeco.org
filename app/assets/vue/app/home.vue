<template>
  <main>
    <!--<respondeco-header></respondeco-header>-->

    <main class="g-pb-100">
      <!-- Promo Block -->
      <section class="u-bg-overlay g-bg-img-hero g-bg-bluegray-opacity-0_3--after" style="background-image: url('/assets/images/home_bg.jpg');">
        <div class="container u-bg-overlay__inner text-center g-pt-150 g-pb-70">
          <div class="g-mb-100">
            <span class="d-block g-color-white g-font-size-20 text-uppercase g-letter-spacing-5 mb-4">{{ $t('home.header.sup') }}</span>
            <h1 class="g-color-white g-font-weight-700 g-font-size-20 g-font-size-50--md text-uppercase g-line-height-1_2 g-letter-spacing-10 mb-4">{{ $t('home.header.title') }}</h1>
            <span class="d-block lead g-color-white g-letter-spacing-2">{{ $t('home.header.sub') || '&nbsp;' }}</span>
          </div>

          <!--<div class="g-pos-abs g-left-0 g-right-0 g-z-index-2 g-bottom-30">-->
            <!--<a class="js-go-to btn u-btn-outline-white g-color-white g-bg-white-opacity-0_1 g-color-black&#45;&#45;hover g-bg-white&#45;&#45;hover g-font-weight-600 text-uppercase g-rounded-50 g-px-30 g-py-11" href="#!" data-target="#content">-->
              <!--<respondeco-icon icon="fal angle-down"></respondeco-icon>-->
            <!--</a>-->
          <!--</div>-->
        </div>
      </section>
      <!-- End Promo Block -->

      <unify-hero>{{ $t('home.organisations') }}</unify-hero>

      <transition name="fade" mode="out-in">
        <div class="container">
          <respondeco-portfolio columns="col-sm-6 col-md-3" :items="organisations" v-if="!$isLoading('organisations')"></respondeco-portfolio>
          <spinner v-if="$isLoading('organisations')"></spinner>
        </div>
      </transition>

      <unify-hero>{{ $t('home.projects') }}</unify-hero>

      <transition name="fade" mode="out-in">
        <div class="container">
          <respondeco-portfolio columns="col-sm-6 col-md-3" :items="projects" v-if="!$isLoading('projects')"></respondeco-portfolio>
          <spinner v-if="$isLoading('projects')"></spinner>
        </div>
      </transition>

      <unify-hero>{{ $t('home.values') }}</unify-hero>

      <transition name="fade" mode="out-in">
        <div class="container">
          <respondeco-our-values></respondeco-our-values>
        </div>
      </transition>

      <unify-hero>{{ $t('home.about_us') }}</unify-hero>

      <transition name="fade" mode="out-in">
        <div class="container">
          <respondeco-about-us></respondeco-about-us>
        </div>
      </transition>

    </main>

    <!--<respondeco-footer></respondeco-footer>-->
  </main>
</template>

<script>
  import RespondecoAboutUs from 'app/main/about-us'
  import RespondecoFooter from 'app/main/footer'
  import RespondecoHeader from 'app/main/header'
  import RespondecoOurValues from 'app/main/our-values'
  import RespondecoPortfolio from 'app/main/portfolio'

  import Spinner from 'vue-simple-spinner'

  import Utils from 'app/utils'

  import { mapActions, mapGetters } from 'vuex'

  import Organisations from 'common/services/organisations'
  import Projects from 'common/services/projects'

  export default {
    name: "home",

    components: {
      RespondecoAboutUs,
      RespondecoFooter,
      RespondecoHeader,
      RespondecoOurValues,
      RespondecoPortfolio,
      Spinner
    },

    created () {
      this.$startLoading('organisations')
      this.$startLoading('projects')

      Organisations.all({
        page: 1,
        pageSize: 8
      }).then(response => {
        let toPortfolioItem = Utils.toPortfolioItem("organisations")

        this.organisations = response.body.items.map(toPortfolioItem)

        this.$endLoading('organisations')
      })

      Projects.all({
        page: 1,
        pageSize: 8
      }).then(response => {
        let toPortfolioItem = Utils.projectToPortfolioItem

        this.projects = response.body.items.map((item) => toPortfolioItem(item))

        this.$endLoading('projects')
      })
    },

    data () {
      return {
        organisations: [],
        projects: []
      }
    },

    methods: {
      ...mapActions(['all'])
    }
  }
</script>

<style scoped>

</style>
