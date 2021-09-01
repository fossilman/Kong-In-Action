<template>
  <div class="avue-contail" :class="{'avue--collapse':isCollapse}">
    <!-- <screenshot></screenshot> -->
    <div class="avue-header">
      <!-- <top ref="top" /> -->
      <top ref="top" @operateMenu="operateMenu"></top>
    </div>

    <div class="avue-layout">
      <div class="avue-left">
        <left />
      </div>
      <div class="avue-main">
        <div class="vue-router" id="avue-view">
          <div class="basic-container">
            <el-card>
              <router-view class="avue-view" />
            </el-card>
          </div>
          <el-footer class="avue-footer">
            <bottom></bottom>
          </el-footer>
        </div>
      </div>
    </div>
  </div>
</template>
<script lang="ts">
import { Component, Vue, Prop } from "vue-property-decorator";
import Top from "@/components/Top/Top.vue";
import Bottom from "@/components/Bottom/Bottom.vue";
import Left from "@/components/Left/Left.vue";
import { mapGetters } from "vuex";
@Component({
  components: {
    Top,
    Bottom,
    Left
  },
  computed: mapGetters(["isCollapse"])
})
export default class Layout extends Vue {
  private operateMenu(): void {
    this.$store.commit("SET_ISCOllAPSE");
  }
  private mounted(): void {
    this.setWindowsSize();
    window.onresize = () => {
      this.setWindowsSize();
    };
  }
  private setWindowsSize(): void {
    this.$store.commit("SET_SCREEN", this.getScreen());
    window.onresize = () => {
      setTimeout(() => {
        this.$store.commit("SET_SCREEN", this.getScreen());
      }, 0);
    };
  }
  private getScreen(): number {
    const width: number = document.body.clientWidth;
    if (width >= 1200) {
      return 3; // 大屏幕
    } else if (width >= 992) {
      return 2; // 中屏幕
    } else if (width >= 768) {
      return 1; // 小屏幕
    } else {
      return 0; // 超小屏幕
    }
  }
}
</script>


<style lang="scss">
.vue-router {
  height: 100%;
  overflow-y: auto;
  overflow-x: hidden;
  padding-top: 10px;
}
.basic-container {
  padding: 10px 6px;
  box-sizing: border-box;
  &--block {
    height: 100%;
    .el-card {
      height: 100%;
    }
  }
  .el-card {
    width: 100%;
  }
  &:first-child {
    padding-top: 0;
  }
  .el-card__body{
    min-height: calc(100vh - 180px);
  }
}
</style>
