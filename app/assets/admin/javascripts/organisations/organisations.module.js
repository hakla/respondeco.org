import angular from 'angular';
import Config from './organisations.config';
import Organisation from './organisation';
import Organisations from './organisations';
import OrganisationsService from './organisations.service';

export default angular
    .module('respondeco.admin.organisations', [])
    .config(Config)
    .controller('Organisation', Organisation)
    .controller('Organisations', Organisations)
    .service('OrganisationsService', OrganisationsService)
    .name;
