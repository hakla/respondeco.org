<template>
  <div class="app flex-row align-items-center">
    <div class="container">
      <div class="row justify-content-center">
        <div class="col-md-4">
          <div class="card-group">
            <div class="card p-4">
              <div class="card-body">
                <form @submit.prevent="login">
                  <h1>Login</h1>
                  <p class="text-muted">Sign In to your account</p>
                  <div class="input-group mb-3">
                  <span class="input-group-addon">
                    <respondeco-icon icon="user"></respondeco-icon>
                  </span>
                    <input type="text" class="form-control" placeholder="Username" v-model="username">
                  </div>
                  <div class="input-group mb-4">
                <span class="input-group-addon">
                  <respondeco-icon icon="fal lock"></respondeco-icon>
                </span>
                    <input type="password" class="form-control" placeholder="Password" v-model="password">
                  </div>
                  <div class="row">
                    <div class="col">
                      <button class="btn btn-primary btn-block px-4">Login</button>
                    </div>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import Authentication from 'common/authentication'

  export default {
    name: 'login',

    created () {
      Authentication
        .get()
        .error(error => this.error = error)
        .loggedIn(() => this.$router.push('/'))
    },

    data () {
      return {
        password: 'admin',
        username: 'admin@respondeco.org'
      }
    },

    methods: {
      login () {
        Authentication
          .get()
          .login(this.username, this.password)
      }
    }
  }
</script>

<style scoped>

</style>
