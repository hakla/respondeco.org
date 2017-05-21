<template>
  <div class="nav-md">
    <div class="container body">
      <div class="main_container">
        <div class="col-md-3 left_col">
          <div class="left_col scroll-view">
            <div class="navbar nav_title" style="border: 0;">
              <a href="index.html" class="site_title"><i class="fa fa-paw"></i> <span>respondeco Admin</span></a>
            </div>

            <div class="clearfix"></div>

            <!-- menu profile quick info -->
            <div class="profile">
              <div class="profile_pic">
                <img src="/assets/images/logo.png" alt="..." class="img-circle profile_img">
              </div>
              <div class="profile_info">
                <span>Welcome,</span>
                <h2>respondeco</h2>
              </div>
            </div>
            <!-- /menu profile quick info -->

            <br/>

            <!-- sidebar menu -->
            <div id="sidebar-menu" class="main_menu_side hidden-print main_menu">
              <div class="menu_section">
                <h3>General</h3>
                <ul class="nav side-menu">
                  <li>
                    <router-link to="/organisations"><i class="fa fa-organisation"></i> Organisationen</router-link>
                  </li>
                  <li>
                    <router-link to="/projects"><i class="fa fa-project"></i> Projekte</router-link>
                  </li>
                </ul>
              </div>

            </div>
            <!-- /sidebar menu -->
          </div>
        </div>

        <!-- page content -->
        <div class="right_col" role="main">
          <router-view></router-view>
        </div>
        <!-- /page content -->
      </div>
    </div>

    <!-- footer content -->
    <footer v-bind:class="{ development: isDev }">
      <div class="pull-right">
        Gentelella - Bootstrap Admin Template by <a href="https://colorlib.com">Colorlib</a>
      </div>
      <div>
        {{ mode }}
      </div>
      <div class="clearfix"></div>
    </footer>
    <!-- /footer content -->
  </div>
</template>

<script>
  import jQuery from 'jquery';
  import moment from 'moment';
  import 'bootstrap/dist/js/bootstrap.min.js';
  import 'bootstrap/dist/css/bootstrap.min.css';
  import 'gentelella/build/css/custom.min.css';
  import 'font-awesome/css/font-awesome.css';
  import daterangepicker from 'gentelella/production/js/datepicker/daterangepicker.js';

  export default {
    name: 'app',
    components: {},
    data () {
      let isDev = window.respondeco === 'dev';

      return {
        isDev,
        mode: isDev ? "Entwicklung" : "Produktion"
      }
    }
  }
</script>

<style>
  #app {
    font-family: 'Avenir', Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    color: #2c3e50;
  }

  body {
    margin-bottom: 35px !important;
  }
</style>

<style scoped>
  footer {
    position: fixed;
    bottom: 0;
    left: 0;
    padding: 10px;
    width: 100%;
    margin: 0;
    color: #333;
    text-align: left;
    background-color: #1abb9c;
    text-shadow: none;
  }

  footer.development {
    background-color: #9c3737;
    color: #fff;
  }
</style>
