<template>
  <div class="deploy-detail">
    <div class="content">
      <el-table :data="list" v-loading="loading" stripe style="width: 100%">
        <el-table-column fixed="left" prop="id" label="编号" width="70px"></el-table-column>
        <el-table-column min-width="160" prop="gitlabVersion" label="新版版本号"></el-table-column>
        <el-table-column width="160" prop="beforeGitlabVersion" label="老版版本号">
          <template
            slot-scope="scope"
          >{{scope.row.beforeGitlabVersion && scope.row.publishType !== 'SIMPLE'?scope.row.beforeGitlabVersion: '无'}}</template>
        </el-table-column>
        <el-table-column width="160" prop="vagrancy" label="新版占比">
          <template slot-scope="scope">{{scope.row.vagrancy}}%</template>
        </el-table-column>
        <el-table-column width="160" prop="publishType" label="发布模式">
          <template slot-scope="scope">{{scope.row.publishType === 'SIMPLE'?'100%新版流量':'新老版本兼容'}}</template>
        </el-table-column>
        <el-table-column width="120" prop="createBy" label="发布者"></el-table-column>
        <el-table-column width="170" prop="updateTime" label="发布时间">
          <template slot-scope="scope">{{scope.row.createTime | formatDate}}</template>
        </el-table-column>
        <el-table-column
          width="100"
          prop="buildStatus"
          label="状态"
          :render-header="renderHeader"
          min-width="8%"
        >
          <template slot-scope="scope">
            <el-tooltip
              class="item"
              effect="dark"
              :content="scope.row.failReason"
              placement="left"
              v-if="scope.row.publishStatus === 'FAIL' || scope.row.publishStatus === 'FAILURE'"
            >
              <div style="cursor: pointer">
                <i
                  class="build_btn icon-status el-icon--right"
                  :class="{
                   'icon-status-loading':scope.row.publishStatus === 'ING',
                   'icon-status-no':scope.row.publishStatus === 'NO',
                   'icon-status-enter':scope.row.publishStatus === 'SUCCESS',
                   'icon-status-close':scope.row.publishStatus === 'FAILURE' || scope.row.publishStatus === 'FAIL'
                   }"
                ></i>
              </div>
            </el-tooltip>
            <i
              class="build_btn icon-status el-icon--right"
              v-if="scope.row.publishStatus !== 'FAIL' && scope.row.publishStatus !== 'FAILURE'"
              :class="{
                   'icon-status-loading':scope.row.publishStatus === 'ING',
                   'icon-status-no':scope.row.publishStatus === 'NO',
                   'icon-status-enter':scope.row.publishStatus === 'SUCCESS',
                   'icon-status-close':scope.row.publishStatus === 'FAILURE' || scope.row.publishStatus === 'FAIL'
                   }"
            ></i>
          </template>
        </el-table-column>
        <el-table-column fixed="right" label="操作" width="100" align="center">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              @click="showDetail(scope.row.id, scope.row.publishType)"
            >详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <el-drawer title="配置信息" append-to-body :visible.sync="showDrawer" :size="'85%'" class="over-drawer">
      <Detail :visible="showDrawer" :id="detailId" :detailublishType="detailublishType"></Detail>
    </el-drawer>
    <div class="pagination">
      <el-pagination
        :hide-on-single-page="true"
        @current-change="currentChange"
        background
        :page-size="size"
        layout="prev, pager, next"
        :total="total"
      ></el-pagination>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Watch, Emit } from "vue-property-decorator";
import Detail from "@/views/Project/DeployDetail/detail.vue";

import { getDeployDetail } from "@/server/publish";

@Component({
  components: {
    Detail
  }
})
export default class DeployDetail extends Vue {
  private current: number = 1;
  private size: number = 10;
  private total: number = 0;

  private loading: boolean = false;
  private list: any[] = [];
  private showDrawer: boolean = false;
  private detailId: number = 0;
  private detailublishType: string = "";

  private mounted(): void {
    this.getDeployDetail();
  }

  private getDeployDetail(): void {
    this.loading = true;
    const id: any = this.$route.params.id;
    this.list = [];
    getDeployDetail(id, this.size, this.current)
      .then((rs: any) => {
        this.loading = false;
        rs.retval.listDeploys.forEach((item: any) => {
          // if (item.publishVag < 100) {
          //   item.vagrancy = item.vagrancy / 10;
          // }
          this.list.push(item);
        });
        this.total = rs.retval.total;
      })
      .catch(err => {
        this.loading = false;
        this.$message({
          message: err.errmsg,
          type: "warning"
        });
      });
  }

  private showDetail(id: number, detailublishType: string): void {
    this.detailId = id;
    this.detailublishType = detailublishType;
    this.showDrawer = true;
  }

  private currentChange(current: number): void {
    this.current = current;
    this.getDeployDetail();
  }

  private renderHeader(h: any, { column, $index }: any, index: any): any {
    return h("span", {}, [
      h("span", {}, "状态 "),
      h(
        "el-popover",
        {
          props: {
            placement: "left",
            width: "200",
            trigger: "hover",
            content: "失败时，鼠标经过状态可以查看失败原因"
          }
        },
        [h("i", { slot: "reference", class: "el-icon-question" }, "")]
      )
    ]);
  }
}
</script>

<style lang="scss">
.deploy-detail {
  text-align: left;
  .desc {
    padding: 15px 20px;
    background-color: #f7f7f7;
    color: #909399;
    border-radius: 5px;
    font-size: 14px;
    margin: 10px 0 15px 0;
    line-height: 1.8em;
  }

  .pagination {
    padding-top: 30px;
    text-align: center;
  }
}
</style>
