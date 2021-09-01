<template>
  <div class="build-detail-pop">
    <el-dialog
      append-to-body
      title="build项目"
      :visible.sync="dialogVisible"
      width="800px"
      :before-close="handleClose">
      <div class="content">
        <el-table
          :data="list"
          stripe
          style="width: 100%">
          <el-table-column
            prop="gitlabHead"
            label="标题"
            min-width="60%">
          </el-table-column>
          <el-table-column
            prop="pushAuthor"
            label="作者"
            min-width="20%">
          </el-table-column>
          <el-table-column
            prop="buildStatus"
            label="状态"
            :render-header="renderHeader"
            min-width="20%">
            <template slot-scope="scope">
              <i class="build_btn icon-status el-icon--right"
                 :class="{
                   'icon-status-loading':scope.row.buildStatus === 'ING',
                   'icon-status-no':scope.row.buildStatus === 'NO',
                   'icon-status-enter':scope.row.buildStatus === 'SUCCESS',
                   'icon-status-close':scope.row.buildStatus === 'FAILURE',
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
