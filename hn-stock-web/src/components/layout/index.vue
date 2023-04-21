<template>
  <div class="app-wrapper">
    <el-container>
      <simple-menu/>
      <div class="main-container" style="width: 100%">
        <NavBar/>
        <AppMain/>
        <FooterBar/>
      </div>
    </el-container>
  </div>

</template>

<script>
  import { NavBar, AppMain, FooterBar} from './components/index.js'
  import Menu from './components/menu/Menu'
  import SimpleMenu from "./components/menu/Menu";

  export default {
    name: "Layout",
    components: {
      SimpleMenu,
      Menu,
      NavBar,
      AppMain,
      FooterBar
    }
  }
</script>

<style scoped>
  .app-wrapper {
    height: 100%;
    width: 100%;
  }
</style>
