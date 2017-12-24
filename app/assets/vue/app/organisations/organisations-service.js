import { BaseService, Singleton } from 'common/http/base-service'

export default class OrganisationsService extends BaseService {
  constructor () {
    super('organisations{/id}')
  }
}
