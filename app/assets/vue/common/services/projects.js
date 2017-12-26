import { BaseService } from 'common/http/base-service'

export default class Projects extends BaseService {
  constructor () {
    super('projects{/id}')
  }
}
