<template>
  <div height="689px" style="width: 100%">
    <indivSelect :getSearch="getSearch"/>
    <el-table
      :data="list"
      :header-cell-style="{background:'#f1e5e5',color:'#606266'}"
      height="622px"
    >
      <el-table-column
        fixed
        prop="sdate"
        label="日期"
        width="120">
      </el-table-column>
      <el-table-column
        prop="scode"
        label="代码"
        width="120">
      </el-table-column>
      <el-table-column
        prop="sname"
        label="名字"
        width="120">
      </el-table-column>
      <el-table-column
        prop="close"
        label="现价"
        width="120">
      </el-table-column>
      <el-table-column
        prop="differrange"
        label="涨跌幅"
        width="120">
      </el-table-column>
      <el-table-column
        prop="differ"
        label="涨跌额"
        width="120">
      </el-table-column>
      <el-table-column
        prop="volume"
        label="成交量(手)"
        width="120">
      </el-table-column>
      <el-table-column
        prop="amount"
        label="成交额"
        width="120">
      </el-table-column>
      <el-table-column
        prop="amplitude"
        label="振幅"
        width="120">
      </el-table-column>
      <el-table-column
        prop="high"
        label="最高"
        width="120">
      </el-table-column>
      <el-table-column
        prop="low"
        label="最低"
        width="120">
      </el-table-column>
      <el-table-column
        prop="open"
        label="今开"
        width="120">
      </el-table-column>
      <el-table-column
        prop="preclose"
        label="昨收"
        width="120">
      </el-table-column>
      <el-table-column
        prop="turn"
        label="换手率"
        width="120">
      </el-table-column>
      <el-table-column
        prop="pe"
        label="市盈率(动态)"
        width="120">
      </el-table-column>
      <el-table-column
        prop="pb"
        label="市净率"
        width="120">
      </el-table-column>
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-button @click="handleClick(scope.row)" type="text" size="small">查看</el-button>
          <el-button type="text" size="small">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="pageNum"
      :page-sizes="[10, 30, 60, 100]"
      :page-size="pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total">
    </el-pagination>
  </div>

</template>

<script>
  import axios from "axios"
  import indivSelect from "../../../utils/indivSelect"

  export default {
    data() {
      return {
        data:{},
        list: [],
        pageNum: 1,
        pageSize: 5,
        total: 50,
        totalPage: 10,
        errMsg: ""
      }
    },
    components: {
      indivSelect
    },
    methods: {
      getSearch(data){
        this.data=data
        this.showlist()
      },
      showlist() {
        this.total = this.data.total
        this.list = this.data.list.slice((this.pageNum - 1) * this.pageSize, this.pageNum * this.pageSize)
      },
      // 当每页数量改变
      handleSizeChange(val) {
        console.log(`每页 ${val} 条`);
        this.pageSize = val
        this.showlist()
      },
      // 当当前页改变
      handleCurrentChange(val) {
        console.log(`当前页: ${val}`);
        this.pageNum = val
        this.showlist()
      }
    }
  }
</script>

<style scoped>

</style>
