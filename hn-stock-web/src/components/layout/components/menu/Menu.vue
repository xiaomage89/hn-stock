<template>
  <div class="menu">
    <template v-for="item in routes">
      <sub-menu
        v-if="item.children && item.children.length > 1"
        :item="item"
        :basePath="item.path"
        :key="item.path"
      />
      <menu-item
        v-else
        :path="item.path"
        :title="item.meta.title"
        :key="item.path"
      />
    </template>
  </div>
</template>

<script>
  import SubMenu from './SubMenu'
  import MenuItem from './MenuItem'

  export default {
    name: "Menu",
    components: {MenuItem, SubMenu},
    computed: {
      routes() {
        return this.$router.options.routes
      }
    },
    provide() {
      return {
        rootMenu: this // 把自己提供给子组件
      }
    },
    props: {},
    data() {
      return {
        openedSubMenus: [] // 已展开的子菜单index
      }
    },
    methods: {
      // 处理子菜单点击事件
      handleClickSubMenu(basePath) {
        if (this.openedSubMenus.includes(basePath)) {
          this.closeSubMenu(basePath)
        } else {
          this.openSubMenu(basePath)
        }
      },

      // 打开子菜单
      openSubMenu(basePath){
        this.openedSubMenus.push(basePath)
      },

      // 关闭子菜单
      closeSubMenu(basePath) {
        this.openedSubMenus.splice(this.openedSubMenus.indexOf(basePath), 1)
        // 关闭 path 下的子菜单
        this.openedSubMenus = this.openedSubMenus.filter(item => item.indexOf(basePath+'/') !== 0)
      }
    }
  }
</script>

<style scoped>
  .menu {
    user-select: none;
    width: 200px;
    height: 100%;
    position: fixed;
    top: 0;
    left: 0;
    box-shadow: 5px 5px 5px rgba(204, 204, 204, 0.23);
    background-color: #545c64;
  }
</style>
