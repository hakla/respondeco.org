let $resource;

export default {

  all() {
    return $resource.get();
  },

  get(id) {
    return $resource.get({id});
  },

  init(o) {
    $resource = o.$resource('finishedProjects{/id}');
  },

  remove(id) {
    return $resource.remove({
      id
    });
  },

  save(historyEntry) {
    return $resource.save(historyEntry);
  },

  update(id, historyEntry) {
    return $resource.update({ id: id }, historyEntry);
  }

}
