<script setup>
import Cardhead from "@/components/Text/Cardhead.vue";
import Text from "@/components/Text/Text.vue";
import HeadText from "@/components/Text/HeadText.vue";
import SearchInput from "@/components/searchBar/SearchInput.vue";
import Button from "@/components/button/Button.vue";
import Line from "@/components/line/Line.vue";
import "vue-multiselect/dist/vue-multiselect.css";
import Map from "./components/summary/Map.vue";
import Info from "@/components/info/Info.vue";
// chart
import DurationChart from "./components/summary/DurationChart.vue";
import ErrorChart from "./components/summary/ErrorChart.vue";
import CongestionChart from "./components/summary/CongestionChart.vue";
import SementoAiResultCard from "./components/detection-report/SementoAiResultCard.vue";

// vue
import { useAnalysisStore } from "@/stores/analysis";
import { onMounted, ref, computed, watch } from "vue";
import moment from "moment";

import Loading from "@/components/loading/Loading.vue";

const analysisStore = useAnalysisStore();

const nowLoading = ref(true); //로딩창 기본 비활성화
const cnt = ref(11);
const congTime = ref(0);
const detectionReportText = "# Detection Report (" + cnt.value + ")";

const formattedStartDate = computed(() => {
  return moment(analysisStore.startDate.value).format("YYYY.MM.DD HH:mm:ss");
});

const formattedEndDate = computed(() => {
  return moment(analysisStore.endDate.value).format("YYYY.MM.DD HH:mm:ss");
});

const summaryText = computed(() => {
  return `${formattedStartDate.value} ~ ${formattedEndDate.value} 기간동안 총 ${cnt.value}개의 정체가 감지되었습니다.`;
});

const congestionText = computed(() => {
  return `총 ${congTime.value}초의 정체시간이 소요되었습니다.`;
});

onMounted(async () => {
  await analysisStore.getAiDetection();
  cnt.value = analysisStore.computedDetectionResult.length;
  analysisStore.computedDetectionResult.forEach((result) => {
    congTime.value +=
      (new Date(result["end-date"]) - new Date(result["start-date"])) / 1000;
  });
  nowLoading.value = false;
});
</script>

<template>
  <div v-if="nowLoading"><Loading /></div>
  <div v-else="!nowLoading" class="body-container">
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
      <Text :text="summaryText" />
    </section>

    <section class="result">
      <!-- 시뮬레이션 -->
      <div class="white-box simulation-box">
        <div class="info-box">
          <Info />
        </div>
        <div class="content">
          <Map />
        </div>
      </div>
    </section>
    <!-- 그래프 분석 -->
    <section class="chart">
      <div class="white-box error-chart-box">
        <section class="title">
          <Cardhead
            headerText="Error Chart"
            contentText="탐지된 정체 상황들의 AI 분석 결과 비율입니다."
          ></Cardhead>
        </section>
        <div class="error-chart">
          <div class="error-chart-content">
            <ErrorChart />
          </div>
        </div>
      </div>
      <div class="right-chart-box">
        <div class="white-box">
          <section class="title">
            <Cardhead
              headerText="Congestion Time"
              contentText="기간동안 소요된 총 정체시간 입니다."
            ></Cardhead>
          </section>
          <div class="congestion-chart">
            <div class="congestion-chart-content">
              <CongestionChart />
            </div>
          </div>
          <div class="congestion-chart-text">
            <Text :text="congestionText" size="19px" />
          </div>
        </div>
        <div class="white-box duration-chart-box">
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
  <div class="footer"></div>
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
  padding: 0 23px;
}
.result {
  display: flex;
  flex-direction: column;
  gap: 25px;
  padding: 0 20px;
}

.chart {
  display: flex;
  flex-direction: row;
  gap: 20px;
  padding: 0 23px;
  height: 500px;
}

.error-chart-box {
}

.duration-chart-box {
  height: 100%;
}

.right-chart-box {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.duration-chart {
  width: 100%;
  overflow: auto;
  overflow-y: hidden;
  padding-left: 20px;
}

.duration-chart-content {
  width: 800px;
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

.congestion-chart {
  width: 100%;
  padding: 0 20px;
}

.congestion-chart-content {
  height: 50px;
}

.congestion-chart-text {
  width: 100%;
  padding: 0 20px;
}

.white-box {
  min-width: 400px;
}

.simulation-box {
  min-height: 400px;
  width: 100%;
}

.title {
  width: 100%;
  display: flex;
  flex-direction: start;
}

.content {
  width: 96%;
}

.info-box {
  width: 100%;
  display: flex;
  flex-direction: row;
  justify-content: flex-end;
  padding: 0 20px;
}
</style>
