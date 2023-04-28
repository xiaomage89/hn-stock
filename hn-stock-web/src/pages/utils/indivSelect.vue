<template>
  <el-form :inline="true" :model="formInline" class="demo-form-inline" style="float: left;height: 35px;">
    <el-form-item label="开始日期">
      <el-date-picker
        size="mini"
        v-model="formInline.startDate"
        type="date"
        value-format="yyyyMMdd"
        :default-value="new Date()"
        placeholder="选择日期">
      </el-date-picker>
    </el-form-item>
    <el-form-item label="结束日期">
      <el-date-picker
        size="mini"
        v-model="formInline.endDate"
        type="date"
        value-format="yyyyMMdd"
        :default-value="new Date()"
        placeholder="选择日期">
      </el-date-picker>
    </el-form-item>
    <el-form-item label="代码">
      <el-input size="mini" clearable v-model="formInline.scode" placeholder="股票代码"></el-input>
    </el-form-item>
    <el-form-item label="名称">
      <el-input size="mini" clearable v-model="formInline.sname" placeholder="股票名称"></el-input>
    </el-form-item>
    <el-form-item>
      <el-button size="small" type="primary" @click="onSubmit">查询</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
  import axios from "axios";

  export default {
    name: "indivSelect",
    props:['getSearch'],
    data() {
      return {
        data: {},
        errMsg: '',
        formInline: {
          startDate: '',
          endDate: '',
          scode: '',
          sname: ''
        }
      };
    },
    methods: {
      onSubmit() {
        axios.get("http://localhost:8086/mkIndivDay/list",
          {
            params: {
              scode: this.formInline.scode,
              sname: this.formInline.sname,
              sdate: this.formInline.startDate
            }
          }).then(
          (response) => {
            this.data = response.data.data
            this.getSearch(this.data)//向父组件传递值
          },
          (error) => {
            this.errMsg=error.message
          }
        )
      }
    }
  }
</script>

<style scoped>

</style>
