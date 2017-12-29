<template>
  <respondeco-dialog
    :height="height"
    :width="width"
    :name="name"
    ref="dialog"
    title="Logo ändern"
    @beforeOpen="reset"
    @opened="initialize"
    :clickToClose="false"
    :scrollable="false"
  >
    <respondeco-dialog-buttons slot="buttons" v-if="activeImage">
      <respondeco-dialog-button @click.prevent.stop="choose">
        <span>Auswählen</span>
      </respondeco-dialog-button>
    </respondeco-dialog-buttons>

    <drop @dragenter="highlight" @drop="handleDrop">
      <div class="d-flex align-items-center justify-content-center" :style="{ height: height - 110 + 'px' }">
        <div class="row">
          <div class="col-12">
            <div class="text-center g-mt-20 cropper-wrapper">
              <img id="image" ref="image" :style="{ opacity: activeImage ? 1 : 0 }">
            </div>
          </div>

          <div class="col-12">
            <div class="g-py-20 text-center drop d-flex align-items-center justify-content-center">
              <label class="btn g-cursor-pointer u-btn-outline-teal g-py-5 g-mb-0" for="chooser">
                <span v-if="!activeImage">{{ $t('image.dialog.choose') }}</span>
                <span v-if="activeImage">{{ $t('image.dialog.chooseAnother') }}</span>
                <input type="file" id="chooser" @change="loadFile">
              </label>
              <span class="btn u-btn g-py-5 g-mb-0">{{ $t('image.dialog.orDragAndDrop') }}</span>
            </div>
          </div>
        </div>
      </div>
    </drop>
  </respondeco-dialog>
</template>

<script>
  import Cropper from 'cropperjs'
  import 'cropperjs/dist/cropper.min.css'

  import { Drop } from 'vue-drag-drop'

  import RespondecoDialog from 'app/main/dialog'
  import RespondecoDialogButton from 'app/main/dialog-button'
  import RespondecoDialogButtons from 'app/main/dialog-buttons'

  export default {
    name: 'image-dialog',

    components: {
      Drop,
      RespondecoDialog,
      RespondecoDialogButton,
      RespondecoDialogButtons
    },

    created () {
      this.activeImage = this.file
    },

    data () {
      return {
        activeImage: '',
        height: 200,
        width: 400
      }
    },

    methods: {
      choose () {
        this.cropper.getCroppedCanvas({
          fillColor: '#fff',
          height: 1080,
          maxHeight: 8192,
          maxWidth: 8192,
          width: 1920
        }).toBlob(blob => {
          this.$emit('changed', blob)
          this.$modal.hide(this.name)
        })
      },

      handleDrop (data, event) {
        event.preventDefault()

        this.fromFile(event.dataTransfer.files[0])
      },

      fromFile (file) {
        this.activeImage = file

        this.height = 700
        this.width = 1000

        let fileReader = new FileReader()
        fileReader.readAsDataURL(file)
        fileReader.addEventListener('load', event => {
          if (this.cropper) {
            this.cropper.destroy()
          }

          this.initialize(event.target.result)
        })
      },

      highlight () {
        console.log('dragenter')
      },

      initialize (src) {
        if (this.$refs.image && src) {
          this.$refs.image.src = src

          this.cropper = new Cropper(this.$refs.image, {
            aspectRatio: this.aspectRatio,
            viewMode: this.viewMode
          })
        }
      },

      loadFile (event) {
        this.fromFile(event.target.files[0])
      },

      reset () {
        this.activeImage = undefined
        this.height = 200
        this.width = 400
      }
    },

    mounted () {
      if (this.file) {
        this.initialize(this.file)
      }
    },

    props: {
      aspectRatio: {
        type: Number,
        default: 16 / 9
      },
      file: String,
      name: String,
      viewMode: Number
    },

    watch: {
      file (newValue) {
        this.initialize(newValue)
      }
    }
  }
</script>

<style scoped>
  .cropper-wrapper {
    max-height: 500px;
  }

  .drop {
    height: 100px;
    width: 100%;
  }

  img {
    max-width: 100%;
  }

  input[type=file] {
    display: none;
  }

  .row {
    width: 100%;
  }
</style>
