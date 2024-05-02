<script setup>
import BlackDataCard from "@/components/card/BlackDataCard.vue";
import Line from "@/components/line/Line.vue";
import StickChart from "./components/job-analysis/StickChart.vue";
import HeadText from "@/components/Text/HeadText.vue";
import Cardhead from "@/components/Text/Cardhead.vue";

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
        <StickChart width="850px" height="320px" />
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
  height: 410px;
  align-items: flex-start;
  overflow-x: auto;
  overflow-y: hidden;
}
</style>
