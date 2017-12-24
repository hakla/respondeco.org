import { BaseService } from 'common/http/base-service'

export default class Organisations extends BaseService {
  constructor () {
    super('organisations{/id}')
  }
}
