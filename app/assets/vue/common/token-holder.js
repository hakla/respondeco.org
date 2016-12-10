export default class TokenHolder {

    static empty () {
        localStorage.setItem("token", undefined);
    }

    static get (f) {
        return f(localStorage.getItem("token") || "");
    }

    static set (token) {
        localStorage.setItem("token", token);
    }

}
