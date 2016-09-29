(() => {

    class Organisation {

        constructor(organisation) {
            this.organisation = organisation;
        }

    }

    angular
        .module('respondeco.admin.organisations')
        .controller('Organisation', Organisation);

})();
