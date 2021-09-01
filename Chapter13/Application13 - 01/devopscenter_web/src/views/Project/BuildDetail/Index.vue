<template>
  <div class="build-detail">
    <div class="content">
      <el-table :data="list" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="gitlabHead" label="标题" width="180">
          <template slot-scope="scope">{{scope.row.gitlabHead}}</template>
        </el-table-column>
        <el-table-column prop="gitlabDesc" label="内容">
          <template slot-scope="scope">{{scope.row.gitlabDesc}}</template>
        </el-table-column>
        <el-table-column prop="createBy" label="构建者" width="120"></el-table-column>
        <el-table-column prop="updateTime" label="构建时间" width="170">
          <template slot-scope="scope">{{scope.row.createTime | formatDate}}</template>
        </el-table-column>
        <el-table-column prop="buildStatus" label="状态" width="100" :render-header="renderHeader">
          <template slot-scope="scope">
            <el-tooltip
              class="item"
              effect="dark"
              :content="scope.row.failReason"
              placement="left"
              v-if="scope.row.buildStatus === 'FAIL' || scope.row.buildStatus === 'FAILURE'"
            >
              <div style="cursor: pointer">
                <i
                  class="build_btn icon-status el-icon--right"
                  :class="{
                  'icon-status-loading':scope.row.buildStatus === 'ING',
                  'icon-status-no':scope.row.buildStatus === 'NO',
                  'icon-status-enter':scope.row.buildStatus === 'SUCCESS',
                  'icon-status-close':scope.row.buildStatus === 'FAILURE' || scope.row.buildStatus === 'FAIL',
               }"
                ></i>
              </div>
            </el-tooltip>
            <i
              class="build_btn icon-status el-icon--right"
              v-if="scope.row.buildStatus !== 'FAIL' && scope.row.buildStatus !== 'FAILURE'"
              :class="{
                  'icon-status-loading':scope.row.buildStatus === 'ING',
                  'icon-status-no':scope.row.buildStatus === 'NO',
                  'icon-status-enter':scope.row.buildStatus === 'SUCCESS',
                  'icon-status-close':scope.row.buildStatus === 'FAILURE' || scope.row.buildStatus === 'FAIL',
               }"
            ></i>
          </template>
        </el-table-column>
      </el-table>
    </div>
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
import { Component, Vue } from "vue-property-decorator";

import { getBuildDetail } from "@/server/publish";

@Component
export default class BuildDetail extends Vue {
  private current: number = 1;
  private size: number = 10;
  private total: number = 0;

  private loading: boolean = false;
  private list: any[] = [];

  private mounted(): void {
    this.buildDetail();
  }

  private buildDetail(): void {
    this.loading = true;
    const id: any = Number(this.$route.params.id);
    getBuildDetail(id, this.size, this.current)
      .then((rs: any) => {
        this.loading = false;
        this.list = rs.retval.buildList;
        this.total = rs.retval.total;
      })
      .catch((err: any) => {
        this.$message({
          message: err.errmsg,
          type: "warning"
        });
      });
  }

  private currentChange(current: number): void {
    this.current = current;
    this.buildDetail();
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
.build-detail {
  text-align: left;
}
</style>
