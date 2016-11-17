export default class Utils {

    static template(path) {
        return require(`../../templates/${path}`);
    }

    static head(array) {
        if (array && array.length > 0) return array[0];
        else return undefined;
    }

}
