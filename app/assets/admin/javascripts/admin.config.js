(() => {

    class Config {

        constructor($urlRouterProvider) {

            $urlRouterProvider.otherwise('/organisations');

        }

    }

    angular
        .module('respondeco.admin')
        .config(Config);

})();
