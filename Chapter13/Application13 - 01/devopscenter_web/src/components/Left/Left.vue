<template>
  <div class="avue-sidebar">
    <div :class="{'avue-logo':screen>1||isCollapse}" @click="home">
      <transition name="fade" v-if="keyCollapse">
        <span class="avue-logo_subtitle" key="0">F</span>
      </transition>
      <transition-group name="fade" v-if="!keyCollapse">
        <span class="avue-logo_title" key="1">Fibonacci DevOps Platform</span>
      </transition-group>
    </div>

    <el-scrollbar style="height:100%">
      <el-menu
        class="el-menu-vertical-demo"
        :default-active="activeIndex"
        :collapse="keyCollapse"
        :show-timeout="200"
        mode="vertical"
        :router="true"
        text-color="#000"
        background-color="#20222a"
      >
        <el-menu-item index="/project">
          <i class="el-icon-news"></i>
          <span slot="title">项目列表</span>
        </el-menu-item>

        <el-menu-item index="/server">
          <i class="el-icon-setting"></i>
          <span slot="title">服务器列表</span>
        </el-menu-item>
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { mapGetters } from 'vuex';

@Component({
    computed: mapGetters(['keyCollapse', 'screen', 'isCollapse'])
})
export default class Left extends Vue {
    get activeIndex(): string {
        let index: string = this.$route.matched[0].path;
        if (index !== '/server') {
            index = '/project';
        }
        return index;
    }
    private home(): void {
        this.$router.push('/project/index');
    }
}
</script>

<style lang="scss" scoped>
.fade-leave-active {
    transition: opacity 0.2s;
}
.fade-enter-active {
    transition: opacity 2.5s;
}
.fade-enter,
.fade-leave-to {
    opacity: 0;
}
.avue-logo {
    position: fixed;
    top: 0;
    left: 0;
    width: 240px;
    height: 64px;
    line-height: 64px;
    background-color: #20222a;
    font-size: 20px;
    overflow: hidden;
    box-sizing: border-box;
    box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.15);
    color: rgba(255, 255, 255, 0.8);
    z-index: 1024;
    &_title {
        display: block;
        text-align: center;
        font-weight: 300;
        font-size: 20px;
    }
    &_subtitle {
        display: block;
        text-align: center;
        font-size: 18px;
        font-weight: bold;
        color: #fff;
    }
    .avue-logo_title {
        font-size: 18px;
    }
}
</style>
