<template>
  <el-dialog
    width="35%"
    title="网关层"
    :visible.sync="routerPoolShow"
    append-to-body
    :close-on-press-escape="false"
    :close-on-click-modal="false"
  >
    <el-form :model="routeData" ref="routeFrom" label-position="left" label-width="110px">
      <el-form-item
        label="外部服务路径"
        prop="outPath"
        :rules="routePoolFrom.outPath"
        placeholder="请输入url  例如/home"
      >
        <el-input disabled v-model="routeData.outPath"></el-input>
      </el-form-item>

      <el-form-item label="内部服务路径" prop="innerPath">
        <el-input disabled v-model="routeData.innerPath"></el-input>
      </el-form-item>

      <el-form-item label="描述" prop="remark">
        <el-input type="textarea" v-model="routeData.remark" placeholder="请输入描述"></el-input>
      </el-form-item>
    </el-form>

    <div slot="footer" class="dialog-footer">
      <el-button @click="routerPoolShow = false">取 消</el-button>
      <el-button type="primary" @click="handleSave">保 存</el-button>
    </div>
  </el-dialog>
</template>
<script lang="ts">
import { Component, Vue, Ref, Prop, Watch } from "vue-property-decorator";
import { routePoolFrom } from "@/common/js/validate-rule";
import { ElForm } from "element-ui/types/form";
import { servicesRoutesSave } from "@/server/routeplus";

@Component
export default class RouterPoolEdit extends Vue {
  @Ref() public routeFrom!: ElForm;
  @Prop() public routesPlugin!: PluginsType;
  @Prop() public routesDetail!: RoutesDetail;
  @Prop() public serviceId!: number;
  private routePoolFrom: typeof routePoolFrom = routePoolFrom;
  private gatewayTypeList: string[] = [];

  private routerPoolShow: boolean = false;
  private gatewayType: string[] = [];
  private gatewayOldType: string[] = [];

  private routeData: RoutesPool = {
    name: "",
    innerPath: "",
    outPath: "",
    hasEnabled: false,
    remark: "",
    plugins: []
  };
  private routeOldData!: RoutesPool;

  public openRouterPool(row: RouteServices | {}): void {
    if (JSON.stringify(row) !== "{}") {
      this.routeData = JSON.parse(JSON.stringify(row));
      this.routeOldData = JSON.parse(JSON.stringify(row));
    } else {
      this.routeData = JSON.parse(
        JSON.stringify({
          name: "",
          innerPath: "",
          outPath: "",
          hasEnabled: false,
          remark: "",
          plugins: []
        })
      );
    }
    this.routerPoolShow = true;
  }

  private handleSave(): void {
    this.routeFrom.validate((status: boolean, fields: object) => {
      if (status) {
        const data: servicesRoutes = {
          serviceId: this.serviceId,
          routesPool: {
            routeId: this.routeData.routeId,
            routeType: this.routeData.routeType,
            // innerPath: this.routeData.innerPath,
            outPath: this.routeData.outPath,
            remark: this.routeData.remark || ""
          }
        };
        if (
          this.routeOldData.outPath === data.routesPool.outPath &&
          this.routeOldData.remark === data.routesPool.remark
        ) {
          this.routerPoolShow = false;
          return;
        }
        const outPath: string | undefined = data.routesPool.outPath;
        if (this.routeOldData.outPath === data.routesPool.outPath) {
          delete data.routesPool.outPath;
        }
        servicesRoutesSave(data).then(
          (res: any) => {
            this.$message.success(res.errmsg);
            this.routerPoolShow = false;
            data.routesPool.outPath = outPath;
            this.$emit("upload", data);
          },
          err => {
            this.$message.error(err.errmsg);
          }
        );
      }
    });
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
