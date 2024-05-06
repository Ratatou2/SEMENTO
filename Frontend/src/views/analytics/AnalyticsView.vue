<script setup>
import Cardhead from "@/components/Text/Cardhead.vue";
import Text from "@/components/Text/Text.vue";
import HeadText from "@/components/Text/HeadText.vue";
import SearchInput from "@/components/searchBar/SearchInput.vue";
import Button from "@/components/button/Button.vue";
import Line from "@/components/line/Line.vue";
import "vue-multiselect/dist/vue-multiselect.css";
// chart
import DurationChart from "./components/summary/DurationChart.vue";
import ErrorChart from "./components/summary/ErrorChart.vue";
import SementoAiResultCard from "./components/detection-report/SementoAiResultCard.vue";

import { ref } from "vue";

const cnt = ref(11);
const detectionReportText = "# Detection Report (" + cnt.value + ")";
</script>

<template>
  <div class="body-container">
    <!-- 설명 및 검색창 -->
    <section class="input">
      <Text
        text="기간을 설정하여 혼잡/정체 상황에 대한 AI 분석을 받아보세요."
      />
      <div class="input-data">
        <SearchInput />
        <Button
          title="AI 분석"
          backgroundColor="#003CB0"
          fontColor="white"
          width="100px"
        />
      </div>
    </section>
    <Line></Line>
    <!-- 검색결과 -->
    <section class="header">
      <!--제목 -->
      <HeadText header-text="# Summary" />
      <Text
        text="2024.01.07 12:00:00 ~ 2024.01.08 15:10:00 기간동안 총 11개의 정체가 감지되었습니다."
      />
    </section>

    <section class="result">
      <!-- 정체 발생 빈도 시각화 맵 -->
      <div class="white-box simulation-box"></div>
    </section>
    <!-- 그래프 분석 -->
    <section class="chart">
      <div class="white-box barchart-box">
        <section class="title">
          <Cardhead
            headerText="Error Chart"
            contentText="탐지된 정체 상황들의 AI 분석 결과 비율입니다."
          ></Cardhead>
        </section>
        <div class="error-chart">
          <div class="error-chart-content"><ErrorChart /></div>
        </div>
      </div>
      <div class="white-box barchart-box">
        <section class="title">
          <Cardhead
            headerText="Duration Time Chart"
            contentText="각 정체 상황별 소요시간 그래프입니다."
          ></Cardhead>
        </section>
        <div class="duration-chart">
          <div class="duration-chart-content"><DurationChart /></div>
        </div>
      </div>
    </section>
    <!-- 각 정체별 상세분석 -->
    <Line />
    <section class="header">
      <!--제목 -->
      <HeadText :header-text="detectionReportText" />
    </section>
    <section v-for="index in 3" :key="index" class="detection-report-container">
      <SementoAiResultCard
        :location="true"
        :number="index"
        text="2024.01.07 12:03:21 ~ 2024.01.07 12:05:59 [총 2분 38초]"
      />
    </section>
  </div>
</template>

<style scoped>
.detection-report-container {
  /* border: 1px solid black; */
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.black-card-content {
  width: 100%;
  display: flex;
  gap: 20px;
}
.body-container {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.input {
  display: flex;
  flex-direction: column;
  gap: 5px;
  padding-left: 23px;
}
.input-data {
  display: flex;
  gap: 18px;
  align-items: center;
  height: 50px;
}
.header {
  display: flex;
  flex-direction: column;
  padding-left: 23px;
}
.result {
  display: flex;
  flex-direction: column;
  gap: 25px;
  padding-left: 23px;
}

.chart {
  display: flex;
  flex-direction: row;
  gap: 10px;
  padding-left: 23px;
}

.duration-chart {
  width: 100%;
  overflow: auto;
  overflow-y: hidden;
  padding-left: 20px;
}

.duration-chart-content {
  width: 800px;
  height: 100%;
}

.error-chart {
  width: 100%;
  overflow: auto;
  overflow-y: hidden;
  padding-left: 20px;
}

.error-chart-content {
  height: 100%;
}

.simulation-box {
  min-height: 400px;
  width: 97%;
}

.title {
  width: 100%;
  display: flex;
  flex-direction: start;
}

.content {
  width: 96%;
}
</style>
