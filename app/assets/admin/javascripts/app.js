import 'bootstrap/dist/css/bootstrap.min.css';
import 'gentelella/build/css/custom.min.css';

import angular from 'angular';
import uiRouter from 'angular-ui-router';

import Config from './admin.config';
import Organisations from './organisations/organisations.module';

angular
    .module('respondeco.admin', [
        // vendor dependencies
        uiRouter,

        Organisations
        // 'respondeco.admin.projects'
    ])
    .config(Config)
    .run((OrganisationsService) => {
        console.log(OrganisationsService);
    });
