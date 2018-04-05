import { BaseService } from 'common/http/base-service'

export default class FinishedProjects extends BaseService {
  constructor () {
    super('finishedProjects{/id}')
  }

  static byProject (id) {
    return this.i().http.get(`/api/v1/projects/${id}/finished`)
  }
}
