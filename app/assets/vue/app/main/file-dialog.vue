<template>
  <respondeco-dialog name="file-dialog" title="Logo ändern" :clickToClose="false" @beforeOpen="reset">
    <respondeco-dialog-buttons slot="buttons">
      <respondeco-dialog-button class="progress-button" @click="upload">
        <span>Upload</span>
        <div class="progress" :class="{ uploading: uploading }" :style="{ width: `${progress}%` }"></div>
      </respondeco-dialog-button>
    </respondeco-dialog-buttons>

    <div class="text-center g-mt-20">
      <croppa
        style="border: 1px solid #ddd"
        :height="300"
        :initial-image="file"
        initial-size="contain"
        initial-position="center"
        :placeholder="$t('filechooser.placeholder')"
        :placeholder-font-size="16"
        :quality="2"
        remove-button-color="black"
        :show-loading="true"
        v-model="croppa"
        :width="300"
        :zoom-speed="3"
      ></croppa>

      <file-upload
        ref="upload"
        v-model="files"
        post-action="/api/v1/images"
        @input-file="inputFile"
        :headers="tokenHeader"
      ></file-upload>
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
        activeImage: '',
        croppa: {},
        files: [],
        progress: 0,
        uploading: false
      }
    },

    methods: {

      inputFile (newFile, oldFile) {
        console.log(newFile)

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

      reset () {
        this.progress = 0
        this.activeImage = this.file
      },

      upload () {
        this.croppa.generateBlob(blob => {
          blob.lastModifiedDate = new Date()
          blob.name = 'image.png'

          this.$refs.upload.add(blob)

          this.$refs.upload.active = true
        }, 'image/png')
      },
    },

    props: ['file'],

    watch: {
      file (newValue) {
        this.activeImage = newValue
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
