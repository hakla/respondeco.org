let $resource;

export default {

  all() {
    return $resource.get();
  },

  get(id) {
    return $resource.get({id});
  },

  init(o) {
    $resource = o.$resource('organisations{/id}');
  },

  remove(id) {
    return $resource.remove({id});
  },

  projects () {
    console.log($resource)
  }

}
