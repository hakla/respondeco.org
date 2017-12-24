<template>
  <div class="admin-page">
    <div class="page-title">
      <div class="title_left">
        <h3>
          {{ title }}
        </h3>
      </div>

      <div class="title_right">
        <slot name="title-right"></slot>
      </div>
    </div>

    <div class="clearfix"></div>

    <div class="row">
      <div class="col-md-12">
        <transition name="fade">
          <spinner class="spinner" v-if="$isLoading('admin-page-loader')"></spinner>
          <div class="x_panel" v-if="!$isLoading('admin-page-loader')">
            <div class="x_title">
              <slot name="title"></slot>
              <ul class="nav navbar-right panel_toolbox">
                <li>
                  <slot name="action"></slot>
                </li>
              </ul>
              <div class="clearfix"></div>
            </div>
            <div class="x_content">
              <slot name="body"></slot>
            </div>
          </div>
        </transition>
      </div>
    </div>
  </div>
</template>

<script>
  import Spinner from 'vue-simple-spinner'

  export default {
    name: "admin-page",

    components: {
      Spinner
    },

    props: ['title']
  }
</script>

<style scoped>
  .admin-page {
    min-height: calc(100vh - 50px);
  }

  .spinner {
    margin: 100px;
  }
</style>
