<template>
    <div class="bottom">
        <img src="@/assets/logo.png" />
        Fibonacci DevOps Platform
    </div>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
@Component
export default class Lamp extends Vue {
    private activeIndex: string = '1';
    private handleSelect(): void {
        console.log(1);
    }
}
</script>

<style lang="scss">
.bottom {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    font-size: 14px;
    color: #999;
    img {
        height: 30px;
        margin-right: 10px;
    }
}
</style>
