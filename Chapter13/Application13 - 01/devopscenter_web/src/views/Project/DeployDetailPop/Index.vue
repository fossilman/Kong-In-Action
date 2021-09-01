<template>
  <div class="deploy-detail-pop">
    <el-dialog
      title="deploy详情"
      :visible.sync="dialogVisible"
      width="1000px"
      :before-close="handleClose"
      append-to-body>
      <div class="content">
        <el-table
          :data="list"
          stripe
          style="width: 100%">
          <el-table-column
            prop="id"
            label="编号"
            min-width="5%">
          </el-table-column>
          <el-table-column
            prop="gitlabVersion"
            label="新版版本号"
            min-width="12%">
          </el-table-column>
          <el-table-column
            prop="beforeGitlabVersion"
            label="老版版本号"
            min-width="12%">
            <template slot-scope="scope">
              {{scope.row.beforeGitlabVersion && scope.row.publishType !== 'SIMPLE'?scope.row.beforeGitlabVersion: '无'}}
            </template>
          </el-table-column>
          <el-table-column
            prop="vagrancy"
            label="新版占比"
            min-width="8%">
            <template slot-scope="scope">
              {{scope.row.vagrancy}}%
            </template>
          </el-table-column>
          <el-table-column
            prop="publishType"
            label="发布模式"
            min-width="12%">
            <template slot-scope="scope">
              {{scope.row.publishType === 'SIMPLE'?'100%新版流量':'新老版本兼容'}}
            </template>
          </el-table-column>
          <el-table-column
            prop="createBy"
            label="发布者"
            min-width="14%">
          </el-table-column>
          <el-table-column
            prop="updateTime"
            label="发布时间"
            min-width="18%">
            <template slot-scope="scope">
              {{scope.row.createTime | formatDate}}
            </template>
          </el-table-column>
          <el-table-column
            prop="buildStatus"
            label="状态"
            :render-header="renderHeader"
            min-width="10%">
            <template slot-scope="scope">
              <el-tooltip class="item" effect="dark" :content="scope.row.failReason" placement="left" v-if="scope.row.publishStatus === 'FAIL' || scope.row.publishStatus === 'FAILURE'">
                <div style="cursor: pointer">
                  <i class="build_btn icon-status el-icon--right"
                     :class="{
                   'icon-status-loading':scope.row.publishStatus === 'ING',
                   'icon-status-no':scope.row.publishStatus === 'NO',
                   'icon-status-enter':scope.row.publishStatus === 'SUCCESS',
                   'icon-status-close':scope.row.publishStatus === 'FAILURE' || scope.row.publishStatus === 'FAIL'
                   }"></i>
                </div>
              </el-tooltip>
              <i class="build_btn icon-status el-icon--right" v-if="scope.row.publishStatus !== 'FAIL' && scope.row.publishStatus !== 'FAILURE'"
                 :class="{
                   'icon-status-loading':scope.row.publishStatus === 'ING',
                   'icon-status-no':scope.row.publishStatus === 'NO',
                   'icon-status-enter':scope.row.publishStatus === 'SUCCESS',
                   'icon-status-close':scope.row.publishStatus === 'FAILURE' || scope.row.publishStatus === 'FAIL'
                   }"></i>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Watch, Emit } from 'vue-property-decorator';

@Component
export default class BuildPop extends Vue {

  @Prop() public show!: boolean;
  @Prop() public list!: any;

  private dialogVisible: boolean = false;

  private handleClose(): void {
    this.dialogVisible = false;
  }

  @Watch('show')
  private setShow(): void {
    this.dialogVisible = this.show;
  }
  @Watch('dialogVisible')
  private setDialogVisible(): void {
    if (!this.dialogVisible) {
      this.changeShow();
    }
  }

  @Emit('changeShow')
  private changeShow(): number {
    return 0;
  }

  private renderHeader(h: any, { column, $index }: any, index: any): any {
    return h('span', {}, [
      h('span', {}, '状态 '),
      h('el-popover', { props: { placement: 'left', width: '200', trigger: 'hover', content: '失败时，鼠标经过状态可以查看失败原因' }}, [
        h('i', { slot: 'reference', class: 'el-icon-question'}, '')
      ])
    ]);
  }

}
</script>

<style lang="scss">
  .build-detail-pop{
    text-align: left;
    textarea{
      height: 150px;
    }
  }
</style>
