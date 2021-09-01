<template>
  <div class="lamp">
    <span
      :class="{
        'is-opened': kongStatus === 'SUCCESS',
        'is-closed': kongStatus === 'FAILURE',
        'is-loading': kongStatus === 'ING',
      }"
    ></span>
  </div>
</template>

<script lang="ts">
  import { Component, Vue, Prop } from 'vue-property-decorator';
  @Component
  export default class Lamp extends Vue {
    @Prop() public kongStatus!: boolean;
  }
</script>

<style lang="scss">
  .lamp{
    display: inline-block;
    width: 20px;
    height: 20px;
    border-radius: 50%;
    span{
      display: block;
      width: 20px;
      height: 20px;
      // background-color: red;
      border-radius: 50%;
      background-repeat: no-repeat;
      background-size: 20px 20px;
    }
    span.is-opened{
     background-image: url('../../assets/lamp-ico.svg');
    }
    span.is-closed{
      background-image: url('../../assets/lamp-close-ico.svg');
    }
    span.is-loading{
      background-image: url('../../assets/lamp-loading-ico.svg');
      animation: lampLoading 1.4s infinite;
      background-position: center center;
    }
  }
  @keyframes lampLoading
  {
    0%{
      opacity: 0;
    }
    50%{
      opacity: 1;
    }
    100%{
      opacity: 0;
    }
  }

</style>
