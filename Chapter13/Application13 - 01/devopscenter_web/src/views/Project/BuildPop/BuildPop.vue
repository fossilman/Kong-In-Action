<template>
  <div class="build-pop">
    <el-dialog
      append-to-body
      :title="appName + '构建'"
      :visible.sync="dialogVisible"
      width="750px"
      :before-close="handleClose">
      <div class="content"
           v-loading="loading"
           element-loading-spinner="el-icon-loading"
           element-loading-text="正在构建中"
      >
        <el-form ref="form" :model="form" :label-position="labelPosition" label-width="90px">
          <el-form-item label="版本描述:">
            <el-select v-model="form.headDesc" placeholder="请选择版本" style="width: 100%" @change="changeSelect" popper-class='build-pop-select'>
              <el-option v-for="(item,index) in list" :label="item.headDesc" :value="index" :key="item.id"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="作 者:">
            <el-input v-model="form.author" :disabled="true"></el-input>
          </el-form-item>
          <el-form-item label="构建版本:">
            <el-input v-model="form.version" :disabled="true"></el-input>
          </el-form-item>
          <el-form-item label="版本信息:" prop="desc">
            <el-input type="textarea" v-model.trim="form.bodyDesc" class="int" rows="4"></el-input>
          </el-form-item>
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer" v-loading="loading" element-loading-spinner="no">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submit">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Watch, Emit } from 'vue-property-decorator';
import { gitLabList, jenkins, buildCheck } from '@/server/publish.ts';

@Component
export default class BuildPop extends Vue {
  @Prop() public show!: boolean;
  @Prop() public gitlabId!: number;
  @Prop() public appId!: number;
  @Prop() public appName!: string;
  private labelPosition: string = 'left';
  private dialogVisible: boolean = false;
  private buildStatusTime: any = {};
  private loading: boolean = false;
  private list: any[] = [];
  private info: any = {};
  private form: any = {
    version: '',
    headDesc: '',
    bodyDesc: '',
    author: ''
  };

  private handleClose(): void {
    this.dialogVisible = false;
  }

  @Watch('show')
  private setShow(): void {
    this.dialogVisible = this.show;
    if (this.dialogVisible) {
      this.form = {
        version: '',
        headDesc: '',
        bodyDesc: '',
        author: ''
      };
      this.getGitLabList();
    }
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

  private getGitLabList(): void {
    gitLabList(this.gitlabId).then((rs: any) => {
      this.list = rs.retval.gitlabInfo;
      this.form.version = this.list[0].version;
      this.form.headDesc = this.list[0].headDesc;
      this.form.bodyDesc = this.list[0].bodyDesc;
      this.form.author = this.list[0].author;
    }).catch(err => {
      this.$message({
        message: err.errmsg,
        type: 'warning'
      });
    });
  }
  // 切换版本
  private changeSelect(index: number): void {
    this.info = this.list[index];
    this.form.version = this.info.version;
    this.form.headDesc = this.info.headDesc;
    this.form.bodyDesc = this.info.bodyDesc;
    this.form.author = this.info.author;
  }

  private submit(): void {
    if (this.loading) return; // 提交锁
    this.loading = true;
    if (!this.form.bodyDesc) {
      this.$message({
        message: '请输入ip地址',
        type: 'warning'
      });
      return;
    }
    jenkins(
      this.gitlabId,
      this.form.version,
      this.form.headDesc,
      // this.form.bodyDesc,
      this.form.bodyDesc + '@' + this.form.version,
      this.form.author,
      this.appId
    ).then((rs: any) => {
      this.dialogVisible = false; // 隐蔽build框
      this.loading = false;
      this.checkBuild(rs.retval.id);
    }).catch(err => {
      this.loading = false;
      // this.dialogVisible = false;
      this.$message({
        message: err.errmsg,
        type: 'warning'
      });
    });
  }

  // 检查build状态
  private checkBuild(id: number): void {
    // 报错都是不停止轮询
    this.setBuildStatus('ING');
    setTimeout(() => {
      this.buildStatusTime[id] = setInterval(() => {
        this.getbuildStatus(id);
      }, 10000);
    }, 10000);
  }

  // 获取build状态数据
  private getbuildStatus(id: number): void {
    buildCheck(id).then((rs: any) => {
      const status: string = rs.retval.status;
      if (status === 'ING' || status === 'null') {
        this.setBuildStatus('ING');
      } else if (status === 'SUCCESS' ) {
        clearInterval(this.buildStatusTime[id]);
        this.setBuildStatus('SUCCESS');
      } else if (status === 'FAILURE' || status === 'FAIL') {
        clearInterval(this.buildStatusTime[id]);
        this.setBuildStatus('FAILURE');
      }
    }).catch(err => {
      this.$message({
        message: err.errmsg,
        type: 'warning'
      });
    });
  }

  @Emit('setBuildStatus')
  private setBuildStatus(status: string): any {
    return {
      id: this.appId,
      status
    };
  }

}
</script>

<style lang="scss" scoped>
  .build-pop{
    text-align: left;
    /deep/.el-loading-mask{
      background-color: rgba(255,255,255,0.7);
      .el-icon-loading {
        font-size: 30px;
      }
      .el-loading-text{
        margin-top: 10px;
      }
    }
    /deep/.el-loading-parent--relative{
      display: block;
    }
  }
</style>
<style lang='scss'>
  .build-pop-select{
    max-width: 200px;
  }
</style>
