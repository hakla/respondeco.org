import Vue from 'vue'

export class BaseService {
  constructor (url, params, action, options) {
    this.http = Vue.http
    this.resource = Vue.resource(url, params, action, options)
    this.endLoading = Vue.prototype.$endLoading
    this.startLoading = Vue.prototype.$startLoading
  }

  static all () {
    return this.i().resource.get()
  }

  static get (id) {
    return this.i().resource.get({id})
  }

  static i () {
    return Singleton.from(this).get()
  }

  static remove (id) {
    return this.i().resource.remove({id})
  }

  static save (obj) {
    return this.i().resource.save({}, obj)
  }

  static update (obj) {
    return this.i().resource.update({id: obj.id}, obj)
  }
}

export class Singleton {
  constructor (o) {
    this.o = o
  }

  get () {
    if (this.instance === undefined) {
      this.instance = new this.o()
    }

    return this.instance
  }

  static from (o) {
    if (!o.__singleton) {
      o.__singleton = new Singleton(o)
    }

    return o.__singleton
  }
}
