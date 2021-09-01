// 页面用户权限表
const author: any = {
  // 项目列表(外层路由级别和routerName对应)
  homeList: {
    router: {
      id: 30100
    },
    // 添加按钮
    add: {
      id: 30101
    },
    // 禁用
    operate: {
      id: 30102
    },
    // build按钮
    build: {
      id: 30103
    },
    // deploy按钮
    deploy: {
      id: 30104
    },
    // 删除
    del: {
      id: 30105
    }
  },
  // 添加项目
  homeSave: {
    router: {
      id: 30101
    }
  },

  // 服务器器列表
  serverList: {
    router: {
      id: 30200
    },
    // 操作（删除）
    del: {
      id: 30201
    },
    edit: {
      id: 30202
    },
    add: {
      id: 30203
    }
  },
  // 编辑服务器
  serversEdit: {
    router: {
      id: 30202
    }
  },
  // 添加服务器
  serversSave: {
    router: {
      id: 30203
    }
  },
  // 配置中心
  confcenter: {
    // 添加按钮
    add: {
      id: 30301
    },
    // 操作（删除）
    operate: {
      id: 30302
    },
    // 编辑
    edit: {
      id: 30303
    }
  },
  // 调度中心
  schedulercenter: {
    // 添加按钮
    add: {
      id: 30401
    },
    // 操作（启用，停用，删除、禁用）
    operate: {
      id: 30402
    },
    // 编辑
    edit: {
      id: 30403
    }
  },
  // mock center
  mockserver: {
    router: {
      id: 30500
    },
    // 添加按钮
    add: {
      id: 30501
    },
    // 编辑
    edit: {
      id: 30502
    },
    // 操作（删除）
    del: {
      id: 30503
    }
  },
  // mqplus消息补偿
  rabbitmqplus: {
    // 重发、忽略
    operate: {
      id: 30601
    }
  },
  routingcenter: {
    // 添加按钮
    add: {
      id: 30701
    },
    // 编辑
    edit: {
      id: 30702
    },
    // 操作（删除）
    del: {
      id: 30703
    },
    // 操作(启动、禁止)
    operate: {
      id: 30704
    },
    // 插件添加
    plugAdd: {
      id: 30705
    },
    // 插件修改
    plugEdit: {
      id: 30706
    },
    // 插件删除
    plugDel: {
      id: 30707
    }
  }
};

export default author;
