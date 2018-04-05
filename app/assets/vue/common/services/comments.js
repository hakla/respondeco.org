import { BaseService } from '../http/base-service'

export default class Comments extends BaseService {
  constructor () {
    super('comments{/id}')
  }

  static byProject (id) {
    return this.i().http.get(`/api/v1/projects/${id}/comments`)
  }

  static byFinishedProject (id) {
    return this.i().http.get(`/api/v1/finishedProjects/${id}/comments`)
  }

  static pin (id) {
    return this.i().http.post(`/api/v1/comments/${id}/pin`)
  }

  static save (obj, commentType, linkId) {
    return this.i().resource.save({
      commentType,
      linkId
    }, obj)
  }

  static unpin (id) {
    return this.i().http.delete(`/api/v1/comments/${id}/pin`)
  }

  static update (obj, commentType, linkId) {
    return this.i().resource.update({
      id: obj.id,
      commentType,
      linkId
    }, obj)
  }

}
