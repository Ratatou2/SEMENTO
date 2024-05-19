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
import InitialPage from "@/components/loading/InitialPage.vue";
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

const nowLoading = ref(false); //로딩창 기본 비활성화
const cnt = ref(11);
const congTime = ref(0);

const startTime = ref(); // 검사 시작 시간
const endTime = ref(); // 검사 끝 시간

watch(
  () => analysisStore.computedStartDate,
  (newValue, oldValue) => {
    startTime.value = newValue;
  },
  { immediate: true }
);

watch(
  () => analysisStore.computedEndDate,
  (newValue, oldValue) => {
    endTime.value = newValue;
  },
  { immediate: true }
);

const detectionReportText = computed(() => {
  return "# Detection Report (" + cnt.value + ")";
});

watch(
  () => analysisStore.computedDetectionResult,
  (newValue, oldValue) => {
    cnt.value = newValue.length;
  },
  { immediate: true }
);

const formattedStartDate = computed(() => {
  console.log("startTime: ", startTime.value);
  return moment(startTime.value).format("YYYY.MM.DD HH:mm:ss");
});

const formattedEndDate = computed(() => {
  console.log("endTime: ", endTime.value);
  return moment(endTime.value).format("YYYY.MM.DD HH:mm:ss");
});

const summaryText = computed(() => {
  return `${formattedStartDate.value} ~ ${formattedEndDate.value} 기간동안 총 ${cnt.value}개의 정체가 감지되었습니다.`;
});

const congestionText = computed(() => {
  const hours = Math.floor(congTime.value / 3600);
  const minutes = Math.floor(congTime.value / 60);
  const seconds = congTime.value % 60;

  let text = "총";
  if (hours > 0) text += `${hours}시간 `;
  if (minutes > 0 || hours > 0) text += `${minutes}분 `;
  text += `${seconds}초의 정체시간이 소요되었습니다.`;
  return text;
});

watch(
  () => analysisStore.computedDetectionResult,
  (newValue, oldValue) => {
    congTime.value = newValue.reduce((total, result) => {
      return (
        total +
        (new Date(result["end-date"]) - new Date(result["start-date"])) / 1000
      );
    }, 0);
  },
  { immediate: true }
);

const detectionReports = computed(() => {
  return analysisStore.computedDetectionResult.map((result, index) => {
    const startDate = new Date(result["start-date"]);
    const endDate = new Date(result["end-date"]);
    const duration = (endDate - startDate) / 1000;

    const hours = Math.floor(duration / 3600);
    const minutes = Math.floor((duration % 3600) / 60);
    const seconds = duration % 60;

    let durationText = "";
    if (hours > 0) durationText += `${hours}시간 `;
    if (minutes > 0 || hours > 0) durationText += `${minutes}분 `;
    durationText += `${seconds}초`;

    let cause;
    const errorType = result["cause"];
    if (errorType === "F") {
      cause = "설비 에러";
    } else if (errorType === "O") {
      cause = "Oht 에러";
    } else if (errorType === "E") {
      cause = "기타 에러";
    }

    return {
      index: index + 1,
      startDate: moment(startDate).format("YYYY.MM.DD HH:mm:ss"),
      endDate: moment(endDate).format("YYYY.MM.DD HH:mm:ss"),
      durationText: durationText,
      cause: cause,
    };
  });
});

const handleStartDate = (newDate) => {
  console.log("handleStartDate");
  startTime.value = newDate;
};
const handleEndDate = (newDate) => {
  console.log("handleEndDate");
  endTime.value = newDate;
};
//AI 버튼 핸들링 이벤트
const handleAIDetectionButton = async () => {
  nowLoading.value = true;
  await analysisStore.getNewAIDetection(startTime, endTime);
  //nowLoading.value = false;
};

onMounted(async () => {
  await analysisStore.getAiDetection();
  cnt.value = analysisStore.computedDetectionResult.length;
  analysisStore.computedDetectionResult.forEach((result) => {
    congTime.value +=
      (new Date(result["end-date"]) - new Date(result["start-date"])) / 1000;
  });
  //nowLoading.value = false;
});
//==초기 화면
const initialPage = ref(true);
</script>

<template>
  <div v-if="nowLoading"><Loading title="AI가 로그를 분석중입니다." /></div>
  <div v-else="!nowLoading" class="body-container">
    <!-- 설명 및 검색창 -->
    <section class="input">
      <Text
        text="기간을 설정하여 혼잡/정체 상황에 대한 AI 분석을 받아보세요."
      />
      <div class="input-data">
        <SearchInput
          :props-start-date="startTime"
          :props-end-date="endTime"
          @update-start="handleStartDate"
          @update-end="handleEndDate"
        />
        <Button
          title="AI 분석"
          backgroundColor="#003CB0"
          fontColor="white"
          width="100px"
          @click="handleAIDetectionButton"
        />
      </div>
    </section>
    <Line></Line>
    <!-- 아직 검색 안했을때 -->
    <div v-if="initialPage"><InitialPage /></div>
    <!-- 검색결과 -->
    <section v-else="initialPage" class="header">
      <!--제목 -->
      <HeadText header-text="# Summary" />
      <Text :text="summaryText" />
    </section>

    <section v-else="initialPage" class="result">
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
    <section v-else="initialPage" class="chart">
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
    <Line v-else="initialPage" />
    <section v-else="initialPage" class="header">
      <!--제목 -->
      <HeadText :header-text="detectionReportText" />
    </section>
    <section
      v-for="report in detectionReports"
      :key="report.index"
      class="detection-report-container"
    >
      <SementoAiResultCard
        :location="true"
        :number="report.index"
        :text="`${report.startDate} ~ ${report.endDate} [총 ${report.durationText}]`"
        :cause="report.cause"
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
