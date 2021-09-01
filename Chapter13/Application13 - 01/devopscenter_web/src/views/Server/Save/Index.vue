<template>
  <div class="server-eidt">
    <div class="breadcrumb">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>添加服务器</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="edit">
      <el-form ref="form" :model="form" label-width="80px">
        <el-form-item label="ip地址">
          <el-input v-model.trim="form.ip" style="max-width: 500px"></el-input>
        </el-form-item>
        <el-form-item label="端口号">
          <el-input v-model.trim="form.port" style="max-width: 500px"></el-input>
        </el-form-item>
        <el-form-item label="分组">
          <el-input v-model.trim="form.team" style="max-width: 500px"></el-input>
        </el-form-item>
        <el-form-item label="名称">
          <el-input v-model.trim="form.name" style="max-width: 500px"></el-input>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.remark" style="max-width: 500px"></el-input>
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
import { Component, Vue, Prop } from 'vue-property-decorator';
import { serversSave } from "@/server/servers.ts";
@Component
export default class ServerEdit extends Vue {
  private current: number = 1;
  private size: number = 10;
  private total: number = 0;
  private loading: boolean = false;
  private isSubmit: boolean = false; // 提交锁
  private form: any = {
    id: '',
    ip: '',
    name: '',
    remark: '',
    team: '',
    port: ''
  };

  private onSubmit(): void {
    if (!this.form.ip) {
      this.$message({
        message: '请输入ip地址',
        type: 'warning'
      });
      return;
    }
    if (!this.form.port) {
      this.$message({
        message: '请输入端口号',
        type: 'warning'
      });
      return;
    }
    if (!this.form.team) {
      this.$message({
        message: '请输入服务器分组',
        type: 'warning'
      });
      return;
    }
    if (!this.form.name) {
      this.$message({
        message: '请输入名称',
        type: 'warning'
      });
      return;
    }
    if (!this.form.remark) {
      this.$message({
        message: '请输入描述',
        type: 'warning'
      });
      return;
    }
    if (this.isSubmit) {
      return;
    }
    this.loading = true;
    this.isSubmit = true;
    serversSave(this.form.ip, this.form.name, this.form.remark, this.form.port, this.form.team).then((rs: any) => {
      this.loading = false;
      this.$router.push({path: '/server/list'});
    }).catch((err: any) => {
      this.isSubmit = false;
      this.loading = false;
      this.$message({
        message: err.errmsg,
        type: 'warning'
      });
    });
  }

  private onChange(): void {
    this.$router.go(-1);
  }

}
</script>

<style lang="scss">
  .server-eidt{
    text-align: left;
    .edit{
      padding-top: 40px;
    }
    .el-input__inner{
      // line-height: 1px;
      vertical-align: middle;
    }
  }
</style>
