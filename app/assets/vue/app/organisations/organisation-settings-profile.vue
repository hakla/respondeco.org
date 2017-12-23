<template>
  <div id="nav-1-1-default-hor-left-underline">
    <!-- Edit Profile -->
    <div class="tab-pane fade active show" id="nav-1-1-default-hor-left-underline--1" role="tabpanel"
         aria-expanded="true">
      <h2 class="h4 g-font-weight-300">Manage your Name, ID and Email Addresses</h2>
      <p>Below are name, email addresse, contacts and more on file for your account.</p>

      <form @submit.prevent="save">
        <unify-list>
          <!-- name -->
          <unify-list-item>
            <unify-list-item-label>{{ $t('organisation.settings.name') }}</unify-list-item-label>

            <unify-list-item-content>
              <unify-form-input v-model="organisation.name"></unify-form-input>
            </unify-list-item-content>
          </unify-list-item>

          <!-- email -->
          <unify-list-item>
            <unify-list-item-label>{{ $t('organisation.settings.email') }}</unify-list-item-label>

            <unify-list-item-content>
              <unify-form-input v-model="organisation.email" type="email"></unify-form-input>
            </unify-list-item-content>
          </unify-list-item>

          <!-- location -->
          <unify-list-item>
            <unify-list-item-label>{{ $t('organisation.settings.location') }}</unify-list-item-label>

            <unify-list-item-content>
              <unify-form-input v-model="organisation.location"></unify-form-input>
            </unify-list-item-content>
          </unify-list-item>

          <!-- website -->
          <unify-list-item>
            <unify-list-item-label>{{ $t('organisation.settings.website') }}</unify-list-item-label>

            <unify-list-item-content>
              <unify-form-input v-model="organisation.website"></unify-form-input>
            </unify-list-item-content>
          </unify-list-item>

          <!-- website -->
          <unify-list-item>
            <unify-list-item-label>{{ $t('organisation.settings.description') }}</unify-list-item-label>

            <unify-list-item-content>
              <unify-textarea rows="10" v-model="organisation.description"></unify-textarea>
            </unify-list-item-content>
          </unify-list-item>
        </unify-list>

        <div class="text-sm-right">
          <unify-button class="btn-text g-mr-10" size="small" @click="reset">
            {{ $t('common.cancel') }}
          </unify-button>
          <unify-button class="u-btn-primary g-mr-10" loading="organisation.settings.saving" size="medium" type="submit">
            {{ $t('common.save') }}
          </unify-button>
        </div>
      </form>
    </div>
    <!-- End Edit Profile -->

  </div>
</template>

<script>
  import { mapActions, mapGetters } from 'vuex'
  import UnifyButton from 'common/components/unify-button'
  import UnifyFormInput from 'common/components/unify-form-input'
  import UnifyList from 'common/components/unify-list'
  import UnifyListItem from 'common/components/unify-list-item'
  import UnifyListItemLabel from 'common/components/unify-list-item-label'
  import UnifyListItemContent from 'common/components/unify-list-item-content'
  import UnifyTextarea from 'common/components/unify-textarea'

  export default {
    name: "OrganisationSettingsProfile",

    components: {
      UnifyButton,
      UnifyFormInput,
      UnifyList,
      UnifyListItem,
      UnifyListItemContent,
      UnifyListItemLabel,
      UnifyTextarea
    },

    data () {
      return {
        organisation: Object.assign({}, this.$store.state.organisation.current)
      }
    },

    methods: {
      ...mapActions(['current']),

      reset () {
        this.organisation = Object.assign({}, this.$store.state.organisation.current)
      },

      save () {
        this.$startLoading('organisation.settings.saving')
        this.current(this.organisation)
        this.reset()

        setTimeout(() => {
          this.$endLoading('organisation.settings.saving')
        }, 2500)
      }
    }
  }
</script>

<style scoped>
  .unify-list-item-label {
    flex-basis: 200px;
  }

  .unify-list-item-content {
    flex-grow: 1;
  }
</style>
