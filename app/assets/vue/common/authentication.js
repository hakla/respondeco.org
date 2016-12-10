import Vue from 'vue';
import TokenHolder from './token-holder';

import StateObject from 'common/state-object';
import FunctionQueue from 'common/function-queue';

let authentication = undefined;
let activeUser = new StateObject();

export default class Authentication {

  constructor(router) {
    this.router = router;

    this.onError = new FunctionQueue();
    this.onLoggedIn = new FunctionQueue();
    this.onLoggedOut = new FunctionQueue();

    Vue.http.get('user').then(response => {
      activeUser.pushState(response.body);
    });
  }

  login(email, password, router) {
    Vue.http.post('auth/obtain-session', {
      email,
      password
    }).then(response => {
      TokenHolder.set(response.headers.get('x-access-token'));

      activeUser = response.body;

      this.onLoggedIn.apply(activeUser);
    }, response => {
      this.onError(response.body);
    });
  }

  logout() {
    Vue.http.post('auth/invalidate-session').then(reponse => {
      TokenHolder.empty();
      activeUser = undefined;

      this.onLoggedOut.apply();
    });
  }

  // Events

  error(cb) {
    this.onError.push(cb);

    return this;
  }

  loggedIn(cb) {
    this.onLoggedIn.push(cb);

    return this;
  }

  loggedOut(cb) {
    this.onLoggedOut.push(cb);

    return this;
  }

  // Static

  static activeUser() {
    return activeUser;
  }

  static get() {
    if (authentication == undefined) {
      throw new Error("Authentication isn't initialized!");
    }

    return authentication;
  }

  static init(router) {
    authentication = new Authentication(router);

    return authentication;
  }

}
