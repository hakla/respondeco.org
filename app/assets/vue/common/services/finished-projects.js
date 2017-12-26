import { BaseService } from 'common/http/base-service'

export default class FinishedProjects extends BaseService {
  constructor () {
    super('finishedProjects{/id}')
  }
}
