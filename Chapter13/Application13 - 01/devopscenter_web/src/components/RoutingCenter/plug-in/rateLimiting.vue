<template>
  <el-form :model="routeDataCopy" ref="rateLimitingForm" label-position="left" label-width="110px">
    <el-form-item
      prop="quantity"
      label="限流数量"
      :rules="{ required: true, message: '请输入限流数量', trigger: 'blur' }"
    >
      <el-input-number v-model="routeDataCopy.quantity" autocomplete="off" :min="1"></el-input-number>
    </el-form-item>

    <el-form-item
      prop="period"
      label="限流周期"
      :rules="{ required: true, message: '请选择限流周期', trigger: 'blur' }"
    >
      <el-select v-model="routeDataCopy.period" placeholder="请选择限流周期">
        <el-option label="秒" value="second"></el-option>
        <el-option label="分钟" value="minute"></el-option>
        <el-option label="小时" value="hour"></el-option>
        <el-option label="天" value="day"></el-option>
        <el-option label="月" value="month"></el-option>
        <el-option label="年" value="year"></el-option>
      </el-select>
    </el-form-item>
  </el-form>
</template>
<script lang="ts">
import { Component, Vue, Ref, Prop, Watch } from "vue-property-decorator";
import { ElForm } from "element-ui/types/form";

@Component
export default class RateLimiting extends Vue {
  @Ref() public rateLimitingForm!: ElForm;

  private routeDataCopy: PluginAttr = {
    quantity: 0,
    period: ""
  };

  public getData(): Promise<PluginAttr> {
    return new Promise((resolve, reject) => {
      this.rateLimitingForm.validate((status: boolean, fields: object) => {
        if (!status) reject();
        resolve(this.routeDataCopy);
      });
    });
  }

  public setData(routeData: Plugins): void {
    this.routeDataCopy = JSON.parse(JSON.stringify(routeData.pluginAttr));
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