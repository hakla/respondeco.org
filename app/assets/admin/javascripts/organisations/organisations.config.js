(() => {

    class Config {

        constructor($stateProvider) {

            $stateProvider
                .state('organisations', {
                    abstract: true,
                    resolve: {
                        organisations: (OrganisationsService) => OrganisationsService.all()
                    },
                    template: '<ui-view></ui-view>',
                    url: '/organisations'
                })
                .state('organisations.list', {
                    controller: 'Organisations',
                    controllerAs: 'vm',
                    templateUrl: '/assets/partials/admin/organisations/organisations.html',
                    url: ''
                })
                .state('organisations.details', {
                    controller: 'Organisation',
                    controllerAs: 'vm',
                    resolve: {
                        organisation: (organisations, $stateParams) => organisations.filter(org => org.id == $stateParams.id)[0]
                    },
                    templateUrl: '/assets/partials/admin/organisations/organisation.html',
                    url: '/{id}'
                });

        }

    }

    angular
        .module('respondeco.admin.organisations')
        .config(Config);

})();
