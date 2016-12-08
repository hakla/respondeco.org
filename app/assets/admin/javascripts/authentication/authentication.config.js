import Utils from '../base/utils.js';

export default function Config($stateProvider) {

    $stateProvider
        .state('login', {
            controller: 'Login',
            controllerAs: 'vm',
            template: Utils.template('authentication/login.html'),
            url: '/login'
        })
        .state('logout', {

        });

}
