import FunctionQueue from './function-queue'

export default class StateObject {

  constructor () {
    this.queue = new FunctionQueue()
    this.state = undefined
  }

  pushState (state) {
    this.state = state

    this.queue.apply(state)
  }

  then (cb) {
    this.queue.push(cb)

    if (this.state !== undefined) {
      cb(this.state)
    }

    return this
  }

}
