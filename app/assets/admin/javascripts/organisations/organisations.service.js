(() => {

    class OrganisationsService {

        constructor($http) {
            this.$http = $http;
        }

        all() {
            return this.$http.get('/organizations').then(result => result.data);
        }

    }

    angular
        .module('respondeco.admin.organisations')
        .service('OrganisationsService', OrganisationsService);

})();
