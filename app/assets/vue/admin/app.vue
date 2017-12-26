<template>
  <div class="nav-md">
    <notifications position="top right"></notifications>

    <div class="container body">
      <div class="main_container">
        <div class="col-md-3 left_col">
          <div class="left_col scroll-view">
            <div class="navbar nav_title" style="border: 0;">
              <a href="/admin/" class="site_title">
                <respondeco-icon icon="wrench"></respondeco-icon>
                <span>respondeco Admin</span>
              </a>
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
                    <router-link to="/accounts">
                      <respondeco-icon icon="users"/>
                      Accounts
                    </router-link>
                  </li>
                  <li>
                    <router-link to="/">
                      <respondeco-icon icon="fal building"/>
                      Organisationen
                    </router-link>
                  </li>
                  <li>
                    <router-link to="/projects">
                      <respondeco-icon icon="bullhorn"/>
                      Projekte
                    </router-link>
                  </li>
                  <li>
                    <router-link to="/finishedProjects">
                      <respondeco-icon icon="trophy"/>
                      Abgeschlossene Projekte
                    </router-link>
                  </li>
                  <li>
                    <a href="/app/">Zu respondeco.org</a>
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
  import 'bootstrap/dist/js/bootstrap.min.js'
  import 'bootstrap/dist/css/bootstrap.min.css'
  import 'gentelella/build/css/custom.min.css'

  export default {
    name: 'app',
    components: {},
    data () {
      let isDev = window.respondeco === 'dev'

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

  .side-menu .svg-inline--fa {
    font-size: 16px;
    width: 30px;
  }
</style>
