<template>
  <respondeco-dialog name="file-dialog" title="Logo ändern" :clickToClose="false" @beforeOpen="reset">
    <respondeco-dialog-buttons slot="buttons">
      <respondeco-dialog-button class="progress-button" @click="clicked">
        <span>Upload</span>
        <div class="progress" :class="{ uploading: uploading }" :style="{ width: `${progress}%` }"></div>
      </respondeco-dialog-button>
    </respondeco-dialog-buttons>

    <div class="text-center g-mt-20">
      <file-upload
        ref="upload"
        v-model="files"
        post-action="/api/v1/images"
        @input-file="inputFile"
        @input-filter="inputFilter"
        :headers="tokenHeader"
        :drop="true"
      >
        <figure class="g-mb-10">
          <img class="img-fluid w-100" :src="activeFile" alt="Image Description">
        </figure>

        Datei wählen
      </file-upload>
    </div>
  </respondeco-dialog>
</template>

<script>
  import RespondecoDialog from 'app/main/dialog'
  import RespondecoDialogButton from 'app/main/dialog-button'
  import RespondecoDialogButtons from 'app/main/dialog-buttons'

  import TokenHolder from 'common/token-holder'

  export default {
    name: "file-dialog",

    components: {
      RespondecoDialog,
      RespondecoDialogButton,
      RespondecoDialogButtons
    },

    computed: {
      tokenHeader () {
        return {
          'X-Access-Token': TokenHolder.sync()
        }
      }
    },

    data () {
      return {
        activeFile: '',
        files: [],
        progress: 0,
        uploading: false
      }
    },

    methods: {
      clicked () {
        this.$refs.upload.active = true
      },

      inputFile(newFile, oldFile) {
        if (newFile && oldFile) {
          // Update file

          // Start upload
          if (newFile.active !== oldFile.active) {
            this.progress = 0
            this.uploading = true
            this.$emit('beginUpload')
          }

          // Upload progress
          if (newFile.progress !== oldFile.progress) {
            this.progress = newFile.progress

            this.$emit('progress', this.progress)
          }

          // Upload error
          if (newFile.error !== oldFile.error) {
            this.progress = 0
            this.uploading = false

            this.$emit('error', newFile.error)
          }

          // Uploaded successfully
          if (newFile.success !== oldFile.success) {
            this.progress = 0
            this.uploading = false
            this.$emit('uploaded', newFile.response)
          }
        }
      },

      /**
       * Pretreatment
       * @param  Object|undefined   newFile   Read and write
       * @param  Object|undefined   oldFile   Read only
       * @param  Function           prevent   Prevent changing
       * @return undefined
       */
      inputFilter: function (newFile, oldFile, prevent) {
        if (newFile && !oldFile) {
          // Filter non-image file
          if (!/\.(jpeg|jpe|jpg|gif|png|webp)$/i.test(newFile.name)) {
            return prevent()
          }

          // Create a blob field
          newFile.blob = ''
          let URL = window.URL || window.webkitURL
          if (URL && URL.createObjectURL) {
            newFile.blob = URL.createObjectURL(newFile.file)
            this.activeFile = newFile.blob
          }
        }
      },

      reset () {
        this.progress = 0
        this.activeFile = this.file
      }
    },

    props: ['file'],

    watch: {
      file (newValue) {
        this.activeFile = newValue
      }
    }
  }
</script>

<style scoped>
  .progress-button {
    position: relative;
  }

  .progress-button span {
    position: relative;
    z-index: 1;
  }

  .progress {
    position: absolute;
    left: 0;
    top: 0;
    height: 100%;
    border-radius: 0;
    background-color: #ddd;
    width: 0;
  }

  .progress.uploading {
    transition: .2s ease width;
  }
</style>
