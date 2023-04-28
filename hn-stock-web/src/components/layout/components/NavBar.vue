<template>
  <div class="navbar">
    <el-badge :value="12" class="item">
      <el-button size="small">评论</el-button>
    </el-badge>
    <el-badge :value="3" class="item">
      <el-button size="small">回复</el-button>
    </el-badge>
    <el-badge :value="1" class="item" type="primary">
      <el-button size="small">评论</el-button>
    </el-badge>
    <el-badge :value="2" class="item" type="warning">
      <el-button size="small">回复</el-button>
    </el-badge>
    <el-dropdown>
        <span class="el-dropdown-link">
           <el-button class="user-item" type="warning" icon="el-icon-user-solid" circle></el-button>
<!--          <i class="el-icon-caret-bottom el-icon&#45;&#45;right "></i>-->
      </span>
      <el-dropdown-menu slot="dropdown">
        <el-dropdown-item>查看</el-dropdown-item>
        <el-dropdown-item>新增</el-dropdown-item>
        <el-dropdown-item>删除</el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>


  </div>
</template>

<script>
  export default {
    name: "NavBar"
  }
</script>

<style scoped>
  .item {
    margin-top: 0px;
    margin-right: 30px;
  }
  .user-item{
    margin-top: 5px;
    margin-right: 10px;
  }
  .navbar {
    text-align: right;
    font-size: 12px;
    user-select: none;
    position: fixed;
    top: 0px;
    right: 0px;
    width: 100%;
    height: 50px;
    background-color: aquamarine;
  }
</style>
