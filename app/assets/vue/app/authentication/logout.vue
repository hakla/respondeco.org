<template>
  <div>
    <h1>Logging out ...</h1>
    <vue-simple-spinner class="g-mt-100 g-mb-100"></vue-simple-spinner>
  </div>
</template>

<script>
  import Authentication from 'common/authentication'
  import Config from 'app/config'
  import { router } from '../router'
  import VueSimpleSpinner from 'vue-simple-spinner'

  export default {
    name: 'Logout',

    components: {
      VueSimpleSpinner
    },

    created () {
      Authentication
        .get()
        .loggedOut(() => router.push(Config.defaultRoute))
        .logout()
    }
  }
</script>
