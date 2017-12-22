import Vue from 'vue'

import StateObject from 'common/state-object'
import FunctionQueue from 'common/function-queue'

import TokenHolder from './token-holder'

let authentication = undefined
let activeUser = new StateObject()

export default class Authentication {

  constructor (router) {
    this.router = router

    this.onError = new FunctionQueue()
    this.onLoggedIn = new FunctionQueue()
    this.onLoggedOut = new FunctionQueue()

    this.refreshUser()
  }

  refreshUser () {
    Vue.http.get('user').then(response => {
      activeUser.pushState(response.body)
    }, response => {
      activeUser.pushState(undefined)
    });
  }

  login (user, password, router) {
    return Vue.http.post('auth/obtain-session', {
      user,
      password
    }).then(response => {
      TokenHolder.set(response.headers.get('x-access-token'))

      this.refreshUser()
      this.onLoggedIn.apply(response.body)
    }, response => {
      this.onError.apply(response.body)
    })
  }

  logout () {
    return Vue.http.post('auth/invalidate-session', {}).then(reponse => {
      TokenHolder.empty()

      activeUser.pushState(undefined)
      this.onLoggedOut.apply()
    }, error => {
      console.error(error)

      this.router.push('/')
    })
  }

  // Events
  error (cb) {
    this.onError.push(cb)

    return this
  }

  loggedIn (cb) {
    this.onLoggedIn.push(cb)

    return this
  }

  loggedOut (cb) {
    this.onLoggedOut.push(cb)

    return this
  }

  // Static
  static activeUser () {
    return activeUser
  }

  static get () {
    if (authentication == undefined) {
      throw new Error("Authentication isn't initialized!")
    }

    return authentication
  }

  static init (router) {
    authentication = new Authentication(router)

    return authentication
  }

}
