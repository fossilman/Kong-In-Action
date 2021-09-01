<template>
  <div class="deploy-pop">
    <el-dialog
      class="deploy-dialog"
      :title="appName + '发布'"
      :visible.sync="dialogVisible"
      width="830px"
      :before-close="handleClose"
      append-to-body
    >
      <div
        v-loading="loading"
        element-loading-spinner="el-icon-loading"
        element-loading-text="正在发布中"
      >
        <div class="content">
          <div class="switch-model">
            <el-switch
              style="display: block"
              v-model="deployModel"
              active-color="#13ce66"
              inactive-color="#409EFF"
              active-text="新老版本兼容"
              inactive-text="100%新流量"
            ></el-switch>
          </div>
          <el-form ref="form" :model="form" :label-position="labelPosition" label-width="120px">
            <el-form-item label="版本描述:">
              <el-select
                v-model="form.gitlabDesc"
                placeholder="请选择版本"
                style="width: 100%"
                @change="changeSelect"
              >
                <el-option
                  class="gitlab-desc"
                  v-for="(item,index) in list"
                  :label="item.gitlabDesc"
                  :value="index"
                  :key="item.id"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="待发布版本:">
              <el-input v-model="form.harborKey" :disabled="true"></el-input>
            </el-form-item>
            <el-form-item label="版本构建者:">
              <el-input v-model="form.createBy" :disabled="true"></el-input>
            </el-form-item>
            <el-form-item label="待发布次新版本:" v-if="deployModel">
              <el-input v-model="form.subNewHarborKey" :disabled="true"></el-input>
            </el-form-item>
            <el-form-item label="待发布版本占比:" v-if="deployModel" :disabled="true">
              <el-input v-model="form.vagrancy" style="width: 100px"></el-input>%
            </el-form-item>
            <el-form-item label="选择服务器:">
              <el-transfer v-model="form.servers" :titles="['服务器列表', '已经选服务器']" :data="serverList"></el-transfer>
            </el-form-item>
          </el-form>
        </div>
        <div class="bootom">
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button @click="submit(false)" :loading="loadingPrePublish">预发布</el-button>
          <el-button type="primary" @click="submit(true)">发布</el-button>
        </div>
      </div>
      <span slot="footer" class="dialog-footer"></span>
    </el-dialog>
    <el-dialog
      append-to-body
      :title="appName + '项目预发布'"
      :visible.sync="visiblePrePublish"
      width="750px"
      :before-close="closePrePublish"
    >
      <div v-html="contentPrePublish" class="prePublishPop"></div>
    </el-dialog>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Watch, Emit } from "vue-property-decorator";
import { serversSurplusList } from "@/server/servers.ts";
import { publish, prePublish, publishCheck } from "@/server/publish";

@Component
export default class DeployPop extends Vue {
  @Prop() public show!: boolean;
  @Prop() public currentListDeploys!: any;
  @Prop() public appId!: number;
  @Prop() public appName!: string;
  private loading: boolean = false;
  private labelPosition: string = "left";
  private dialogVisible: boolean = false;
  private deployModel: boolean = false;
  private deployStatusTime: any = null;

  private list: any[] = [];

  private visiblePrePublish: boolean = false; // 预发布弹框
  private contentPrePublish: string = "";
  private loadingPrePublish: boolean = false;

  private info: any = {};
  private form: any = {
    harborKey: 0,
    gitlabHead: "",
    createBy: "",
    vagrancy: "",
    num: "",
    subNewHarborKey: "",
    servers: []
  };
  private serverList: any[] = [];
  private beforeServers: any[] = [];

  private getServerList(): void {
    this.serverList = [];
    serversSurplusList(this.appId, this.appName)
      .then((rs: any) => {
        // const serverList: any[] = rs.retval.serverList;
        const buildList: any[] = [];
        // 传入build 成功的列表
        if (rs.retval.listBuilds) {
          rs.retval.listBuilds.forEach((item: any) => {
            if (item.buildStatus === "SUCCESS") {
              buildList.push(item);
            }
          });
        }
        this.list = buildList;

        this.info = this.list[0];
        this.form.harborKey = this.info.harborKey;
        this.form.gitlabDesc = this.info.gitlabDesc;
        this.form.createBy = this.info.createBy;
        // 如何不是第一次发布，设置次新版本
        if (this.currentListDeploys.length) {
          this.form.subNewHarborKey = this.currentListDeploys[0].gitlabVersion;
        }

        this.beforeServers = rs.retval.beforeServers;
        const list: any[] = rs.retval.serverList;
        const newList: any[] = [];
        list.forEach(item => {
          newList.push({
            key: item.id,
            label: item.name,
            ip: item.ip,
            port: item.port,
            name: item.name
          });
        });
        this.serverList = newList;

        this.form.servers = [];
        // 添加上回选择的服务器
        this.beforeServers.forEach((info: any) => {
          this.serverList.forEach((item: any) => {
            if (info.serverIp === item.ip) {
              this.form.servers.push(item.key);
            }
          });
        });
      })
      .catch((err: any) => {
        console.log(err);
        this.$message({
          message: err.errmsg,
          type: "warning"
        });
      });
  }

  private handleClose(): void {
    this.dialogVisible = false;
  }

  // 切换版本
  private changeSelect(index: number): void {
    this.info = this.list[index];
    this.form.harborKey = this.info.harborKey;
    this.form.gitlabHead = this.info.gitlabHead;
    this.form.createBy = this.info.createBy;
  }
  // deploy弹出监听
  @Watch("show")
  private setShow(): void {
    this.dialogVisible = this.show;
    if (this.dialogVisible) {
      this.getServerList();
      this.form.servers = [];
    }
  }
  @Watch("dialogVisible")
  private setDialogVisible(): void {
    if (!this.dialogVisible) {
      this.changeShow();
    }
  }

  @Watch("deployModel")
  private setVagrancy(newDeployModel: boolean): void {
    this.form.vagrancy = "";
    // if (newDeployModel && this.currentServerList[0].publishVag < 100) {
    //   this.form.vagrancy = this.currentServerList[0].publishVag;
    // } else {
    //   this.form.vagrancy = '';
    // }
    // if (newDeployModel) {
    //   this.form.servers = [];
    // } else {
    //   this.setCurrentServerList();
    // }
  }

  @Emit("changeShow")
  private changeShow(): number {
    return 0;
  }

  private submit(type: boolean): void {
    if (
      (this.deployModel && !this.form.vagrancy) ||
      (this.deployModel &&
        (this.form.vagrancy >= 100 || this.form.vagrancy <= 0))
    ) {
      this.$message({
        message: "请输入正确的版本占比",
        type: "warning"
      });
      return;
    }

    if (this.deployModel) {
      if (this.form.vagrancy % 10 !== 0) {
        this.$message({
          message: "版本占比必须为10的倍数",
          type: "warning"
        });
        return;
      }
    }

    if (!this.form.servers.length) {
      this.$message({
        message: "请选择需要发布的服务器",
        type: "warning"
      });
      return;
    }

    // 发布类型
    let publishType: string = "SIMPLE";
    if (this.deployModel) {
      publishType = "COMPATIBILITY";
      if (!this.form.subNewHarborKey) {
        this.$message({
          message: "第一次发布不能选择新老版本兼容",
          type: "warning"
        });
        return;
      }
    }

    // 选择的服务器列表
    const selectServerList: any = [];
    this.form.servers.forEach((item: any) => {
      this.serverList.forEach((obj: any) => {
        if (item === obj.key) {
          selectServerList.push({
            ip: obj.ip,
            port: obj.port,
            name: obj.name
          });
        }
      });
    });

    if (this.loading) return; // 提交锁
    if (type) {
      this.loading = true;
      clearTimeout(this.deployStatusTime);
      // 发布
      publish(
        this.info.id,
        this.info.listId,
        this.form.harborKey,
        this.info.gitlabDesc,
        selectServerList,
        this.info.pushAuthor,
        publishType,
        this.form.subNewHarborKey,
        this.form.vagrancy
      )
        .then((rs: any) => {
          this.dialogVisible = false;
          this.checkDeploy(rs.retval.deployId);
          this.loading = false;
        })
        .catch(err => {
          this.loading = false;
          this.$message({
            message: err.errmsg,
            type: "warning"
          });
        });
    } else {
      this.loadingPrePublish = true;
      // 预发布
      prePublish(
        this.info.id,
        this.info.listId,
        this.form.harborKey,
        this.info.gitlabDesc,
        selectServerList,
        this.info.pushAuthor,
        publishType,
        this.form.subNewHarborKey,
        this.form.vagrancy
      )
        .then((rs: any) => {
          this.loadingPrePublish = false;
          // this.dialogVisible = false;
          this.visiblePrePublish = true;
          this.contentPrePublish = rs.retval.remark;
        })
        .catch(err => {
          this.loadingPrePublish = false;
          this.$message({
            message: err.errmsg,
            type: "warning"
          });
        });
    }
  }

  private closePrePublish(): void {
    this.visiblePrePublish = false;
  }

  // 检查deploy状态
  private checkDeploy(id: number): void {
    // 报错都是不停止轮询
    this.setDeployStatus("ING");
    // 2分钟后查询状态
    setTimeout(() => {
      this.getDeployStatus(id);
    }, 20000);
  }

  // 获取Deploy状态数据
  private getDeployStatus(id: number): void {
    clearTimeout(this.deployStatusTime);

    // 超时或者报错，1分钟后自动再去发送请求
    this.deployStatusTime = setTimeout(() => {
      this.getDeployStatus(id);
    }, 20000);

    publishCheck(id)
      .then((rs: any) => {
        const status: any = rs.retval.deployStatus;
        if (status === "ING") {
          clearInterval(this.deployStatusTime);
          setTimeout(() => {
            this.getDeployStatus(id);
          }, 20000);
          this.setDeployStatus("ING");
        } else if (status === "SUCCESS") {
          clearInterval(this.deployStatusTime);
          this.setDeployStatus(status);
        } else if (status === "FAILURE" || status === "FAID") {
          clearInterval(this.deployStatusTime);
          this.setDeployStatus("FAILURE");
        }
      })
      .catch(err => {
        this.$message({
          message: err.errmsg,
          type: "warning"
        });
      });
  }

  @Emit("setDeployStatus")
  private setDeployStatus(status: string): any {
    return {
      id: this.appId,
      status
    };
  }
}
</script>

<style lang="scss">
.gitlab-desc {
  max-width: 640px;
}

.deploy-dialog {
  text-align: left;
  .switch-model {
    padding-bottom: 20px;
    margin-bottom: 20px;
    text-align: right;
  }
  .el-checkbox,
  .el-checkbox__input {
    // display: block;
  }
  .el-transfer-panel__body .el-checkbox,
  .el-transfer-panel__body .el-checkbox__input {
    display: block;
  }
  .el-transfer-panel {
    width: 242px;
  }
  .prePublishPop {
    line-height: 1.8em;
    p {
      padding-top: 10px;
    }
  }
  .bootom {
    text-align: right;
  }
  .el-loading-mask {
    background-color: rgba(255, 255, 255, 0.7);
    .el-icon-loading {
      font-size: 30px;
    }
    .el-loading-text {
      margin-top: 10px;
    }
  }
  .el-loading-parent--relative {
    display: block;
  }
}
</style>
