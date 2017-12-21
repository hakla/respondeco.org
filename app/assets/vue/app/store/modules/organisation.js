import * as types from '../mutation-types'

// initial state
const state = {
  all: [],
  current: {
    id: 'empty'
  },
  finishedProjects: [],
  projects: []
}

// getters
const getters = {
  finishedProjects: state => state.finishedProjects,
  organisation: state => state.current,
  organisations: state => state.all,
  projects: state => state.projects
}

// actions
const actions = {
  all ({ commit}, value) {
    commit(types.ORGANISATION_ALL, value)
  },

  current ({ commit }, value) {
    commit(types.ORGANISATION_CURRENT, value)
  },

  finishedProjects ({ commit }, value) {
    commit(types.ORGANISATION_FINISHED_PROJECTS, value)
  },

  projects ({ commit }, value) {
    commit(types.ORGANISATION_PROJECTS, value)
  }
}

// mutations
const mutations = {
  [types.ORGANISATION_ALL] (state, all) {
    state.all = all
  },

  [types.ORGANISATION_CURRENT] (state, value) {
    state.current = value
  },

  [types.ORGANISATION_FINISHED_PROJECTS] (state, value) {
    state.finishedProjects = value
  },

  [types.ORGANISATION_PROJECTS] (state, value) {
    state.projects = value
  }
}

export default {
  state,
  getters,
  actions,
  mutations
}
