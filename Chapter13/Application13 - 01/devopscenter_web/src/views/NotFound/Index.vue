<template>
  <div class="author">
    <div class="value">404</div>
    <div class="name">页面未找到<em @click="back">返回</em> <em @click="quit">退出登录</em></div>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop } from "vue-property-decorator";
import { delLocalStorage } from "@/common/js/localStorage";
import { kong } from "@/server/kong";
@Component
export default class ServerList extends Vue {
  protected back(): void {
    this.$router.go(-1);
  }

  private mounted(): void {
    this.getKongStatus();
  }

  private quit(): void {
    delLocalStorage("token");
    delLocalStorage("name");
    this.$router.push({ path: "/login" });
  }

  private getKongStatus(): void {
    kong()
      .then((rs: any) => {
        console.log(rs);
      })
      .catch(err => {
        console.log(err);
      });
  }
}
</script>

<style lang="scss" scoped>
.author {
  text-align: center;
  font-size: 80px;
  .value {
    padding-top: 250px;
    font-size: 100px;
  }
  .name {
    padding-top: 20px;
    font-size: 18px;
  }
  em {
    font-style: normal;
    color: red;
    cursor: pointer;
  }
}
</style>
