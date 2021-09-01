<template>
  <div class="server-eidt">
    <div class="breadcrumb">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>添加项目</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="edit">
      <el-form ref="form" :model="form" label-width="80px">
        <el-form-item label="项目名称">
          <el-input v-model.trim="form.name" style="max-width: 500px"></el-input>
        </el-form-item>
        <el-form-item label="项目类型">
          <el-select v-model="form.type" placeholder="请选择项目类型" width="200px">
            <el-option label="后端外部" value="server_outer"></el-option>
            <el-option label="后端内部" value="server_inner"></el-option>
            <el-option label="前端项目" value="client"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="网关类型">
          <el-checkbox-group v-model="form.gatewayType">
            <el-checkbox label="web"></el-checkbox>
            <el-checkbox label="pc"></el-checkbox>
            <el-checkbox label="openapi"></el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit" :loading="loading" class="pull-right">立即添加</el-button>
          <el-button @click="onChange">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>
<script lang="ts">
import { Component, Vue, Prop } from "vue-property-decorator";
import { publishSave } from "@/server/publish.ts";
@Component
export default class ServerEdit extends Vue {
  private current: number = 1;
  private size: number = 10;
  private total: number = 0;
  private loading: boolean = false;
  private form: any = {
    id: "",
    ip: "",
    name: "",
    remark: "",
    port: "",
    type: [],
    gatewayType: []
  };

  private onSubmit(): void {
    if (!this.form.name) {
      this.$message({
        message: "请输入名称",
        type: "warning"
      });
      return;
    }

    if (!this.form.type) {
      this.$message({
        message: "请选择项目类型",
        type: "warning"
      });
      return;
    }

    if (this.form.gatewayType.length === 0) {
      this.$message({
        message: "策略不能为空",
        type: "warning"
      });
      return;
    }

    this.loading = true;
    publishSave(this.form.name, this.form.type, this.form.gatewayType.join())
      .then((rs: any) => {
        this.loading = false;
        this.$router.push({ path: "/project/index" });
      })
      .catch((err: any) => {
        this.loading = false;
        this.$message({
          message: err.errmsg,
          type: "warning"
        });
      });
  }

  private onChange(): void {
    this.$router.go(-1);
  }
}
</script>

<style lang="scss">
.server-eidt {
  text-align: left;
  .edit {
    padding-top: 40px;
  }
}
</style>
