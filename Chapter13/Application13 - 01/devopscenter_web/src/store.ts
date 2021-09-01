import Vue from 'vue';
import Vuex, { StoreOptions } from 'vuex';

Vue.use(Vuex);


const store: StoreOptions<RootState> = {
  state: {
    kongStatus: 'ING', // 网关状态 ING 加载中; SUCESS 成功; FAILURE 失败
    isCollapse: false,
    screen: -1
  },
  mutations: {
    setKongStatus: (state, data) => state.kongStatus = data,
    SET_COLLAPSE: (state, data) => {
      state.isCollapse = !state.isCollapse;
    },
    SET_SCREEN: (state, screen) => {
      state.screen = screen;
    }
  },
  getters: {
    kongStatus: state => state.kongStatus,
    isCollapse: state => state.isCollapse,
    keyCollapse: (state, getters) => getters.screen > 1 ? getters.isCollapse : false,
    screen: state => state.screen
  },
  actions: {

  }
};

export default new Vuex.Store(store);
