<template>
  <div class="hello">
    <h1>{{ msg }}</h1>
    <el-row :gutter="20" class="tittle">
      <el-col :span="16">
        <div class="text1">近12月保函数据</div>
      </el-col>
      <el-col :span="2">
        <button @click="getDataMonLine(1)" class="cursor-pointer">保函数量</button>
        <div :class="{active:isActive2===1}" @click="getDataMonLine(1)" class="cursor-pointer">保函数量</div>
      </el-col>
      <el-col :span="2">
        <div :class="{active:isActive2===2}" @click="getDataMonLine(2)" class="cursor-pointer">保费收入</div>
      </el-col>
      <el-col :span="2">
        <div :class="{active:isActive2===3}" @click="getDataMonLine(3)" class="cursor-pointer">担保金额</div>
      </el-col>
    </el-row>
    <div ref="echartDemo2" class="chart-box" style="height: 400px"></div>
  </div>
</template>

<script>
  // import echarts from 'echarts'
  export default {
    name: 'ChartLine',
    data() {
      return {
        isActive2: '1',
        msg: 'Welcome to Your Vue.js App'
      }
    },
    methods: {
      /**
       * 获取层叠折线图 filterType 1保函数量 2保费收入 3担保金额 不传返回所有
       */
      getDataMonLine(filterType) {
        console.log('===============================')
        if (filterType) {
          this.isActive2 = filterType
          // console.log('filterType高亮的是：',filterType,this.isActive2);
        }
        var monTop121 = [];
        var monTop122 = [];
        var monTop123 = [];
        var dataTop121 = [];
        var dataTop122 = [];
        var dataTop123 = [];
        // this.$http
        //   .post("/sys/data/guaranteeorgtwelvemonthstatistic", {
        //     dataType: 1,
        //     filterType:filterType,

        //   })
        //   .then((res) => {
        //     // console.log("折线图数据/投标保函按月统计表格数据是：", res.data);
        //     // this.twelvemonthDataLine = res.data;
        // 示例数据
        this.twelvemonthDataLine = [
          {
            detailList: [
              {countOrder: 1, guaranteeOrgName: "08担保"},
              {countOrder: 5, guaranteeOrgName: "01担保"},
            ],
            ym: "2021-06"
          },
          {
            detailList: [
              {countOrder: 20, guaranteeOrgName: "08担保"},
              {countOrder: 6, guaranteeOrgName: "01担保"},
            ],
            ym: "2021-07"
          },
          {
            detailList: [
              {countOrder: 6, guaranteeOrgName: "08担保"},
              {countOrder: 15, guaranteeOrgName: "01担保"},
            ],
            ym: "2021-08"
          },
          {
            detailList: [
              {countOrder: 0, guaranteeOrgName: "08担保"},
              {countOrder: 0, guaranteeOrgName: "01担保"},
            ],
            ym: "2021-09"
          },
          {
            detailList: [
              {countOrder: 0, guaranteeOrgName: "08担保"},
              {countOrder: 0, guaranteeOrgName: "01担保"},
            ], ym: "2021-10"
          },
          {
            detailList: [
              {countOrder: 0, guaranteeOrgName: "08担保"},
              {countOrder: 0, guaranteeOrgName: "01担保"},
            ], ym: "2021-11"
          },
          {
            detailList: [
              {countOrder: 0, guaranteeOrgName: "08担保"},
              {countOrder: 0, guaranteeOrgName: "01担保"},
            ], ym: "2021-12"
          },
          {
            detailList: [
              {countOrder: 12, guaranteeOrgName: "08担保"},
              {countOrder: 4, guaranteeOrgName: "01担保"},
            ], ym: "2022-01"
          },
          {
            detailList: [
              {countOrder: 0, guaranteeOrgName: "08担保"},
              {countOrder: 0, guaranteeOrgName: "01担保"},
            ], ym: "2022-02"
          },
          {
            detailList: [
              {countOrder: 0, guaranteeOrgName: "08担保"},
              {countOrder: 0, guaranteeOrgName: "01担保"},
            ], ym: "2022-03"
          },
          {
            detailList: [
              {countOrder: 0, guaranteeOrgName: "08担保"},
              {countOrder: 6, guaranteeOrgName: "01担保"},
            ], ym: "2022-04"
          },
          {
            detailList: [
              {countOrder: 3, guaranteeOrgName: "08担保"},
              {countOrder: 13, guaranteeOrgName: "01担保"},
            ], ym: "2022-05"
          }
        ]
        this.twelvemonthDataLine.forEach((value, index) => {
          if (filterType == 1) {
            monTop121.push(value.ym);
            dataTop121.push(value.detailList);

          } else if (filterType == 2) {
            monTop122.push(value.ym);
            dataTop122.push(value.sumGuaranteeFeeToWanYuan);
          } else if (filterType == 3) {
            monTop123.push(value.ym);
            dataTop123.push(value.sumBzjAmountToWanYuan);
          }
        });
        if (filterType == 1) {
          this.initChartLine(monTop121, dataTop121)
        } else if (filterType == 2) {
          this.initChartLine(monTop122, dataTop122);
        } else if (filterType == 3) {
          this.initChartLine(monTop123, dataTop123);
        }
        // });
      },
      /**
       * 层叠折线图
       */
      initChartLine(mon, data) {
        // console.log('monTop121:',mon);
        let series = [];
        let leggend = [];
        let yydata = [];
        for (let i = 0; i < data.length; i++) {
          let countOrders = [];
          // leggend.push(data[i].guaranteeOrgName);
          // countOrders=[0,1,5,2,4,5,3,1,4,9,14,2]
          var arr = data[i];
          console.log(arr)
          arr.forEach((item, index) => {
            countOrders.push(item.countOrder);
            yydata.push(item.countOrder);
            leggend.push(item.guaranteeOrgName);
          });
          // 将数据组装到series数组，供option配置使用
          console.log("series是0：", countOrders);
          series.push({
            // 每个担保机构，对应一条折线图
            // name: data[i].guaranteeOrgName,
            type: "line",
            data: countOrders // 每个担保机构，对应的横坐标的值组成的数组
          });
        }

        console.log("series是1：", mon);
        console.log("series是2：", leggend);
        console.log("series是3：", series);
        var option = {
          tooltip: {
            trigger: "axis"
          },
          title: {
            subtext: "保函数量(件)",
            left: "5%",
            top: "0%",
            textAlign: "center"
          },
          legend: {
            padding: 10,
            tooltip: {
              show: true
            },
            data: leggend
          },
          xAxis: {
            type: "category",
            boundaryGap: false,
            data: mon
          },
          yAxis: {
            type: "value"
          },
          series: {
            type:"line",
            data:yydata
          }
        };
        this.chartLine = this.$echarts.init(this.$refs.echartDemo2);
        this.chartLine.setOption(option);
      }
    }
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
  .active {
    color: red;
  }
</style>
