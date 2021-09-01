<template>
  <div class="schedulerplus">
    <div class="main">
      <div class="main-table">
        <el-table :data="routesPoolData" empty-text="暂无相关数据" row-key="id">
          <el-table-column type="index" label="序号" width="80"></el-table-column>
          <el-table-column prop="outPath" min-width="100" label="外部路径">
            <template slot-scope="scope">
              <span style="margin-left: 10px">{{ scope.row.outPath }}</span>
            </template>
          </el-table-column>
          <!-- <el-table-column prop="innerPath" label="内部路径"></el-table-column> -->
          <el-table-column prop="plugins" label="附属插件" width="120">
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
          <el-table-column prop="kongRoutesName" label="路由名称"></el-table-column>
          <el-table-column prop="updateBy" label="修改人"></el-table-column>
          <el-table-column prop="updateTime" label="修改时间" width="165">
            <template slot-scope="scope">{{ (scope.row.updateTime * 1000) | formatDate }}</template>
          </el-table-column>

          <el-table-column fixed="right" label="操作" width="170" align="center">
            <template slot-scope="scope">
              <el-button size="small" type="text" :style="{ color: scope.row.enabled ? '#f56c6c' : '#409EFF' }" @click="handleStart(scope.row.routeId, !scope.row.enabled)" v-author.disabled="$author && $author.routingcenter.operate">{{ scope.row.enabled ? "禁用" : "启动" }}</el-button>
              <el-button type="text" size="small" @click="handleEdit(scope.$index, scope.row)" v-author.disabled="$author && $author.routingcenter.edit">修改</el-button>
              <el-button type="text" size="small" @click="handleDelete(scope.row.routeId, scope.row)" v-author.disabled="$author && $author.routingcenter.del">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!--        <router-plug-list-->
        <!--          :gatewayType="routesDetail.gatewayType"-->
        <!--          :routesPlugin="routesPlugin"-->
        <!--          :routesDetail="routesDetail"-->
        <!--          @updatePlug="updatePlug"-->
        <!--          ref="routerPlugList"-->
        <!--        ></router-plug-list>-->
        <add-plug :gatewayType="routesDetail.gatewayType" :routesDetail="routesDetail" :routesPlugin="routesPlugin" @updatePlug="updatePlug" ref="routerPlugList"></add-plug>

        <router-pool-edit @upload="uploadData" :routesPlugin="routesPlugin" :serviceId="serviceId" ref="routerPoolEdit"></router-pool-edit>
      </div>

      <div class="pagination">
        <el-pagination :page-size="pageSize" :current-page="pageNo" hide-on-single-page layout="prev, pager, next" :total="total" @current-change="handleCurrentPageChange"></el-pagination>
      </div>
    </div>
  </div>
</template>
<script lang="ts">
import { Component, Vue, Prop, Watch } from "vue-property-decorator";
import { routesList, routesPlugin, servicesEnabled, servicesDel, servicesRoutesSave, pluginDelete, addPlugForRoute, delPlugForRoute } from "@/server/routeplus";
import RouterEdit from "@/components/RoutingCenter/RouterEdit.vue";
import RouterPlugList from "@/components/RoutingCenter/plug-in/index-list.vue";
import RouterPoolEdit from "@/components/RoutingCenter/RouterPoolEdit.vue";
import RouteServicesEdit from "@/components/RoutingCenter/RouteServicesEdit.vue";
import AddPlug from "@/components/RoutingCenter/plug-in/add-plug.vue";

@Component({
  components: {
    RouterEdit,
    AddPlug,
    RouterPoolEdit
  }
})
export default class RoutingCenter extends Vue {
  @Prop() public routesPoolData!: RoutesPool;
  @Prop() public routesPlugin!: PluginsType[];
  @Prop() public serviceId!: number;
  @Prop() public routesDetail!: RoutesDetail;
  private updateRoutesPool!: RoutesPool;

  private handleEdit(index: number, row: RoutesPool): void {
    (this.$refs.routerPoolEdit as RouterPoolEdit).openRouterPool(row);
  }

  private handleDelete(routeId: number, row: RoutesPool): void {
    if (row.enabled) {
      this.$message.error("请先禁用再关闭!");
      return;
    }
    this.$confirm("此操作将删除配置信息, 是否继续?", "警告", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      confirmButtonClass: "el-button--danger",
      cancelButtonClass: "el-button--primary",
      type: "error"
    }).then(() => {
      servicesDel({
        routes: {
          id: routeId
        }
      }).then(
        (res: any) => {
          this.$emit("uploadTable");
          this.$message.success(res.errmsg);
        },
        (err: any) => {
          this.$message.error(err.errmsg);
        }
      );
    });
  }

  private uploadData(row: RouteServices): void {
    this.$emit("editTable", row);
  }

  private handleStart(routeId: number, enabled: boolean): void {
    this.$confirm(`此操作将${enabled ? "启动" : "禁用"}, 是否继续?`, "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      confirmButtonClass: "el-button--danger",
      cancelButtonClass: "el-button--primary"
    })
      .then(() => {
        servicesEnabled({
          routes: {
            id: routeId,
            enabled
          }
        }).then(
          (res: any) => {
            this.$emit("uploadPlug", routeId, enabled);
            this.$message.success(`子节点${enabled ? "启动" : "禁用"}成功`);
          },
          (err: any) => {
            this.$message.error(err.errmsg);
          }
        );
      })
      .catch(err => {
        console.log("已取消");
      });
  }

  // ========================================================关于标签=======
  // 关于标签-新增
  private addRoutingTag(row: RoutesPool, plugins: Plugins[]): void {
    // if (!row.enabled) {
    //   this.$message.error("请先启动再添加插件!");
    //   return;
    // }
    // if (this.routesPlugin.length <= plugins.length) {
    //   this.$message.error("没有剩余插件可以使用");
    //   return;
    // }
    // this.updateRoutesPool = row;
    (this.$refs.routerPlugList as RouterPlugList).openRoutring(row);
  }

  // 关于标签-修改
  private editRoutingTag(plug: PluginsType, row: RoutesPool): void {
    if (!row.enabled) {
      this.$message.error("请先启用外部路径!");
      return;
    }
    // @ts-ignore
    if (!this.$isAuthor(this.$author.routingcenter.plugEdit.id)) {
      this.$message.error("您没有相关权限!");
      return;
    }
    this.updateRoutesPool = row;
    // this.updatePluginsType = plug;
    (this.$refs.routerPlugList as RouterPlugList).openRoutring(plug);
  }

  // 更新插件
  private updatePlug(params: any): void {
    // const data: any = {
    //   serviceId: this.serviceId,
    //   routesPool: {
    //     plugins: [plugins],
    //     routeId: this.updateRoutesPool.routeId
    //   }
    // };
    // servicesRoutesSave(data).then(
    //   (res: any) => {
    //     this.$message.success(res.errmsg);
    //     this.$emit("upload");
    //   },
    //   (err: any) => {
    //     this.$message.error(err.errmsg);
    //   }
    // );
    addPlugForRoute(params)
      .then((res: any) => {
        this.$message.success(res.errmsg);
        this.$emit("upload");
      })
      .catch(err => {
        this.$message.error(err.errmsg);
      });
  }

  // 删除插件
  private closeRoutingTag(plug: PluginsType, row: RoutesPool): void {
    this.$confirm("此操作将删除插件, 是否继续?", "警告", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      confirmButtonClass: "el-button--danger",
      cancelButtonClass: "el-button--primary",
      type: "error"
    }).then(() => {
      // @ts-ignore
      delPlugForRoute(plug.id).then(() => {
        this.$emit("delPlug", plug.id);
        this.$message.success("删除成功");
      });
    });
    // // @ts-ignore
    // if (!this.$isAuthor(this.$author.routingcenter.plugDel.id)) {
    //   this.$message.error("您没有相关权限!");
    //   return;
    // }
    // if (!row.enabled) {
    //   this.$message.error("请先启用外部路径!");
    //   return;
    // }
    // if (plug.enabled) {
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
    //   pluginDelete("gateway", plug.id).then(
    //     (res: any) => {
    //       this.$emit("delPlug", plug.id);
    //       this.$message.success(res.errmsg);
    //     },
    //     (err: any) => {
    //       this.$message.error(err.errmsg);
    //     }
    //   );
    // });
  }
}
</script>
<style lang="scss" scoped>
.schedulerplus {
  width: 100%;
  .app-title {
    margin: 20px 0;
    font-size: 20px;
  }
  .add-btn {
    margin-top: 20px;
  }
  .terminal-list {
    margin: 15px 0;
    display: flex;
    .terminal-title {
      font-size: 15px;
      line-height: 35px;
      width: 90px;
      margin-right: 10px;
    }
  }

  .input-new-tag {
    width: 90px;
    margin-left: 15px;
    vertical-align: bottom;
  }
}
</style>

<style lang="scss">
.el-tag + .el-tag {
  margin-left: 20px;
}
</style>
