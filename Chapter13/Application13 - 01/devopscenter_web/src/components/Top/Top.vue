<template>
    <div class="avue-top">
        <div class="top-bar__left">
            <div
                class="avue-breadcrumb"
                :class="[{ 'avue-breadcrumb--active': isCollapse }]"
                v-if="showCollapse"
            >
                <i class="el-icon-s-fold" @click="setCollapse"></i>
            </div>
        </div>
        <div class="top-bar__title"></div>
        <div class="top-bar__right">
            <span class="gateway">网关状态</span>

            <lamp
                class="top-bar-right lamp-ico"
                :kongStatus="kongStatus"
            ></lamp>
        </div>
    </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Emit } from 'vue-property-decorator';
import { getLocalStorage, delLocalStorage } from '@/common/js/localStorage';
import Lamp from '@/base/Lamp/Lamp.vue';
import { kong } from '@/server/kong';
import { mapGetters } from 'vuex';
import GLOBAL from '@/common/js/global-variable.ts';

@Component({
    components: {
        Lamp
    },
    computed: mapGetters(['kongStatus', 'isCollapse'])
})
export default class Left extends Vue {
    private sw: boolean = true;
    private activeIndex: string = '0';
    private showCollapse: boolean = true;
    // private isCollapse: boolean = false;

    private mounted(): void {
        this.getKongStatus();
        clearInterval(GLOBAL.KONGTIME);
        GLOBAL.KONGTIME = setInterval(() => {
            this.getKongStatus();
        }, 60000);
    }

    // @Emit("operateMenu")
    // private operateMenu(): number {
    //   return 0;
    // }

    private getKongStatus(): void {
        kong()
            .then((rs: any) => {
                let status: string = 'ING';
                if (rs.retval.kongStatus) {
                    status = 'SUCCESS';
                } else {
                    status = 'FAILURE';
                }
                this.$store.commit('setKongStatus', status);
            })
            .catch(err => {
                console.log(err);
            });
    }

    private beforeDestroy(): void {
        clearInterval(GLOBAL.KONGTIME);
    }

    private setCollapse(): void {
        this.$store.commit('SET_COLLAPSE');
    }
}
</script>

<style lang="scss" scope>
.avue-top {
    .top-bar-right {
        margin-right: 10px;
    }
    .gateway {
        margin-right: 5px;
    }
}
</style>
