<template>
  <div class="deploy-detail-d">
    <div class="content">
      <h1>版本明细</h1>
      <div class="list">
        <div class="list-sub" :style="{width:oldDeployServerList.length?'':'100%'}">
          <h2>新版本</h2>
          <el-table
            :data="newDeployServerList"
            v-loading="loading"
            border
            :span-method="newDeploySpanMethod"
            style="width: 100%">
            <el-table-column
              prop="name"
              label="机器列表">
            </el-table-column>
            <el-table-column
              prop="serverIp"
              label="ip地址">
              <template slot-scope="scope">
                {{scope.row.serverIp}}
              </template>
            </el-table-column>
            <el-table-column
              prop="gitlabVersion"
              label="版本编号"
            >
            </el-table-column>
            <el-table-column
              prop="gitlabVersion"
              label="版本描述"
              >
              <template slot-scope="scope">
                {{remark}}
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="list-sub" v-if="oldDeployServerList.length">
          <h2>老版本</h2>
          <el-table
            :data="oldDeployServerList"
            :span-method="oldDeploySpanMethod"
            border
            v-loading="loading"
            style="width: 100%">
            <el-table-column
              prop="name"
              label="机器列表">
            </el-table-column>
            <el-table-column
              prop="serverIp"
              label="ip地址">
              <template slot-scope="scope">
                {{scope.row.serverIp}}
              </template>
            </el-table-column>
            <el-table-column
              prop="gitlabVersion"
              label="版本编号"
            >
            </el-table-column>
            <el-table-column
              prop="gitlabVersion"
              label="版本描述"
            >
              <template slot-scope="scope">
                {{oldRemark}}
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
      <h2 style="margin-top: 40px">kong网关流量</h2>
      <div class="target">
        <el-table
          :data="kongTarget"
          v-loading="loading"
          stripe
          border
          style="width: 100%">
          <el-table-column
            prop="target"
            label="机器列表"
            min-width="30%">
          </el-table-column>
          <el-table-column
            prop="weight"
            label="流量"
          >
            <template slot-scope="scope">
              {{scope.row.weight}}
            </template>
          </el-table-column>
        </el-table>
      </div>

    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Watch, Emit } from 'vue-property-decorator';

import {
  getDeployDetailMore,
} from "@/server/publish";

@Component
export default class DeployDetail extends Vue {
  @Prop() public visible!: boolean;
  @Prop() public id!: boolean;
  @Prop() public detailublishType!: string;
  private loading: boolean = false;
  private remark: string = '';
  private oldRemark: string = '';
  private newDeployServerList: any[] = [];
  private oldDeployServerList: any[] = [];
  private kongTarget: any[] = [];

  private mounted(): void {
    this.getDeployDetail();
  }

  @Watch('id')
  private getData(): void {
    this.getDeployDetail();
  }

  private getDeployDetail(): void {
    this.loading = true;
    const id: any = this.id;
    this.newDeployServerList = [];
    this.oldDeployServerList = [];
    getDeployDetailMore(id).then((rs: any) => {
      this.loading = false;
      this.newDeployServerList = rs.retval.newDeployServerList;
      this.remark = rs.retval.remark;
      this.oldRemark = rs.retval.remark1;
      // 非百分百模式才显示老版本
      if (this.newDeployServerList.length && this.detailublishType !== 'SIMPLE') {
        this.oldDeployServerList = rs.retval.oldDeployServerList;
      }
      this.kongTarget = rs.retval.targets;
    }).catch(err => {
      this.loading = false;
      this.$message({
        message: err.errmsg,
        type: 'warning'
      });
    });
  }

  private newDeploySpanMethod({ row, column, rowIndex, columnIndex }: any): any {
    if (columnIndex === 2 || columnIndex === 3) {
      if (rowIndex === 0) {
        return {
          rowspan: this.newDeployServerList.length,
          colspan: 1
        };
      } else {
        return {
          rowspan: 0,
          colspan: 0
        };
      }
    }
  }

  private oldDeploySpanMethod({ row, column, rowIndex, columnIndex }: any): any {
    if (columnIndex === 2 || columnIndex === 3) {
      if (rowIndex === 0) {
        return {
          rowspan: this.oldDeployServerList.length,
          colspan: 1
        };
      } else {
        return {
          rowspan: 0,
          colspan: 0
        };
      }
    }
  }



}
</script>

<style lang="scss">
  .deploy-detail-d{
    text-align: left;
    .deploy-remark{
      text-align: left;
      font-size: 13px;
      margin-bottom: 40px;
      line-height: 1.6em;
      color: #666;
      border: 1px solid #EBEEF5;
      h3{
        font-size: 14px;
        color: #333;
        padding: 20px 15px;
        background-color: rgb(242, 242, 242);
      }
      .remark{
        padding: 10px 15px;
      }
    }
    .content{
      padding: 0 20px 20px 20px;
      text-align: center;
      h1{
        padding-bottom: 30px;
        //padding-top: 20px;
        font-size: 18px;
        color: #409EFF;
      }
      h2{
        font-size: 14px;
        color: #333;
        padding: 20px 0;
        background-color: rgb(242, 242, 242);
      }
      .list{
        display: flex;
        justify-content: space-between;
        .list-sub{
          text-align: center;

          width: 49%;
          // border: 1px solid #dcdcdc
        }
      }
      .target{
        // border: 1px solid #dcdcdc;
      }
    }
  }
</style>
