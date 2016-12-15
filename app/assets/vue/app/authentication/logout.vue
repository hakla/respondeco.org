<template>
  <h1>Logging out ...</h1>
</template>

<script>

import Authentication from 'common/authentication';
import {
  router
} from '../router';

export default {

  created () {
    Authentication
      .get()
      .loggedOut(() => router.push('/'))
      .logout()
  }

}

</script>
