<template>
  <el-container>
    <el-header style="padding: 0px;height: 48px">
      <NavBar/>
    </el-header>
    <el-container>
      <el-aside style="width: 200px">
        <Menu/>
      </el-aside>
      <el-container>
        <el-main style="padding: 0px;height: 689px;">
          <AppMain/>
        </el-main>
<!--        <el-footer style="padding: 0px;height: 25px;">-->
<!--          <FooterBar/>-->
<!--        </el-footer>-->
      </el-container>
    </el-container>
  </el-container>
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
</style>
