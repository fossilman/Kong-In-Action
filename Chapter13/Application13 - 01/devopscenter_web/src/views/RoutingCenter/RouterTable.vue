<template>
  <div class="main">
    <div class="topbar">
      <el-button class="add-btn" type="primary" @click="handleAdd()" v-show="ready" v-author.disabled="$author && $author.routingcenter.add">添加</el-button>
    </div>
    <div class="main-table">
      <el-table :data="routeServicesList" v-loading="!ready" empty-text="暂无相关数据" row-key="serviceId" :expand-row-keys="expandKeys" @expand-change="routesPluginListShow">
        <el-table-column type="expand">
          <template>
            <routes-pool-table
              v-loading="!poolReady"
              :routesPlugin="routesPlugin"
              :serviceId="servicesId"
              :routesPoolData="routesPluginList"
              :routesDetail="routesDetail"
              @uploadTable="getServicesList"
              @editTable="editTable"
              @upload="updateRoutesPool"
              @uploadPlug="uploadPlug"
              @delPlug="delPlug"
            ></routes-pool-table>
          </template>
        </el-table-column>
        <el-table-column type="index" label="序号" width="80"></el-table-column>
        <el-table-column prop="path" label="内部路径"></el-table-column>
        <el-table-column prop="routesPool" label="外部网关类型">
          <template slot-scope="scope">
            <el-row>
              <el-col :span="24" v-for="(item, index) in scope.row.routes" :key="index">
                <el-tag>{{ item.routeType }}</el-tag>
              </el-col>
            </el-row>
          </template>
        </el-table-column>
        <el-table-column prop="plugins" label="附属插件" width="140">
          <template slot-scope="scope">
            <el-row>
              <el-col :span="24" v-for="(tag, index) in scope.row.plugins" :key="index">
                <el-tag class="el-hand" closable :disable-transitions="true" :type="tag.enabled ? 'success' : 'danger'" @close="closeRoutingTag(tag, scope.row)" @click="editRoutingTag(tag, scope.row)" v-author.disabled="$author && $author.routingcenter.edit">
                  {{ tag.pluginName }}
                </el-tag>
              </el-col>
              <el-col :span="24">
                <el-button class="button-new-tag" size="small" @click="addRoutingTag(scope.row, scope.row.plugins)" v-author.disabled="$author && $author.routingcenter.plugAdd">+ 添加插件</el-button>
              </el-col>
            </el-row>
          </template>
        </el-table-column>
        <el-table-column prop="kongServicesName" label="网关应用名称"></el-table-column>
        <el-table-column prop="updateBy" label="修改人"></el-table-column>
        <el-table-column prop="updateTime" label="修改时间" width="170">
          <template slot-scope="scope">{{ (scope.row.updateTime * 1000) | formatDate }}</template>
        </el-table-column>

        <el-table-column fixed="right" label="操作" width="155" align="center">
          <template slot-scope="scope">
            <el-button size="small" type="text" :style="{ color: scope.row.enabled ? '#f56c6c' : '#409EFF' }" @click="handleStart(scope.row.serviceId, !scope.row.enabled)" v-author.disabled="$author && $author.routingcenter.operate">{{ scope.row.enabled ? "禁用" : "启动" }}</el-button>
            <el-button type="text" size="small" @click="handleEdit(scope.$index, scope.row)" v-author.disabled="$author && $author.routingcenter.edit">修改</el-button>
            <el-button type="text" size="small" @click="handleDelete(scope.row.serviceId, scope.row)" v-author.disabled="$author && $author.routingcenter.edit">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <router-edit @upload="getServicesList" :routesDetail="routesDetail" ref="routerEdit"></router-edit>
    </div>

    <div class="pagination">
      <el-pagination :page-size="pageSize" :current-page="pageNo" hide-on-single-page layout="prev, pager, next" :total="total" @current-change="handleCurrentPageChange"></el-pagination>
    </div>

    <!--    <router-plug-list-->
    <!--      :gatewayType="routesDetail.gatewayType"-->
    <!--      :routesDetail="routesDetail"-->
    <!--      :routesPlugin="routesPlugin"-->
    <!--      @updatePlug="updatePlug"-->
    <!--      ref="RouterPlugList"-->
    <!--    ></router-plug-list>-->
    <add-plug :gatewayType="routesDetail.gatewayType" :routesDetail="routesDetail" :routesPlugin="routesPlugin" @updatePlug="updatePlug" ref="RouterPlugList"></add-plug>
  </div>
</template>
<script lang="ts">
import { Component, Vue, Ref, Prop, Watch } from "vue-property-decorator";
import { ElForm } from "element-ui/types/form";
import { servicesList, servicesRoutesList, servicesSaveOrUpdate, servicesEnabled, checkEnabled, servicesDel, pluginDelete, addPlugForApp, delPlugForApp } from "@/server/routeplus";
import RouterEdit from "@/components/RoutingCenter/RouterEdit.vue";
import RoutesPoolTable from "@/components/RoutingCenter/RoutesPoolTable.vue";
import RouterPlugList from "@/components/RoutingCenter/plug-in/index-list.vue";
import AddPlug from "@/components/RoutingCenter/plug-in/add-plug.vue";
import { deepClone } from "@/common/js/deepClone";
@Component({
  components: {
    RouterEdit,
    RoutesPoolTable,
    AddPlug
  }
})
export default class RouterTable extends Vue {
  @Prop() public routesDetail!: RoutesDetail;
  @Prop() public routesPlugin!: PluginsType[];

  private total: number = 0;
  private pageSize: number = 10;
  private pageNo: number = 1;
  private ready: boolean = false;
  private poolReady: boolean = false;
  private routeServicesList: RouteServices[] = [];
  private addJobDialogVisibleFlag: boolean = false;
  private editJobDialogVisibleFlag: boolean = false;
  private expandKeys: number[] = [];
  private whenRouteServices!: RouteServices;
  private updateRouteServices!: RouteServices;
  private servicesId!: number | undefined;
  private updatePluginsType!: PluginsType;
  private routesPluginList!: PluginsType[];

  private mounted(): void {
    this.init();
  }

  private init(): void {
    this.pageNo = 1;
    this.getServicesList();
  }

  private getServicesList(): void {
    servicesList(this.$route.params.id, this.pageNo, this.pageSize).then(
      (res: any) => {
        this.ready = true;
        this.routeServicesList = deepClone(res.retval.services);
        this.total = res.retval.total;
        this.expandKeys = [];
      },
      (err: any) => {
        this.ready = true;
        this.routeServicesList = [];
        this.$message.error(err.errmsg);
      }
    );
  }

  private handleCurrentPageChange(page: number): void {
    this.pageNo = page;
    this.getServicesList();
  }

  // 增加
  private handleAdd(): void {
    (this.$refs.routerEdit as RouterEdit).openRoutring({});
  }

  // 修改
  private handleEdit(index: number, row: RouteServices): void {
    (this.$refs.routerEdit as RouterEdit).openRoutring(row);
  }

  // 禁用
  private handleStart(serviceId: number, enabled: boolean): void {
    const data: any = this.routeServicesList.find(c => c.serviceId === serviceId);
    if (data) {
      data.enabled = enabled;
      if (data.plugins) {
        if (Array.isArray(data.plugins)) {
          data.plugins.forEach((c: Plugins) => {
            // @ts-ignore
            c.enabled = enabled;
          });
        } else {
          data.plugins.enabled = enabled;
        }
      }
    }
    // this.$confirm(`此操作将${enabled ? "启动" : "禁用"}, 是否继续?`, "提示", {
    //   confirmButtonText: "确定",
    //   cancelButtonText: "取消",
    //   confirmButtonClass: "el-button--danger",
    //   cancelButtonClass: "el-button--primary"
    // })
    //   .then(() => {
    //     servicesEnabled({
    //       services: {
    //         id: serviceId,
    //         enabled
    //       }
    //     }).then(
    //       (res: any) => {
    //         this.routeServicesList.forEach(
    //           (item: RouteServices, index: number) => {
    //             if (item.serviceId === serviceId) {
    //               this.routeServicesList[index].enabled = enabled;
    //               // @ts-ignore
    //               this.routeServicesList[index].plugins.forEach(
    //                 (plugin: Plugins) => {
    //                   plugin.enabled = enabled;
    //                 }
    //               );
    //             }
    //           }
    //         );
    //         if (this.routesPluginList) {
    //           this.routesPluginList.forEach(
    //             (item: RoutesPool, index: number) => {
    //               this.routesPluginList[index].enabled = enabled;
    //               // @ts-ignore
    //               this.routesPluginList[index].plugins.forEach(
    //                 (plugin: Plugins, key: number) => {
    //                   // @ts-ignore
    //                   plugin.enabled = enabled;
    //                 }
    //               );
    //             }
    //           );
    //         }
    //         this.$message.success(`子节点${enabled ? "启动" : "禁用"}成功`);
    //       },
    //       (err: any) => {
    //         this.$message.error(err.errmsg);
    //       }
    //     );
    //   })
    //   .catch(err => {
    //     console.log("已取消");
    //   });
  }

  // 删除
  private handleDelete(serviceId: number, data: RouteServices): void {
    const index: number = this.routeServicesList.findIndex(c => c.serviceId === serviceId);
    if (index >= 0) {
      this.routeServicesList.splice(index, 1);
    }
    // if (data.enabled) {
    //   this.$message.error("请先禁用再关闭!");
    //   return;
    // }
    // this.$confirm("此操作将删除, 是否继续?", "警告", {
    //   confirmButtonText: "确定",
    //   cancelButtonText: "取消",
    //   confirmButtonClass: "el-button--danger",
    //   cancelButtonClass: "el-button--primary",
    //   type: "error"
    // })
    //   .then(() => {
    //     checkEnabled(serviceId).then((checkRes: any) => {
    //       if (checkRes.retval.hasEnabled) {
    //         this.$message.error("请禁用子集网关层");
    //         return;
    //       }
    //       this.postServicesDel(serviceId, data);
    //     });
    //   })
    //   .catch(err => {
    //     console.log("已取消");
    //   });
  }

  private postServicesDel(serviceId: number, data: RouteServices): void {
    this.ready = false;
    servicesDel({
      services: {
        id: serviceId
      }
    }).then(
      (res: any) => {
        this.ready = true;
        this.routeServicesList.forEach((item: RouteServices, index: number) => {
          if (item.serviceId === serviceId) {
            this.routeServicesList.splice(index, 1);
          }
        });
        this.$message.success(res.errmsg);
      },
      (err: any) => {
        this.ready = true;
        this.$message.error(err.errmsg);
      }
    );
  }

  // 删除插件
  private closeRoutingTag(row: PluginsType, project: RouteServices): void {
    this.$confirm("此操作将删除插件, 是否继续?", "警告", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      confirmButtonClass: "el-button--danger",
      cancelButtonClass: "el-button--primary",
      type: "error"
    }).then(() => {
      // @ts-ignore
      delPlugForApp(row.id).then(
        () => {
          this.getServicesList();
          this.$message.success("删除成功");
        },
        (err: any) => {
          this.$message.error(err.errmsg);
        }
      );
    });
    // @ts-ignore
    // if (!this.$isAuthor(this.$author.routingcenter.plugDel.id)) {
    //   this.$message.error("您没有相关权限!");
    //   return;
    // }
    // if (!project.enabled) {
    //   this.$message.error("请先启用内部路径!");
    //   return;
    // }
    // if (row.enabled) {
    //   this.$message.error("请先禁用再关闭!");
    //   return;
    // }
    // this.$confirm("此操作将删除插件, 是否继续?", "警告", {
    //   confirmButtonText: "确定",
    //   cancelButtonText: "取消",
    //   confirmButtonClass: "el-button--danger",
    //   cancelButtonClass: "el-button--primary",
    //   type: "error"
    // }).then(() => {
    //   pluginDelete("application", row.id).then(
    //     res => {
    //       this.getServicesList();
    //       this.$message.success("删除成功");
    //     },
    //     (err: any) => {
    //       this.$message.error(err.errmsg);
    //     }
    //   );
    // });
  }

  // ========================================================关于子表操作=======
  // 子列表展开
  private routesPluginListShow(row: RouteServices): void {
    this.whenRouteServices = row;
    this.servicesId = row.serviceId;
    // @ts-ignore
    if (this.expandKeys.indexOf(row.serviceId) >= 0) {
      this.expandKeys.shift();
      return;
    }
    this.getRouteServicesList(row);
  }

  // 更新数据
  private updateRoutesPool(): void {
    this.getRouteServicesList(this.whenRouteServices);
  }

  // 编辑数据
  private editTable(row: RouteServices): void {
    // @ts-ignore
    const routeId: number = row.routesPool.routeId;
    console.log(row);
    this.routesPluginList.forEach((item: RoutesPool, index: number) => {
      if (item.routeId === routeId) {
        // @ts-ignore
        this.routesPluginList[index].outPath = row.routesPool.outPath;
        // @ts-ignore
        this.routesPluginList[index].remark = row.routesPool.remark;
      }
    });
  }

  // 路由网络层子列表
  private getRouteServicesList(row: RouteServices): void {
    this.poolReady = false;
    // @ts-ignore
    servicesRoutesList(row.serviceId)
      .then((res: any) => {
        this.routesPluginList = res.retval.routes;
        this.poolReady = true;
        this.expandKeys.shift();
        // @ts-ignore
        this.expandKeys.push(row.serviceId);
      })
      .catch(() => {
        this.routesPluginList = [];
        this.poolReady = true;
      });
  }

  // 模拟网关更新插件的状态
  private uploadPlug(routeId: number, enabled: boolean): void {
    this.routesPluginList.forEach((item: RoutesPool, index: number) => {
      if (item.routeId === routeId) {
        this.routesPluginList[index].enabled = enabled;
        // @ts-ignore
        this.routesPluginList[index].plugins.forEach((plugin: Plugins, key: number) => {
          // @ts-ignore
          plugin.enabled = enabled;
        });
      }
    });
  }

  // 模拟删除插件
  private delPlug(id: number): void {
    this.getServicesList();
    // this.routesPluginList.forEach((item: RoutesPool, index: number) => {
    //   // @ts-ignore
    //   item.plugins.forEach((plugin: Plugins, key: number) => {
    //     if (plugin.id === id) {
    //       // @ts-ignore
    //       this.routesPluginList[index].plugins.splice(key, 1);
    //     }
    //   });
    // });
  }
  // ========================================================关于标签=======
  // 关于标签-新增
  private addRoutingTag(row: RouteServices, plugins: Plugins[]): void {
    // if (!row.enabled) {
    //   this.$message.error("请先启动再添加插件!");
    //   return;
    // }
    // if (this.routesPlugin.length <= plugins.length) {
    //   this.$message.error("没有剩余插件可以使用");
    //   return;
    // }
    this.updateRouteServices = row;
    (this.$refs.RouterPlugList as RouterPlugList).openRoutring(row);
  }

  // 关于标签-修改
  private editRoutingTag(plug: PluginsType, row: RouteServices): void {
    // @ts-ignore
    if (!this.$isAuthor(this.$author.routingcenter.plugEdit.id)) {
      this.$message.error("您没有相关权限!");
      return;
    }
    if (!row.enabled) {
      this.$message.error("请先启用内部路径!");
      return;
    }
    this.updateRouteServices = row;
    this.updatePluginsType = plug;
    (this.$refs.RouterPlugList as RouterPlugList).openRoutring(plug);
  }

  private updatePlug(params: any): void {
    // const data: any = {
    //   applicationId: Number(this.routesDetail.applicationId),
    //   applicationName: this.routesDetail.applicationName,
    //   services: {
    //     plugins: [plugins],
    //     serviceId: this.updateRouteServices.serviceId
    //   }
    // };
    // servicesSaveOrUpdate(data).then(
    //   (res: any) => {
    //     this.$message.success(res.errmsg);
    //     this.getServicesList();
    //   },
    //   (err: any) => {
    //     this.$message.error(err.errmsg);
    //   }
    // );
    addPlugForApp(params)
      .then(() => {
        this.$message.success("添加插件成功");
        this.getServicesList();
      })
      .catch(err => {
        console.log(err);
        // this.$message.error(err.errmsg);
        // throw err;
      });
  }
}
</script>
<style lang="scss" scoped></style>
