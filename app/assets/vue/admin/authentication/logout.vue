<script>

import { router } from '../router';

export default {

  name: 'Logout',

  beforeRouteEnter(to, from, next) {
    logout(result => {
      this.$http.post('logout').then(response => {
        router.push('/login');
      });
    })
  }

}
</script>
