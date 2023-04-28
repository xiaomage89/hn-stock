`
<template>
  <div>
    <div ref="echartShare" class="chart-box" style="height:500px"></div>
  </div>
</template>

<script>
  import axios from "axios";

  export default {
    name: "shareChart",
    data() {
      return {
        search: {
          sdate: '20230412',
          scode: '000001',
          sname: '',
          sday: '1'
        },
        errMsg: "",
        data: [],
        lmax: '0',
        lmin: '100000'
      }
    },
    mounted() {
      this.drawLine();	//执行下面的函数
    },
    methods: {
      drawLine() {
        // 第三方接口，需要实时刷新用的定时器，并未做websocket的处理，有需要可以自己加
        // this.timer = setInterval(() => {
        // vue项目中为了规范，跨域请求封装了jsonp的方法
        axios.get("http://localhost:8086/indiv/mkShareChart/list",
          {
            params: {
              scode: this.search.scode,
              sdate: this.search.sdate,
              sday: this.search.sday
            }
          }
        ).then(
          (response) => {
            this.data = response.data.data
            this.initChartLine();
          },
          (error) => {
            this.errMsg = error.message
          }
        )
      },

      initChartLine() {
        let xdata = [];
        let ydata = [];
        this.data.forEach(item => {
          if (item.stime > '2023-04-12 13:55') {
            xdata.push(item.stime.substring(11, 16));
            ydata.push(item.price);
            if (this.lmax < item.price) this.lmax = item.price;
            if (this.lmin > item.price) this.lmin = item.price;
          }
        });
        this.lmax = this.lmax + (this.lmax - this.lmin);
        this.lmin = this.lmin - (this.lmax - this.lmin);

        var option = {
          xAxis: {
            type: 'category',
            axisTick: {
              alignWithLabel: true,
              length: 6
            },
            data: xdata
          },
          yAxis: {
            type: 'value',
            name: '成交价',
            min: this.lmin,
            max: this.lmax,
            position: 'left'
          },
          series: [
            {
              type: 'line',
              yAxisIndex: 0,
              data: ydata
            }
          ]
        };
        // 进行初始化
        this.chartLine = this.$echarts.init(this.$refs.echartShare);
        this.chartLine.setOption(option);
      },
    }
  }
</script>

<style scoped>


</style>
`
