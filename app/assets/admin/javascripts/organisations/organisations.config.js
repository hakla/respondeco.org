import Utils from '../base/utils.js';

export default function Config($stateProvider) {

    $stateProvider
        .state('organisations', {
            abstract: true,
            resolve: {
                organisations: (OrganisationsService) => {
                    return OrganisationsService.all();
                }
            },
            template: '<ui-view></ui-view>',
            url: '/organisations'
        })
        .state('organisations.list', {
            controller: 'Organisations',
            controllerAs: 'vm',
            template: Utils.template('organisations/organisations.html'),
            url: ''
        })
        .state('organisations.details', {
            controller: 'Organisation',
            controllerAs: 'vm',
            resolve: {
                organisation: (organisations, $stateParams) => Utils.head(organisations.filter(org => org.id == $stateParams.id))
            },
            template: Utils.template('organisations/organisation.html'),
            url: '/{id}'
        });

}
