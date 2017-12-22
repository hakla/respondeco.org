import * as types from '../mutation-types'

// initial state
const state = {
  activeUser: undefined
}

// getters
const getters = {
  activeUser: state => state.activeUser
}

// actions
const actions = {
  activeUser ({ commit }, value) {
    commit(types.USER_ACTIVE_USER, value)
  }
}

// mutations
const mutations = {
  [types.USER_ACTIVE_USER] (state, value) {
    state.activeUser = value
  }
}

export default {
  state,
  getters,
  actions,
  mutations
}
