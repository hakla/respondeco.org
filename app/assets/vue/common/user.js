import Vue from 'vue';

export default class {

  static current(cb) {
    Vue.http.get('user').then(response => {
      cb(response.body);
    });
  }

}
