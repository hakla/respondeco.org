(() => {

    function Organisations(organisations) {

        let vm = this;

        vm.organisations = organisations;

        return vm;

    }

    angular
        .module('respondeco.admin.organisations')
        .controller('Organisations', Organisations);

})();
