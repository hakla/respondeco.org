export default class TokenHolder {

    static empty () {
        localStorage.setItem('token', undefined);
    }

    static get (f) {
        return f(this.sync());
    }

    static set (token) {
        localStorage.setItem('token', token);
    }

    static sync () {
      return localStorage.getItem('token' || '')
    }

}
