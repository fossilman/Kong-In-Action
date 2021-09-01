<template>
  <div class="application">
    <div class="main">
      <div class="breadcrumb">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item :to="{ path: '/' }">项目列表</el-breadcrumb-item>
          <el-breadcrumb-item>application:{{application}}「id:{{id}}」- {{routerName}}</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
      <div class="toptabs">
        <el-tabs v-model="activeName" type="card" @tab-click="handleTabClick">
          <el-tab-pane
            v-for="(item,index) in routerList"
            :key="index"
            :label="item.tabName"
            :name="item.name"
          ></el-tab-pane>
        </el-tabs>
      </div>
      <keep-alive>
        <router-view />
      </keep-alive>
    </div>
  </div>
</template>
<script lang="ts">
import { Component, Vue, Watch } from "vue-property-decorator";
import { tablist, TabList } from "@/config/applicationTabConfig";
import { ElTabs } from "element-ui/types/tabs";
import { ElTabPane } from "element-ui/types/tab-pane";

@Component
export default class ApplicationConf extends Vue {
  protected application: string = "";
  protected id: number | string = "";
  protected activeName?: string | null = "";
  private tablist: typeof tablist = tablist;
  private routerList: TabList[] = [];
  private routerName: string = "";
  private type: string = "";

  private mounted(): void {
    this.application = this.$route.params.application;
    this.id = this.$route.params.id;
    this.type = this.$route.params.type;
    this.activeName = this.$route.name;
    this.setRouterName();
    this.getRouterType();
  }

  private getRouterType(): void {
    this.tablist.forEach((item: TabList, index: number) => {
      if (
        item.path === "routingCenter" &&
        this.type !== "server_outer" &&
        this.type !== "server_inner"
      ) {
        return;
      }
      this.routerList.push(item);
    });
  }

  @Watch("activeName")
  private setRouterName(): void {
    const findRes: TabList | undefined = this.tablist.find(
      e => e.name === this.$route.name
    );
    this.routerName = findRes ? findRes.tabName : "";
  }

  private handleTabClick(tab: ElTabPane, event: Event): void {
    if (tab.name === this.$route.name) {
      return;
    }
    this.$router.replace({
      name: tab.name
    });
  }
}
</script>
<style lang="scss" scoped>
.application {
  width: 100%;
  .main {
    position: relative;
    width: 100%;

    .breadcrumb {
      height: 20px;
      padding: 0 10px 10px 10px;
      // border-bottom: 1px solid #dcdcdc;
    }
  }
  .toptabs {
    margin-top: 20px;
  }
}
</style>
