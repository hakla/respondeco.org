import 'bootstrap/dist/css/bootstrap.min.css';
import 'gentelella/build/css/custom.min.css';

import angular from 'angular';
import uiRouter from 'angular-ui-router';

import Config from './admin.config';
import Organisations from './organisations/organisations.module';
import Authentication from './authentication/authentication.module';

angular
    .module('respondeco.admin', [
        // vendor dependencies
        uiRouter,

        Authentication,
        Organisations
        // 'respondeco.admin.projects'
    ])
    .config(Config);
