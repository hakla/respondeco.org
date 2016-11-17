import BaseService from '../base/service';

export default class OrganisationsService extends BaseService {

    constructor($http) {
        super($http);
    }

    all() {
        return super.get('/organisations');
    }

    get(id) {
        return super.get('/organisations/:id', { id });
    }

    post(organisation) {
        return super.post('/organisations/:id', organisation, { id: organisation.id })
    }

}
