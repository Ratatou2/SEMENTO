<script setup>
// 컴포넌트
import BlackDataCard from "@/components/card/BlackDataCard.vue";
import Line from "@/components/line/Line.vue";
import HeadText from "@/components/Text/HeadText.vue";
import Cardhead from "@/components/Text/Cardhead.vue";
import Table from "@/components/table/Table.vue";
// 차트
import StickChart from "./components/job-analysis/StickChart.vue";
import PieChart from "./components/job-analysis/PieChart.vue";
import DoughnutChart from "./components/job-analysis/DoughnutChart.vue";
import LineChart from "./components/state-analysis/LineChart.vue";

// 날짜 계산
const months = [
  "January",   // 1월
  "February",  // 2월
  "March",     // 3월
  "April",     // 4월
  "May",       // 5월
  "June",      // 6월
  "July",      // 7월
  "August",    // 8월
  "September", // 9월
  "October",   // 10월
  "November",  // 11월
  "December"   // 12월
];
const currentDate = new Date();
const year = currentDate.getFullYear(); // 년도 가져오기
const month = currentDate.getMonth(); // 월 가져오기 (0부터 시작하므로 +1 해줘야 함)


</script>
<template>
  <div class="col container-header">
    <div class="row">
      <div class="search-period">
        Months
      </div>
      <div class="search-date">
        {{ months[month] }}, {{ year }}
      </div>
    </div>
  </div>
  <Line />
  <div class="container col gap-40">
    <!-- 작업 분석 -->
    <div class="col">
      <HeadText header-text="# Job Analysis" />
      <!-- 작업 분석 - 시간대별 작업량 -->
      <div class="container-item row gap-20">
        <div class="col">
          <BlackDataCard
            title="OHT Usage"
            content="30 대"
            :width="'320px'"
            :height="'130px'"
          />
          <BlackDataCard
            title="Total Work"
            content="10,986"
            percentage="+1.43%"
            :width="'320px'"
            :height="'130px'"
          />
          <BlackDataCard
            title="Average Daily Work per OHT"
            content="1,178"
            percentage="-4.43%"
            :width="'320px'"
            :height="'130px'"
          />
        </div>
        <div class="white-box job-time">
          <div class="title">
            <Cardhead header-text="시간대 별 작업량" content-text="한달동안 시간대 별 OHT의 작업량을 보여줍니다." />
          </div>
          <div class="padding-left-20">
            <StickChart width="100%" height="310px" />
          </div>
        </div>
      </div>
    </div>

    <!-- 작업 분석 - 작업 성공 실패 -->
    <div class="col">
      <div class="row gap-20">
        <div class="col">
          <div class="white-box job-chart">
            <div class="title">
              <Cardhead header-text="작업 성공률" content-text="전체 작업에 대해 데드라인 내에 도착한 OHT의 비율을 보여줍니다." />
            </div>
            <PieChart width="200px" height="200px" />
          </div>
          <div class="white-box job-chart">
            <div class="title">
              <Cardhead header-text="작업 실패 원인 별 비율" content-text="실패한 작업에 대해 실패 원인에 대한 비율을 보여줍니다." />
            </div>
            <DoughnutChart width="200px" height="200px" />
          </div>
        </div>
        <div class = "white-box job-table">
          <div class="title">
            <Cardhead header-text="실패한 작업 로그(198건)" content-text="데드라인까지 도착하지 못한 OHT에 대한 원인을 보여줍니다." />
          </div>
          <div class="padding-left-20">
            <Table
              width="100%"
              :columns="['No.', 'OHT ID', 'ERROR', 'COUNT']"
              :data="[
                ['1', '1923', '300', '23회'],
                ['2', '1932', '300', '2회'],
                ['3', '1932', '300', '2회'],
                ['4', '1932', '300', '2회'],
                ['5', '1932', '300', '2회'],
                ['6', '1932', '300', '2회'],
                ['7', '1932', '300', '2회'],
                ['1', '1923', '300', '23회'],
                ['2', '1932', '300', '2회'],
                ['3', '1932', '300', '2회'],
                ['4', '1932', '300', '2회'],
                ['5', '1932', '300', '2회'],
                ['6', '1932', '300', '2회'],
                ['7', '1932', '300', '2회'],
                ['1', '1923', '300', '23회'],
                ['2', '1932', '300', '2회'],
                ['3', '1932', '300', '2회'],
                ['4', '1932', '300', '2회'],
                ['5', '1932', '300', '2회'],
                ['6', '1932', '300', '2회'],
                ['7', '1932', '300', '2회'],
                ['1', '1923', '300', '23회'],
                ['2', '1932', '300', '2회'],
                ['3', '1932', '300', '2회'],
                ['4', '1932', '300', '2회'],
                ['5', '1932', '300', '2회'],
                ['6', '1932', '300', '2회'],
                ['7', '1932', '300', '2회'],
              ]"
            />
          </div>
        </div>
      </div>
    </div>

    <!-- 상태 분석 -->
    <div class="col">
      <HeadText header-text="# State Analysis" />
      <div class="container-item row gap-20">
        <div class="col">
          <BlackDataCard
            title="OHT Deadline"
            content="30m 20s"
            :width="'320px'"
            :height="'130px'"
          />
          <BlackDataCard
            title="Average Working Hours"
            content="4m 35s"
            percentage="+1.43%"
            :width="'320px'"
            :height="'130px'"
          />
          <BlackDataCard
            title="Average Idle Hours"
            content="15m 24s"
            percentage="-4.43%"
            :width="'320px'"
            :height="'130px'"
          />
        </div>
        <div class="col" style="width: 100%;">
          <div class="white-box line-chart">
            <div class="title">
              <Cardhead header-text="시간대 별 작업/유휴 상태 OHT 수" content-text="시간대 별로 작업 중인 OHT와 유휴 상태의 OHT 수를 보여줍니다." />
            </div>
            <div class="padding-left-20">
              <LineChart height="200px"/>
            </div>
          </div>
          <div class="row">
          </div>
        </div>
      </div>
    </div>
  </div>
  
</template>
<style scoped>
.row {
  display: flex;
  flex-direction: row;
  gap: 10px;
}

.col {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.gap-20 {
  gap: 20px;
}
.gap-30 {
  gap: 30px;
}
.gap-40 {
  gap: 40px;
}

.padding-left-20 {
  width: 100%;
  padding-left: 30px;
}

.container-header {
 padding-left: 30px; 
}
.container {
  width: 100%;
  height: 100%;
  padding: 10px 30px;
  overflow: hidden;
}

.search-period {
  width: 90px;
  height: 30px;
  line-height: 30px;
  background-color: white;
  color: black;
  text-align: center;
  font-size: small;
  font-weight: 700;
  border-radius: 5px;
}
.search-date {
  height: 30px;
  line-height: 30px;
  color: #9F9F9F;
  font-size: small;
  font-weight: 400;
  margin-left: 10px;
}
.job-time{
  width: 100%;  
}

.bar-chart {
  padding: 20px 0;
  width: 100%;
  overflow-x: auto;
  overflow-y: hidden;
}

.job-chart {
  width: 320px;
  height: 350px;
}

.job-table {
  width: 100%;
  height: 710px;
  overflow-y: auto;
}

.line-chart {
  width: 100%;
  padding: 20px 0;
}
</style>
