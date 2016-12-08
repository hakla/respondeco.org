import angular from 'angular';
import Config from './authentication.config';
import Login from './login';

export default angular
    .module('respondeco.admin.authentication', [])
    .config(Config)
    .controller('Login', Login)
    .name;
