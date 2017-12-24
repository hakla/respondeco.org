import { BaseService } from 'common/http/base-service'
import Store from 'app/store'

export default class Organisations extends BaseService {
  constructor () {
    super('accounts{/id}')
  }

  static updatePassword (passwordChangeRequest) {
    return this.i().http.post('user/change-password', passwordChangeRequest)
  }
}
