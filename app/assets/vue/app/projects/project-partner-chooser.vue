<template>
  <respondeco-dialog name="addPartnerDialog" :title="$t('project.partner.dialog.title')" :height="'800px'" :width="800">
    <label>Partner-Organisation wählen</label>
    <multiselect v-model="partner" :options="['test']"></multiselect>
  </respondeco-dialog>
</template>

<script>
  import Multiselect from 'vue-multiselect'
  import RespondecoDialog from '../main/dialog'

  export default {
    name: 'project-partner-chooser',

    components: {
      Multiselect,
      RespondecoDialog
    },

    created () {},

    data () {
      return {
        partner: -1
      }
    },

    methods: {}
  }
</script>
