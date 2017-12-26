import { BaseService } from 'common/http/base-service'

export default class Images extends BaseService {

  constructor () {
    super('images{/id}')
  }

  static save (blob) {
    const formData = new FormData()

    formData.append('file', blob, blob.name || 'image.png')

    return this.i().http.post('/api/v1/images', formData)
  }

}
