<template>
  <div class="home-list">
    <div class="breadcrumb">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>项目列表</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="add-btn">
      <el-button
        size="small"
        type="primary"
        @click="addBtn"
      >
        <li class="el-icon-plus"></li>
        <span>添加项目</span>
      </el-button>
    </div>
    <template>
      <el-table :data="list" v-loading="loading" stripe style="width: 100%">
        <el-table-column label="项目名称" width="200">
          <template slot-scope="scope">
            <div class="tpl-disabled" v-if="scope.row.del"></div>
            <el-button
              size="small"
              type="text"
              @click="productNameClick(scope.row.name,scope.row.id,scope.row.type)"
            >{{scope.row.name}}</el-button>
          </template>
        </el-table-column>
        <el-table-column label="pod数量" width="100">
          <template slot-scope="scope">
            <div class="tpl-disabled" v-if="scope.row.del"></div>
            {{scope.row.num ? scope.row.num : 0}}
          </template>
        </el-table-column>
        <el-table-column label="新版本占比" min-width="300">
          <template slot-scope="scope" class="slider">
            <div class="tpl-disabled" v-if="scope.row.del"></div>
            <el-button
              size="small"
              type="text"
              class="new"
              v-if="scope.row.num"
              @click="deployDetail(scope.row.listDeploys,scope.row.num)"
            >版本详情</el-button>
            <el-button
              size="small"
              type="text"
              style="width: 70px;padding-right: 10px;"
              v-if="!scope.row.num"
              disabled
            >版本详情</el-button>
            <div class="block" v-if="scope.row.listDeploys">
              <el-slider v-model="scope.row.listDeploys[0].vagrancy" :step="10" @change="handleSliderChange(scope.row)"></el-slider>
            </div>
            <div class="no-block" v-if="!scope.row.listDeploys"></div>
          </template>
        </el-table-column>
        <el-table-column width="128" label="build">
          <template slot-scope="scope">
            <div class="tpl-disabled" v-if="scope.row.del"></div>
            <el-button
              size="small"
              type="text"
              :disabled="scope.row.del?true:false"
              @click="build(scope.row.gitlabId,scope.row.id,scope.row.name,scope.row.buildStatus)"
            >
              <span style="line-height:15px">build</span>
              <i
                class="build_btn icon-status el-icon--right"
                :class="{
                 'icon-status-loading':scope.row.buildStatus === 'ING',
                 'icon-status-no':scope.row.buildStatus === 'NO',
                 'icon-status-enter':scope.row.buildStatus === 'SUCCESS',
                 'icon-status-close':scope.row.buildStatus === 'FAILURE',
                 }"
              ></i>
            </el-button>
          </template>
        </el-table-column>
        <el-table-column width="140" label="deploy">
          <template slot-scope="scope">
            <div class="tpl-disabled" v-if="scope.row.del"></div>
            <el-button
              size="small"
              type="text"
              :disabled="(!scope.row.successNum || scope.row.del)?true:false"
              @click="deploy(scope.row.listBuilds,scope.row.id,scope.row.listDeploys,scope.row.num,scope.row.name,scope.row.publishStatus)"
            >
              <span style="line-height:15px">deploy</span>
              <i
                class="icon-status el-icon--right"
                :class="{
                 'icon-status-loading':scope.row.publishStatus === 'ING',
                 'icon-status-no':scope.row.publishStatus === 'NO',
                 'icon-status-enter':scope.row.publishStatus === 'SUCCESS',
                 'icon-status-close':scope.row.publishStatus === 'FAILURE',
                 }"
              ></i>
            </el-button>
          </template>
        </el-table-column>
        <el-table-column fixed="right" width="120" label="操作" align="center">
          <template slot-scope="scope">
            <el-button
              size="small"
              type="text"
              @click="banBtn(scope.row.id, scope.row.name, 0)"
              v-if="scope.row.del"
            >启用</el-button>
            <el-button
              size="small"
              type="text"
              style="color:#f56c6c"
              @click="banBtn(scope.row.id, scope.row.name, 1)"
              v-if="!scope.row.del"
            >禁用</el-button>
            <el-button
              size="small"
              type="text"
              @click="editBtn(scope.row.id, scope.row)"
            >修改</el-button>
            <el-button
              size="small"
              type="text"
              @click="delBtn(scope.row.id, scope.row.name)"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </template>

    <build-pop
      :show="buildPopShow"
      @changeShow="buildChangeShow"
      @setBuildStatus="setBuildStatus"
      :gitlabId="gitlabId"
      :appId="appId"
      :appName="appName"
    ></build-pop>

    <deploy-pop
      :show="deployPopShow"
      @changeShow="deployChangeShow"
      @setDeployStatus="setDeployStatus"
      :currentListDeploys="currentListDeploys"
      :appId="appId"
      :appName="appName"
    ></deploy-pop>

    <deploy-detail-pop
      :show="deployDetailPopShow"
      @changeShow="deployDetailChangeShow"
      :list="deployDetailList"
    ></deploy-detail-pop>

    <home-edit :homeData="homeData" @update="getPublishList" ref="homeEdit"></home-edit>

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
import { Component, Vue, Prop } from 'vue-property-decorator';
import BuildPop from '@/views/Project/BuildPop/BuildPop.vue';
import DeployDetailPop from '@/views/Project/DeployDetailPop/Index.vue';
import DeployPop from '@/views/Project/DeployPop/Index.vue';
import HomeEdit from '@/components/Home/HomeEdit.vue';
import { mapGetters } from 'vuex';
import {
    publishList,
    publishCheck,
    buildCheck,
    publishDel,
    publishBan,
    getProjectsId,
} from '@/server/publish';
@Component({
    components: {
        BuildPop,
        DeployPop,
        DeployDetailPop,
        HomeEdit
    },
    computed: mapGetters(['userInfo'])
})
export default class Home extends Vue {
    private buildPopShow: boolean = false;
    private deployPopShow: boolean = false;
    private deployDetailPopShow: boolean = false;

    private current: number = 1;
    private size: number = 20;
    private total: number = 0;
    private list: any = [];
    private loading: boolean = false;

    private currentListDeploys: any = [];

    private appId: number = 0;
    private gitlabId: number = 0;
    private appName: string = '';

    // private buildDetailList: any[] = []; // 当前build详情弹框
    private deployDetailList: any[] = []; // 当前deploy详情弹框

    private buildStatusTime: any = null;
    private deployStatusTime: any = null;

    private homeData!: HomeForm;

    private mounted(): void {
        this.loading = true;
        this.getPublishList();
    }

    // 版本占比滑块发生改变
    private handleSliderChange(data: any): void {
        console.log(data);
        // do something
    }

    // 拉取项目列表
    private getPublishList(): void {
        this.loading = true;
        clearInterval(this.deployStatusTime);
        clearInterval(this.buildStatusTime);
        publishList(this.current, this.size)
            .then((rs: any) => {
                const list: any[] = rs.retval.publishList;

                // // 组控制数据显示
                // let groupName: string = '';
                // // @ts-ignore
                // console.log(this.userInfo.groups);
                // // @ts-ignore
                // this.userInfo.groups.forEach((item)=> {
                //   if(item.id === 2) {
                //     groupName = item.groupName;
                //   }
                // });
                //
                // list = list.filter((item) => {
                //   if(groupName === "Developer") {
                //     if (item.name === 'confplus' || item.name == 'schedulerplus') {
                //       return true;
                //     } else {
                //       return false;
                //     }
                //     return true;
                //   } else {
                //     return true;
                //   }
                // });

                list.forEach(item => {
                    // build状态灯默认显示
                    if (item.listBuilds) {
                        const buildStatus: string =
                            item.listBuilds[0].buildStatus;
                        if (buildStatus === 'ING' || buildStatus === 'NULL') {
                            item.buildStatus = 'ING';
                            // 初次加载为ING 需要轮询检查
                            this.getBuildCheck(item.listBuilds[0].id, item.id);
                        } else if (buildStatus === 'SUCCESS') {
                            item.buildStatus = buildStatus;
                        } else if (
                            buildStatus === 'FAILURE' ||
                            buildStatus === 'FAIL'
                        ) {
                            item.buildStatus = 'FAILURE';
                        } else {
                            item.buildStatus = 'NO';
                        }
                        // 统计成功的build记录
                        item.successNum = 0;
                        item.listBuilds.forEach((info: any) => {
                            if (info.buildStatus === 'SUCCESS') {
                                item.successNum++;
                            }
                        });
                    } else {
                        item.buildStatus = 'NO';
                    }

                    // deploy 状态灯默认显示
                    if (item.listDeploys) {
                        let deployPublish: string = 'SUCCESS';
                        // 处理列表灯显示状态
                        const publishStatus: string =
                            item.listDeploys[0].publishStatus;
                        if (
                            publishStatus === 'ING' &&
                            deployPublish !== 'FAILURE'
                        ) {
                            deployPublish = 'ING';
                        } else if (
                            publishStatus === 'FAILURE' ||
                            publishStatus === 'FAIL'
                        ) {
                            deployPublish = 'FAILURE';
                        }
                        item.publishStatus = deployPublish;
                        // ing 中执行轮询
                        // ING状态ING去轮询加载
                        if (deployPublish === 'ING') {
                            this.getDeployCheck(item.listDeploys[0].id, item.id);
                        }
                    } else {
                        item.publishStatus = 'NO';
                    }
                });
                this.total = rs.retval.total;
                this.loading = false;
                this.list = list;
            })
            .catch(err => {
                console.log(err);
                this.$message({
                    message: err.errmsg,
                    type: 'warning'
                });
            });
    }

    // build按钮点击
    private build(
        gitlabId: number,
        id: number,
        name: string,
        buildStatus: string
    ): void {
        if (buildStatus === 'ING') {
            this.$message({
                message: '应用正在构建中',
                type: 'warning'
            });
            return;
        }
        this.buildPopShow = true;
        this.gitlabId = gitlabId;
        this.appId = id;
        this.appName = name;
    }

    // deploy 按钮点击
    private deploy(
        info: any,
        id: number,
        listDeploys: any[],
        num: number,
        name: string,
        publishStatus: string
    ): void {
        if (publishStatus === 'ING') {
            this.$message({
                message: '应用正在发布中',
                type: 'warning'
            });
            return;
        }
        this.deployPopShow = true;
        this.appId = id;
        this.appName = name;
        // const list: any[] = [];
        // const newList: any[] = [];
        // for (let i: number = 0; i < num; i++) {
        //   newList.push(serverList[i]);
        // }
        if (listDeploys) this.currentListDeploys = listDeploys;

        // // 传入build 成功的列表
        // info.forEach((item: any) => {
        //   if (item.buildStatus === 'SUCCESS' ) {
        //     list.push(item);
        //   }
        // });
        // this.buildDetailList = list;
    }

    // build状态按钮检查灯设置
    private setBuildStatus(info: any): void {
        if (info.status !== 'ING') {
            // 不是在加载中，则更新列表数据
            this.getPublishList();
        } else {
            // 加载中更新灯的为加loading中
            this.list.forEach((item: any) => {
                if (info.id === item.id) {
                    item.buildStatus = info.status;
                    return false;
                }
            });
        }
    }

    // deploy状态按钮检查灯设置
    private setDeployStatus(info: any): void {
        if (info.status !== 'ING') {
            // 成功则更新列表数据
            this.getPublishList();
            console.log(1);
        } else {
            console.log(2);
            // 加载中更新灯的为加loading中
            this.list.forEach((item: any) => {
                console.log(info.id);
                if (info.id === item.id) {
                    item.publishStatus = info.status;
                    return false;
                }
            });
        }
    }

    // build检查请求
    private getBuildCheck(id: number, appid: number): void {
        clearInterval(this.buildStatusTime);
        this.buildStatusTime = setInterval(() => {
            buildCheck(id)
                .then((rs: any) => {
                    const status: string = rs.retval.status;
                    let currentStatus: string = '';
                    if (status === 'ING') {
                        currentStatus = 'ING';
                    } else if (status === 'SUCCESS') {
                        currentStatus = status;
                        clearInterval(this.buildStatusTime);
                    } else {
                        currentStatus = 'FAILURE';
                        clearInterval(this.buildStatusTime);
                    }
                    this.setBuildStatus({
                        id: this.appId,
                        status: currentStatus
                    });
                })
                .catch(err => {
                    this.$message({
                        message: err.errmsg,
                        type: 'warning'
                    });
                });
        }, 20000);
    }

    // deploy检查请求
    private getDeployCheck(id: number, appid: number): void {
        clearInterval(this.deployStatusTime);
        this.deployStatusTime = setInterval(() => {
            publishCheck(id)
                .then((rs: any) => {
                    const status: any = rs.retval.deployStatus;
                    let currentStatus: string = '';
                    if (status === 'ING' || status === 'null') {
                        currentStatus = 'ING';
                    } else if (status === 'SUCCESS') {
                        currentStatus = status;
                        clearInterval(this.deployStatusTime);
                    } else if (status === 'FAILURE' || status === 'FAIL') {
                        currentStatus = 'FAILURE';
                        clearInterval(this.deployStatusTime);
                    }

                    this.setDeployStatus({
                        id: this.appId,
                        status: currentStatus
                    });
                })
                .catch(err => {
                    this.$message({
                        message: err.errmsg,
                        type: 'warning'
                    });
                });
        }, 20000);
    }

    // 翻页
    private currentChange(current: number): void {
        this.current = current;
        this.getPublishList();
    }

    // deploy详情按钮点击
    private deployDetail(info: any, num: number): void {
        // const newInfo: any = JSON.parse(JSON.stringify(info));
        this.deployDetailPopShow = true;
        const list: any[] = [];

        if (info.length) {
            list.push(info[0]);
        }

        this.deployDetailList = list;
    }

    private addBtn(): void {
        this.$router.push({
            path: '/project/save'
        });
    }

    private delBtn(id: number, name: string): void {
        this.$confirm('是要删除' + name + '项目吗?', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        })
            .then(() => {
                publishDel(id)
                    .then((rs: any) => {
                        this.getPublishList();
                    })
                    .catch((err: any) => {
                        this.$message({
                            message: err.errmsg,
                            type: 'warning'
                        });
                    });
            })
            .catch(() => {
                console.log(1);
            });
    }

    private editBtn(id: number, row: HomeForm): void {
        getProjectsId(row.id).then((res: any) => {
            row.routeType = res.retval.gatewayType;
            (this.$refs.homeEdit as HomeEdit).openHomeShow(row);
        });
    }

    private banBtn(id: number, name: string, status: number): void {
        this.$confirm(`是要${status ? '禁用' : '启用'}${name}项目吗?`, '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        })
            .then(() => {
                publishBan(id, status)
                    .then((rs: any) => {
                        this.getPublishList();
                    })
                    .catch((err: any) => {
                        this.$message({
                            message: err.errmsg,
                            type: 'warning'
                        });
                    });
            })
            .catch(() => {
                console.log(1);
            });
    }

    private buildChangeShow(id: number): void {
        this.buildPopShow = false;
    }

    private deployChangeShow(): void {
        this.deployPopShow = false;
    }

    private deployDetailChangeShow(id: number): void {
        this.deployDetailPopShow = false;
    }

    private productNameClick(name: string, id: string, type: string): void {
        this.$router.push({
            path: `/application/${name}/id/${id}/${type}`
        });
    }

    private destroyed(): void {
        clearInterval(this.deployStatusTime);
        clearInterval(this.buildStatusTime);
    }
}
</script>

<style lang="scss">
.home-list {
    .main-title {
        text-align: left;
        padding: 20px 10px;
    }
    .cell {
        display: flex;
        align-items: center;
        width: 90%;
        .new {
            width: 70px;
            padding-right: 10px;
            color: #409eff;
            cursor: pointer;
        }
        .name {
            color: #409eff;
            cursor: pointer;
        }

        .block {
            flex: 1;
        }

        .old {
            color: olivedrab;
            padding-left: 20px;
        }
    }
    .no-block {
        width: 85%;
        height: 6px;
        border-radius: 5px;
        background-color: #e4e7ed;
    }
    .pagination {
        padding-top: 30px;
    }
    .add-btn {
        padding: 20px 0;
        text-align: left;
        border-bottom: 1px solid #dcdcdc;
        margin-bottom: 15px;
    }

    .tpl-disabled {
        position: absolute;
        content: ' ';
        position: absolute;
        left: 0;
        top: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(255, 255, 255, 0.5);
        z-index: 1002;
    }
}
.breadcrumb {
    height: 30px;
    padding: 0 10px 10px 10px;
    border-bottom: 1px solid #dcdcdc;
}
</style>
