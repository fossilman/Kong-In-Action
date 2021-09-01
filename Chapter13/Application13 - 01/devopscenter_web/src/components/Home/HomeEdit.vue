<template>
  <el-dialog
    width="35%"
    title="项目修改"
    :visible.sync="homeShow"
    append-to-body
    :close-on-press-escape="false"
    :close-on-click-modal="false"
    v-loading="ready"
  >
    <el-form :model="homeDataCopy" ref="homeData" label-position="left" label-width="110px">
      <el-form-item label="项目名称">
        <el-input v-model.trim="homeDataCopy.name" style="max-width: 500px" disabled></el-input>
      </el-form-item>
      <el-form-item label="项目类型">
        <el-select v-model="homeDataCopy.type" placeholder="请选择项目类型" width="200px" disabled>
          <el-option label="后端外部" value="server_outer"></el-option>
          <el-option label="后端内部" value="server_inner"></el-option>
          <el-option label="前端项目" value="client"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="网关类型">
        <el-checkbox-group v-model="homeDataCopy.gatewayType">
          <el-checkbox label="web" :disabled="gatewayOldType.includes('web')"></el-checkbox>
          <el-checkbox label="pc" :disabled="gatewayOldType.includes('pc')"></el-checkbox>
          <el-checkbox label="openapi" :disabled="gatewayOldType.includes('openapi')"></el-checkbox>
        </el-checkbox-group>
      </el-form-item>
    </el-form>

    <div slot="footer" class="dialog-footer">
      <el-button @click="homeShow = false">取 消</el-button>
      <el-button type="primary" @click="handleSave">保 存</el-button>
    </div>
  </el-dialog>
</template>
<script lang="ts">
import { Component, Vue, Ref, Prop } from "vue-property-decorator";
import { ElForm } from "element-ui/types/form";
import MockCenter from "../../views/MockCenter/Index.vue";
import { publishEdit } from "@/server/publish";
@Component
export default class HomeEdit extends Vue {
  @Ref() public homeForm!: ElForm;
  private homeShow: boolean = false;
  private homeDataCopy: HomeForm = {
    buildStatus: "",
    createBy: "",
    del: 0,
    gitlabId: 0,
    groups: "",
    id: 0,
    name: "",
    num: 0,
    publishStatus: "",
    type: "",
    routeType: "",
    gatewayType: []
  };
  private ready: boolean = false;
  private gatewayOldType: string[] = [];

  public openHomeShow(row: HomeForm): void {
    this.homeDataCopy = JSON.parse(JSON.stringify(row));
    if (this.homeDataCopy.routeType) {
      this.$set(
        this.homeDataCopy,
        "gatewayType",
        this.homeDataCopy.routeType.split(",")
      );
      this.gatewayOldType = row.routeType.split(",");
    } else {
      this.$set(this.homeDataCopy, "gatewayType", []);
      this.gatewayOldType = [];
    }
    this.homeShow = true;
  }

  private handleSave(): void {
    this.ready = true;
    const gatewayType: string = this.homeDataCopy.gatewayType.join(",");
    publishEdit(this.homeDataCopy.id, gatewayType)
      .then(
        res => {
          this.ready = false;
          this.homeShow = false;
          this.$emit("update", "success");
        },
        err => {
          this.ready = false;
          this.$message.error(err.errmsg);
        }
      )
      .catch(err => {
        this.ready = true;
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
