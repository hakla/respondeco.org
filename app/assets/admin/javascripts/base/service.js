export default class BaseService {

    constructor ($http) {
        this.$http = $http;
    }

    get (url, params) {
        return this.$http.get(url, params).then(result => result.data);
    }

    post (url, body, params) {
        return this.$http.post(url, body, { params }).then(result => result.data);
    }

}
