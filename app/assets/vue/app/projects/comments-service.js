let $resource;

export default {

  all() {
    return $resource.get();
  },

  get(id) {
    return $resource.get({id});
  },

  init(o) {
    $resource = o.$resource('comments{/id}');
  },

  remove(id) {
    return $resource.remove({id});
  },

  save(comment) {
    return $resource.save(null, comment);
  },

  update(id, comment) {
    return $resource.update({id}, comment);
  }

}
