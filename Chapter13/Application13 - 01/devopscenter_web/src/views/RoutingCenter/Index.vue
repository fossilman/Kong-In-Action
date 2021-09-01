<template>
  <div class="schedulerplus">
<!--    <section>-->
<!--      <div class="app-title">应用层</div>-->
<!--      <el-row>-->
<!--        <el-tag-->
<!--          class="el-hand tag-list"-->
<!--          :key="index"-->
<!--          v-for="(tag,index) in applicationTags"-->
<!--          closable-->
<!--          :disable-transitions="true"-->
<!--          :type="tag.enabled?'success':'danger'"-->
<!--          @close="closeServicesTag(tag,index)"-->
<!--          @click="editRoutingTag(tag,'application','all')"-->
<!--        >-->
<!--          <span>{{tag.pluginAttr.quantity}}/{{tag.pluginAttr.period.substring(0,1)}}</span>-->
<!--        </el-tag>-->
<!--        <el-button-->
<!--          class="button-new-tag"-->
<!--          size="small"-->
<!--          v-author.disabled="$author && $author.routingcenter.plugAdd"-->
<!--          @click="addRoutingTag('application','all',applicationTags)"-->
<!--        >+ 添加插件</el-button>-->
<!--      </el-row>-->
<!--    </section>-->

<!--    <section>-->
<!--      <div class="app-title">网关层</div>-->
<!--      <el-row class="terminal-list" v-for="(item,index) in gatewayTags" :key="index">-->
<!--        <span class="terminal-title">{{item.type}}</span>-->
<!--        <el-tag-->
<!--          class="el-hand tag-list"-->
<!--          :key="tag"-->
<!--          v-for="tag in item.plugins"-->
<!--          closable-->
<!--          :disable-transitions="true"-->
<!--          :type="tag.enabled?'success':'danger'"-->
<!--          @close="closeRoutingTag(tag)"-->
<!--          @click="editRoutingTag(tag,'gateway',item.type)"-->
<!--          v-author.disabled="$author && $author.routingcenter.edit"-->
<!--        >-->
<!--          <span>{{tag.pluginAttr.quantity}}/{{tag.pluginAttr.period.substring(0,1)}}</span>-->
<!--        </el-tag>-->
<!--        <el-button-->
<!--          class="button-new-tag"-->
<!--          size="small"-->
<!--          @click="addRoutingTag('gateway',item.type,item.plugins)"-->
<!--          v-author.disabled="$author && $author.routingcenter.plugAdd"-->
<!--        >+ 添加插件</el-button>-->
<!--      </el-row>-->
<!--      <el-row class="terminal-list" v-if="gatewayTags.length===0">暂无数据可选</el-row>-->
<!--    </section>-->

    <router-plug
      :gatewayType="routesDetail.gatewayType"
      :routesDetail="routesDetail"
      :routesPlugin="routesPlugin"
      @updatePlug="updatePlug"
      ref="routerPlug"
    ></router-plug>

    <router-table :routesDetail="routesDetail" :routesPlugin="routesPlugin"></router-table>
  </div>
</template>
<script lang="ts">
import { Component, Vue } from "vue-property-decorator";
import { routesList, routesPlugin, routesDelete } from "@/server/routeplus";
import { getProjectsId } from "@/server/publish";
import RouterPlug from "@/components/RoutingCenter/plug-in/index.vue";
import RouteServicesEdit from "@/components/RoutingCenter/RouteServicesEdit.vue";
import RoutesPoolTable from "@/components/RoutingCenter/RoutesPoolTable.vue";
import RouterTable from "./RouterTable.vue";

@Component({
  components: {
    RouterPlug,
    RoutesPoolTable,
    RouterTable
  }
})
export default class RoutingCenter extends Vue {
  private total: number = 0;
  private pageSize: number = 10;
  private pageNo: number = 1;
  private ready: boolean = false;
  private applicationTags: Plugins[] = [];
  private gatewayTags: Gateways[] = [];
  private routesDetail: RoutesDetail = {
    gatewayType: [],
    applicationName: "",
    applicationId: ""
  };

  private routesPlugin: PluginsType[] = [];

  private inputVisible: boolean = false;
  private inputValue: string = "";

  private mounted(): void {
    this.getRoutesPlugin(); // 请求插件列表
    this.getRoutesDetail(); // 获取外层标签
  }

  private getRoutesDetail(): void {
    getProjectsId(this.$route.params.id).then((res: any) => {
      let gatewayType: string[] = [];
      if (res.retval.gatewayType) {
        gatewayType = res.retval.gatewayType.split(",");
      } else {
        gatewayType = [];
      }
      this.routesDetail = {
        gatewayType,
        applicationName: this.$route.params.application,
        applicationId: this.$route.params.id
      };
      this.getTags(); // 获取外层标签
    });
  }

  // 请求插件列表
  private getRoutesPlugin(): void {
    routesPlugin().then((res: any) => {
      this.routesPlugin = res.retval.plugins;
    });
  }

  // 获取最外层的标签
  private getTags(): void {
    routesList(this.$route.params.id, this.pageNo, this.pageSize).then(
      (res: any) => {
        this.applicationTags = res.retval.applications.plugins;
        this.gatewayTags = res.retval.gateways;
        this.routesDetail.gatewayType.forEach((item: string) => {
          if (!this.gatewayTags.some(tag => tag.type === item)) {
            this.gatewayTags.push({
              type: item,
              plugins: []
            });
          }
        });
      }
    );
  }

  // ================================================== 插件数据的更新操作
  // 更新插件
  private updatePlug(row: PluginsIn, id: number): void {
    // 新增
    if (!row.plugins[0].hasOwnProperty("id")) {
      row.plugins[0].id = id;
      if (row.type === "all") {
        this.addAll(row);
        return;
      }

      this.addGateway(row);
      return;
    }

    // 修改
    if (row.type === "all") {
      this.editAll(row);
      return;
    }

    this.eidtGateway(row);
  }

  // 修改应用层父级插件
  private editAll(row: PluginsIn): void {
    this.applicationTags.forEach((item: Plugins, index: number) => {
      row.plugins.forEach((plugins: Plugins, num: number) => {
        if (item.id === plugins.id) {
          this.$set(this.applicationTags, index, plugins);
        }
      });
    });
  }

  // 添加应用层父级插件
  private addAll(row: PluginsIn): void {
    this.applicationTags.push(row.plugins[0]);
    console.log(this.applicationTags);
  }

  // 增加网关层父级插件
  private addGateway(row: PluginsIn): void {
    this.gatewayTags.forEach((gatewayTag: Gateways, key: number) => {
      if (gatewayTag.type === row.type) {
        // @ts-ignore
        this.gatewayTags[key].plugins.push(row.plugins[0]);
      }
    });
  }

  // 修改网关层父级插件
  private eidtGateway(row: PluginsIn): void {
    this.gatewayTags.forEach((gatewayTag: Gateways, key: number) => {
      if (gatewayTag.type === row.type) {
        gatewayTag.plugins.forEach((item: Plugins, index: number) => {
          row.plugins.forEach((plugins: Plugins, num: number) => {
            if (item.id === plugins.id) {
              this.$set(this.gatewayTags[key].plugins, index, plugins);
            }
          });
        });
      }
    });
  }

  // ==============================================================关于标签-操作

  // 关于标签--修改
  private editRoutingTag(
    row: Plugins,
    gatewayName: string,
    type: string
  ): void {
    // @ts-ignore
    if (!this.$isAuthor(this.$author.routingcenter.plugEdit.id)) {
      this.$message.error("您没有相关权限!");
      return;
    }
    (this.$refs.routerPlug as RouterPlug).openRoutring(row, gatewayName, type);
  }

  // 关于标签-新增
  private addRoutingTag(
    gatewayName: string,
    type: string,
    plugins: Plugins[]
  ): void {
    if (this.routesPlugin.length <= plugins.length) {
      this.$message.error("没有剩余插件可以使用");
      return;
    }
    (this.$refs.routerPlug as RouterPlug).openRoutring({}, gatewayName, type);
  }

  // 删除应用层
  private closeServicesTag(row: Plugins, index: number): void {
    // @ts-ignore
    if (!this.$isAuthor(this.$author.routingcenter.plugDel.id)) {
      this.$message.error("您没有相关权限!");
      return;
    }
    if (row.enabled) {
      this.$message.error("请先禁用再关闭!");
      return;
    }
    this.$confirm("此操作将删除插件, 是否继续?", "警告", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      confirmButtonClass: "el-button--danger",
      cancelButtonClass: "el-button--primary",
      type: "error"
    }).then(() => {
      routesDelete({
        services: {
          id: row.id
        }
      }).then(
        res => {
          // 模拟删除
          this.applicationTags.splice(index, 1);
        },
        (err: any) => {
          this.$message.error(err.errmsg);
        }
      );
    });
  }

  // 删除网关层
  private closeRoutingTag(row: Plugins): void {
    // @ts-ignore
    if (!this.$isAuthor(this.$author.routingcenter.plugDel.id)) {
      this.$message.error("您没有相关权限!");
      return;
    }
    if (row.enabled) {
      this.$message.error("请先禁用再关闭!");
      return;
    }
    this.$confirm("此操作将删除插件, 是否继续?", "警告", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      confirmButtonClass: "el-button--danger",
      cancelButtonClass: "el-button--primary",
      type: "error"
    }).then(() => {
      routesDelete({
        routes: {
          id: row.id
        }
      }).then(
        res => {
          this.gatewayTags.forEach((gatewayTag: Gateways, key: number) => {
            gatewayTag.plugins.forEach((plugin: Plugins, index: number) => {
              if (row.id === plugin.id) {
                this.gatewayTags[key].plugins.splice(index, 1);
              }
            });
          });
        },
        (err: any) => {
          this.$message.error(err.errmsg);
        }
      );
    });
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

  .button-new-tag {
    height: 32px;
    line-height: 30px;
    padding-top: 0;
    padding-bottom: 0;
  }

  .input-new-tag {
    width: 90px;
    margin-left: 15px;
    vertical-align: bottom;
  }

  .tag-spacing {
    margin: 10px;
  }

  .tag-list {
    margin-right: 10px;
  }
}
</style>

<style lang="scss">
.el-tag + .el-tag {
  margin-left: 20px;
}
</style>
