let $resource;

export default {

  all() {
    return $resource.get();
  },

  get(id) {
    return $resource.get({id});
  },

  init(o) {
    $resource = o.$resource('projects{/id}');
  },

  remove(id) {
    return $resource.remove({id});
  },

  save(project) {
    return $resource.save(null, project);
  },

  update(id, project) {
    return $resource.update({id}, project);
  }

}
