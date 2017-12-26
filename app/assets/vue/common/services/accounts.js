import { BaseService } from 'common/http/base-service'
import Store from 'app/store'

export default class Accounts extends BaseService {
  constructor () {
    super('accounts{/id}')
  }

  static updatePassword (passwordChangeRequest) {
    return this.i().http.post('user/change-password', passwordChangeRequest)
  }
}

export class AdminAccounts extends BaseService {
  constructor () {
    super('admin/accounts{/id}')
  }
}
