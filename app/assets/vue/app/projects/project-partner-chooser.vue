<template>
  <form @submit.prevent="submit()">
    <vue-simple-suggest destyled ref="suggest" :filter-by-query="true" :min-length="0" mode="select" @select="selected" type="search" pattern="[a-z]+" :list="suggestions" display-attribute="name" value-attribute="id">
      <div class="form-group g-mb-20">
        <div class="input-group g-brd-primary--focus">
          <div class="input-group-prepend">
            <span class="input-group-text rounded-0 g-bg-white g-color-gray-light-v1"><i class="icon-user-follow"></i></span>
          </div>
          <input v-model="model" class="form-control form-control-md border-left-0 rounded-0 pl-0" type="text" :placeholder="$t('project.partner.placeholder')">
        </div>
      </div>
    </vue-simple-suggest>

    <unify-button
      :disabled="partner === undefined"
      v-tooltip="tooltip"
      type="submit"
      loading="partner-chooser"
      class="btn u-btn-outline-teal g-font-weight-600 g-letter-spacing-0_5 g-brd-2 g-rounded-0--md g-mr-10 g-mt-20">
      <span>{{ $t('project.partner.add') }}</span>
    </unify-button>
  </form>
</template>

<script>
  import VueSimpleSuggest from 'vue-simple-suggest'
  import 'vue-simple-suggest/dist/styles.css'
  import Organisations from '../../common/services/organisations'

  export default {
    name: 'project-partner-chooser',

    components: {
      VueSimpleSuggest
    },

    created () {
      this.fetchOrganisations()
    },

    data () {
      return {
        model: undefined,
        partner: undefined,
        suggestions: [],
        tooltip: 'Partner auswählen'
      }
    },

    methods: {
      fetchOrganisations () {
        return Organisations.all().then(data => {
          this.suggestions = data.body.items

          return this.suggestions
        })
      },

      selected (value) {
        this.partner = value ? value.id : undefined
        this.model = value ? value.name : undefined
      },

      submit () {
        this.$emit('submit', this.partner)

        this.model = undefined
      }
    },

    mixins: [],

    mounted () {
      this.$nextTick(() => {
        $.HSCore.helpers.HSFocusState.init()
      })
    }
  }
</script>

<style>
  .sbx-google {
    width: 100%;
  }

  .vue-simple-suggest {
    position: relative;
  }

  .vue-simple-suggest .suggestions {
    position: absolute;
    left: 0;
    right: 0;
    top: 100%;
    top: calc(100% + 5px);
    border-radius: 3px;
    border: 1px solid #72c02c;
    background-color: #fff;
    z-index: 1000;
  }

  .vue-simple-suggest .suggestions .suggest-item {
    cursor: pointer;
    user-select: none;
  }

  .vue-simple-suggest .suggestions .suggest-item,
  .vue-simple-suggest .suggestions .misc-item {
    padding: 5px 10px;
  }

  .vue-simple-suggest .suggestions .suggest-item.hover {
    background-color: #18ba9b !important;
    color: #fff !important;
  }

  .vue-simple-suggest .suggestions .suggest-item.selected {
    background-color: #18ba9b;
    color: #fff;
  }
</style>
