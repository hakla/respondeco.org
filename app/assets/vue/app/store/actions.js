import * as types from './mutation-types'

export const organisation = ({ commit }, organisation) => {
  commit(types.ORGANISATION, organisation)
}
