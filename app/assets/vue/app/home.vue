<template>
  <main>
    <!-- Promo Block -->
    <section class="u-bg-overlay g-bg-img-hero g-bg-bluegray-opacity-0_3--after background-image">
      <div class="container u-bg-overlay__inner text-center g-pt-150 g-pb-70">
        <div class="g-mb-100">
          <span class="d-block g-color-white g-font-size-20 text-uppercase g-letter-spacing-5 mb-4">{{
            $t('home.header.sup') }}</span>
          <h1
            class="g-color-white g-font-weight-700 g-font-size-20 g-font-size-50--md text-uppercase g-line-height-1_2 g-letter-spacing-10 mb-4">
            {{ $t('home.header.title') }}</h1>
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

    <unify-page>
      <unify-hero>{{ $t('home.organisations.title') }}</unify-hero>

      <transition name="fade" mode="out-in">
        <div class="container">
          <respondeco-portfolio :callToAction="{ link: 'organisations', value: $t('home.organisations.discover') }" columns="col-sm-6 col-md-3" :items="organisations"
                                v-if="!$isLoading('organisations')"></respondeco-portfolio>
          <spinner v-if="$isLoading('organisations')"></spinner>
        </div>
      </transition>
    </unify-page>

    <unify-page class="g-bg-gray-light-v5">
      <unify-hero>{{ $t('home.projects.title') }}</unify-hero>

      <transition name="fade" mode="out-in">
        <div class="container">
          <respondeco-portfolio :callToAction="{ link: 'projects', value: 'Entdecke alle Projekte' }"
                                columns="col-sm-6 col-md-3" :items="projects"
                                v-if="!$isLoading('projects')"></respondeco-portfolio>
          <spinner v-if="$isLoading('projects')"></spinner>
        </div>
      </transition>
    </unify-page>

    <unify-page class="g-mb-50">
      <unify-hero>{{ $t('home.values') }}</unify-hero>

      <transition name="fade" mode="out-in">
        <div class="container">
          <respondeco-our-values></respondeco-our-values>
        </div>
      </transition>
    </unify-page>

    <unify-page class="g-bg-gray-light-v5">
      <unify-hero>Sinn der Sache</unify-hero>

      <respondeco-meaning></respondeco-meaning>
    </unify-page>

    <unify-page>
      <unify-hero>Unsere Arbeitsprinzipien</unify-hero>

      <transition name="fade" mode="out-in">
        <respondeco-our-principles></respondeco-our-principles>
      </transition>
    </unify-page>

    <unify-page class="g-color-white u-bg-overlay g-bg-blue-lineargradient g-bg-bluegray-opacity-0_3--after">
      <unify-hero class="g-pos-rel g-z-index-1">{{ $t('home.about_us') }}</unify-hero>

      <transition name="fade" mode="out-in">
        <div class="container g-pos-rel g-z-index-1">
          <respondeco-about-us></respondeco-about-us>
        </div>
      </transition>
    </unify-page>

  </main>
</template>

<script>
  import RespondecoAboutUs from 'app/main/about-us'
  import RespondecoFooter from 'app/main/footer'
  import RespondecoHeader from 'app/main/header'
  import RespondecoMeaning from 'app/main/meaning'
  import RespondecoOurPrinciples from 'app/main/our-principles'
  import RespondecoOurValues from 'app/main/our-values'
  import RespondecoPortfolio from 'app/main/portfolio'

  import Spinner from 'vue-simple-spinner'

  import Utils from 'app/utils'

  import { mapActions } from 'vuex'

  import Organisations from 'common/services/organisations'
  import Projects from 'common/services/projects'

  export default {
    name: 'home',

    components: {
      RespondecoAboutUs,
      RespondecoFooter,
      RespondecoHeader,
      RespondecoMeaning,
      RespondecoOurPrinciples,
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
        let toPortfolioItem = Utils.toPortfolioItem('organisations')

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
  .background-image {
    background-image: url('/assets/images/home_bg.jpg');
    background-position: 82% 57%;
  }
</style>
