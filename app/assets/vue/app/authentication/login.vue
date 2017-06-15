<template>
<div class="container-fluid">
  <div class="row equal-height-columns">
    <div class="col-md-6 col-sm-6 hidden-xs image-block equal-height-column">
      <!-- <img src="/assets/images/login.jpg" /> -->
    </div>

    <div class="col-md-6 col-sm-6 form-block equal-height-column">
      <router-link to="/">
        <img src="/assets/images/logo-small.png" alt="">
      </router-link>
      <h2 class="margin-bottom-30">Login To Your Account</h2>
      <p v-if="error != undefined">Test</p>
      <form action="#" v-on:submit.prevent="login">
        <div class="login-block">
          <div class="input-group margin-bottom-20">
            <span class="input-group-addon rounded-left"><i class="icon-user color-blue"></i></span>
            <input type="text" class="form-control rounded-right" placeholder="Username" v-model="user" autofocus>
          </div>

          <div class="input-group margin-bottom-20">
            <span class="input-group-addon rounded-left"><i class="icon-lock color-blue"></i></span>
            <input type="password" class="form-control rounded-right" placeholder="Password" v-model="password">
          </div>

          <div class="checkbox">
            <ul class="list-inline">
              <li>
                <label>
									<input type="checkbox"> Remember me
								</label>
              </li>

              <li class="pull-right">
                <a href="#">Forgot password?</a>
              </li>
            </ul>
          </div>

          <div class="row margin-bottom-70">
            <div class="col-md-12">
              <button type="submit" class="btn-u btn-u-blue btn-block rounded">Sign In</button>
            </div>
          </div>

          <div class="social-login text-center">
            <div class="hidden">
              <div class="or rounded-x">Or</div>
              <ul class="list-inline margin-bottom-20">
                <li>
                  <button class="btn rounded btn-lg btn-facebook">
                    <i class="fa fa-facebook"></i> Facebook Sign in
                  </button>
                </li>
                <li>
                  <button class="btn rounded btn-lg btn-twitter">
                    <i class="fa fa-twitter"></i> Twitter Sign in
                  </button>
                </li>
              </ul>
            </div>
            <p>Don't have an account? <a href="page_registration2.html">Create New</a></p>
          </div>
        </div>
      </form>
    </div>
  </div>
</div>
<!--/container-->
</template>

<script>
import 'unify/css/pages/page_log_reg_v4.css';
import 'unify/plugins/brand-buttons/brand-buttons.css';

import Authentication from 'common/authentication';
import {
  router
} from '../router';

export default {
  name: 'Login',

  created() {
    Authentication
      .get()
      .error(error => this.error = error)
      .loggedIn(() => router.push('/'));
  },

  data() {
    return {
      error: undefined,
      user: '',
      password: ''
    }
  },

  methods: {
    login() {
      Authentication
        .get()
        .login(this.user, this.password);
    }
  }
}
</script>

<style lang="css">
  body {
    margin-bottom: 0 !important;
  }
</style>
