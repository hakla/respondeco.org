export default class {

  constructor() {
    // Queue of functions to be executed
    this.queue = [];
  }

  /**
   * Call the function queue with a given value
   * @param value Each function in the queue will be called with this value
   */
  apply(value) {
    this.queue.forEach(f => f(value))
  };

  /**
   * Push a new callback function onto the queue
   * @param  {Function} cb New callback to be pushed onto the queue
   * @return {[type]}      [description]
   */
  push (cb) {
    this.queue.push(cb);

    return this;
  };

};
