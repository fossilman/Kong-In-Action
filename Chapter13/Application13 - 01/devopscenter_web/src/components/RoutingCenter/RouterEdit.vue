<template>
  <el-dialog
    width="35%"
    title="应用层"
    :visible.sync="routingShow"
    append-to-body
    :close-on-press-escape="false"
    :close-on-click-modal="false"
  >
    <el-form :model="routeData" ref="routeFrom" label-position="left" label-width="110px">
      <el-form-item label="内部服务路径" prop="path" :rules="routePoolFrom.innerPath">
        <el-input v-model="routeData.path" :disabled="disabledShow" placeholder="请输入url  例如/home">
          <template slot="prepend">/{{routesDetail.applicationName}}</template>
        </el-input>
      </el-form-item>

      <el-form-item label="描述" prop="remark">
        <el-input v-model="routeData.remark" placeholder="请输入描述"></el-input>
      </el-form-item>

      <el-form-item label="外部网关类型" :rules="{ required: true, message: '请输入描述', trigger: 'blur' }">
        <el-checkbox-group v-model="gatewayType">
          <el-checkbox v-for="(item,index) in routesDetail.gatewayType" :key="index" :label="item"></el-checkbox>
        </el-checkbox-group>
        <span v-if="routesDetail.gatewayType.length==0">暂无可选</span>
      </el-form-item>
    </el-form>

    <div slot="footer" class="dialog-footer">
      <el-button @click="routingShow = false">取 消</el-button>
      <el-button type="primary" @click="handleSave">保 存</el-button>
    </div>
  </el-dialog>
</template>
<script lang="ts">
import { Component, Vue, Ref, Prop, Watch } from "vue-property-decorator";
import { routePoolFrom } from "@/common/js/validate-rule";
import { ElForm } from "element-ui/types/form";
import { servicesSaveOrUpdate } from "@/server/routeplus";

@Component
export default class RouterEdit extends Vue {
  @Ref() public routeFrom!: ElForm;
  @Prop() public routesPlugin!: PluginsType;
  @Prop() public routesDetail!: RoutesDetail;
  private disabledShow: boolean = false;
  private routePoolFrom: typeof routePoolFrom = routePoolFrom;
  private gatewayTypeList: string[] = [];

  private routingShow: boolean = false;
  private gatewayType: string[] = [];
  private gatewayOldType: string[] = [];
  private chooiseType: any[] = [];

  private routeData: RouteServices = {
    path: "",
    enabled: true,
    remark: "",
    routesPool: [],
    plugins: []
  };

  public openRoutring(row: RouteServices | {}): void {
    this.gatewayType = [];
    this.gatewayOldType = [];
    this.chooiseType = [];
    // @ts-ignore
    this.routeData.routes = [];
    if (JSON.stringify(row) !== "{}") {
      this.disabledShow = true;
      this.routeData = JSON.parse(JSON.stringify(row));
      this.routeData.path = this.routeData.path.replace(`${this.routesDetail.applicationName}/`, '');
      // @ts-ignore
      if (row.routes) {
        // @ts-ignore
        row.routes.forEach((item: string, index: number) => {
          // @ts-ignore
          this.gatewayType.push(item.routeType);
          // @ts-ignore
          this.gatewayOldType.push(item.routeType);
          // @ts-ignore
          this.chooiseType[item.routeType] = {
            // @ts-ignore
            routeType: item.routeType,
            // @ts-ignore
            routeId: item.routeId
          };
        });
      }
    } else {
      this.disabledShow = false;
      this.routeData = JSON.parse(
        JSON.stringify({
          path: "",
          enabled: true,
          remark: "",
          routes: [],
          plugins: []
        })
      );
      this.gatewayType = [];
      this.gatewayOldType = [];
    }
    this.routingShow = true;
  }
  private handleSave(): void {
    this.routeFrom.validate((status: boolean, fields: object) => {
      if (status) {
        const routesPool: any[] = [];
        this.gatewayType.forEach((item: string) => {
          if (!this.gatewayOldType.includes(item)) {
            routesPool.push({
              // @ts-ignore
              routeType: item
            });
          } else {
            routesPool.push({
              routeType: item,
              // @ts-ignore
              routeId: this.chooiseType[item].routeId
            });
          }
        });
        if (routesPool.length === 0) {
          this.$message.error("外部网关类型不能为空");
          return;
        }
        const routeData: RouteServices = this.routeData;
        routeData.routesPool = routesPool;

        const data: any = {
          applicationId: Number(this.routesDetail.applicationId),
          applicationName: this.routesDetail.applicationName,
          services: routeData
        };
        data.services.path = `/${this.routesDetail.applicationName}${data.services.path}`;
        if (this.disabledShow) {
          delete data.services.path;
        }
        this.servicesSave(data);
        this.routingShow = false;
      }
    });
  }

  private servicesSave(data: PluginsIn): void {
    // @ts-ignore
    delete data.services.plugins;
    servicesSaveOrUpdate(data).then(
      (res: any) => {
        this.$message.success(res.errmsg);
        this.$emit("upload", res.retval, this.disabledShow);
      },
      (err: any) => {
        this.$message.error(err.errmsg);
        this.routingShow = false;
      }
    );
  }
}
</script>
<style lang="scss" scoped>
.table {
  width: 100%;
  .main {
    position: relative;
    width: 100%;
  }
  .btn_container {
    width: 100%;
    display: flex;
    justify-content: flex-end;
    .saveBtn {
      width: 100px;
      margin-right: 20px;
    }
    .cancelBtn {
      width: 100px;
    }
  }
}
</style>
