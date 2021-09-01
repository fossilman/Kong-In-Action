<template>
  <el-dialog
    width="35%"
    title="插件配置"
    :visible.sync="routingPlugShow"
    append-to-body
    :close-on-press-escape="false"
    :close-on-click-modal="false"
  >
    <el-form :model="routeData" label-position="left" label-width="110px">
      <el-form-item label="插件类型">
        <el-col span="22">
          <el-select
            style="width:100%"
            v-model="routeData.pluginName"
            placeholder="请选择插件类型"
            @change="getPluginId()"
          >
            <el-option
              v-for="(item,index) in routesPlugin"
              :label="item.remark"
              :value="item.pluginName"
              :key="index"
            ></el-option>
          </el-select>
        </el-col>
      </el-form-item>

      <rate-limiting
        v-if="routeData.pluginName==='rateLimiting'"
        ref="rateLimiting"
        :routeData="routeData"
      ></rate-limiting>

      <el-form-item label="是否启用">
        <el-switch v-model="routeData.enabled"></el-switch>
      </el-form-item>
    </el-form>

    <div slot="footer" class="dialog-footer">
      <el-button @click="routingPlugShow = false">取 消</el-button>
      <el-button type="primary" @click="handleSave">保 存</el-button>
    </div>
  </el-dialog>
</template>
<script lang="ts">
import { Component, Vue, Ref, Prop } from "vue-property-decorator";
import { mockRule } from "@/common/js/validate-rule";
import { ElForm } from "element-ui/types/form";
import rateLimiting from "./rateLimiting.vue";
@Component({
  components: {
    rateLimiting
  }
})
export default class RouterPlugList extends Vue {
  @Prop() public routesPlugin!: PluginsType[];
  @Prop() public routesDetail!: RoutesDetail;

  private routingPlugShow: boolean = false;
  private routeData: Plugins = {
    pluginAttr: {
      quantity: 0,
      period: ""
    },
    pluginName: "",
    enabled: true
  };

  public openRoutring(row: Plugins | {}): void {
    if (JSON.stringify(row) !== "{}") {
      this.routeData = JSON.parse(JSON.stringify(row));
    } else {
      this.routeData = {
        pluginAttr: {
          quantity: 0,
          period: ""
        },
        pluginId: 0,
        pluginName: "",
        enabled: true
      };
      // @ts-ignore
      const pluginName: string = this.routesPlugin[0].pluginName;
      this.routeData.pluginName = pluginName;
      // @ts-ignore
      this.routeData.pluginId = this.routesPlugin[0].id;
    }
    this.$nextTick(() => {
      // @ts-ignore
      this.$refs[this.routeData.pluginName].setData(this.routeData);
    });
    this.routingPlugShow = true;
  }

  private handleSave(): void {
    if (this.routeData.pluginName === "rateLimiting") {
      (this.$refs.rateLimiting as rateLimiting).getData().then(
        (res: any) => {
          this.routeData.pluginAttr = res;
          const data: Plugins = this.routeData;
          let matomoTip: string = "新增";
          if (this.routeData.id) {
            data.id = this.routeData.id;
            matomoTip = "修改";
          }
          this.$emit("updatePlug", data);
          this.routingPlugShow = false;
        },
        (res: any) => {
          console.log("未通过");
        }
      );
    }
  }

  private getPluginId(): void {
    this.routesPlugin.forEach((item: PluginsType) => {
      if (item.id === this.routeData.pluginId) {
        this.routeData.pluginName = item.pluginName;
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
