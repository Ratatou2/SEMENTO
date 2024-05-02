<script setup>
// 컴포넌트
import BlackDataCard from "@/components/card/BlackDataCard.vue";
import Line from "@/components/line/Line.vue";
import HeadText from "@/components/Text/HeadText.vue";
import Cardhead from "@/components/Text/Cardhead.vue";
// 차트
import StickChart from "./components/job-analysis/StickChart.vue";
import PieChart from "./components/job-analysis/PieChart.vue";
import DoughnutChart from "./components/job-analysis/DoughnutChart.vue";

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
  <div class="container col">
    <!-- 작업 분석 -->
    <!-- 작업 분석 - 시간대별 작업량 -->
    <div class="col container-item">
      <div class="row">
        <div class="search-period">
          Months
        </div>
        <div class="search-date">
          {{ months[month] }}, {{ year }}
        </div>
      </div>
      <Line />
    </div>
    <HeadText header-text="# Job Analysis" />
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
      <div class="white-box">
        <div class="title">
          <Cardhead header-text="시간대 별 작업량" content-text="한달동안 시간대 별 OHT의 작업량을 보여줍니다." />
        </div>
        <StickChart width="850px" height="310px" />
      </div>
    </div>
    <!-- 작업 분석 - 작업 성공 실패 -->
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
        <div>표 넣을 자리</div>
      </div>
    </div>
    <!-- 상태 분석 -->
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

.container {
  width: 100%;
  height: 100%;
  padding: 20px;
  overflow: hidden;
  gap: 20px
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

.bar-chart {
  padding: 20px 0;
  width: 100%;
  align-items: flex-start;
  overflow-x: auto;
  overflow-y: hidden;
}

.job-chart {
  width: 320px;
  height: 350px;
}

.job-table {
  width: 100%;
}
</style>
