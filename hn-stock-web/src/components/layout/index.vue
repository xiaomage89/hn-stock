<template>
  <div class="app-wrapper">
    <el-container>
      <el-aside>
          <Menu/>
      </el-aside>
      <el-container>
        <el-header>
          <NavBar/>
        </el-header>
        <el-main>
          <AppMain/>
        </el-main>
        <el-footer>
          <FooterBar/>
        </el-footer>
      </el-container>
    </el-container>
  </div>

</template>

<script>
  import {Menu, NavBar, AppMain, FooterBar} from './components/index.js'

  export default {
    name: "Layout",
    components: {
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
