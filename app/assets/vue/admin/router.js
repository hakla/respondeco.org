import Vue from 'vue';
import App from './app';

import VueRouter from 'vue-router';

import Organisations from './organisations/organisations';
import Login from './authentication/login';
import Logout from './authentication/logout';

Vue.use(VueRouter);

export const routes = [{
    path: '/',
    component: App,
    children: [{
        path: 'organisations',
        component: Organisations
    }]
}, {
    path: '/login',
    component: Login
}, {
    path: '/logout',
    component: Logout
}];

export const router = new VueRouter({
    routes
})
