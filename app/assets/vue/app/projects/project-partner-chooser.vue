<template>
  <form @submit.prevent="submit()">
    <vue-simple-suggest ref="suggest" mode="select" @select="selected" type="search" pattern="[a-z]+" :list="filter" display-attribute="name" value-attribute="id">
      <div class="form-group g-mb-20">
        <div class="input-group g-brd-primary--focus">
          <div class="input-group-addon d-flex align-items-center g-bg-white g-color-gray-light-v1 rounded-0">
            <i class="icon-user-follow"></i>
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

      filter (query) {
        return this.suggestions.filter(suggestion => suggestion.name.toLowerCase().indexOf(query.toLowerCase()) > -1)
      },

      selected (value) {
        this.partner = value ? value.id : undefined
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
</style>
