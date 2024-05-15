<script setup>
import Simulation from "@/components/simulation/Simulation.vue";
import Cardhead from "@/components/Text/Cardhead.vue";
import Text from "@/components/Text/Text.vue";
import SearchInput from "@/components/searchBar/SearchInput.vue";
import Button from "@/components/button/Button.vue";
import Line from "@/components/line/Line.vue";
import { ref, onMounted } from "vue";
import Multiselect from "vue-multiselect";
import "vue-multiselect/dist/vue-multiselect.css";
import StickChart from "./components/StickChart.vue";
import BlackDataCard from "@/components/card/BlackDataCard.vue";
import SimulationSideTapView from "./SimulationSideTapView.vue";
import Table from "@/components/table/Table.vue";
import Loading from "@/components/loading/Loading.vue";
import { simulationStore } from "@/stores/simulation";
import moment from "moment";
const {
  startDate,
  endDate,
  getComparedData,
  getChartData,
  getSimulation,
  getClassificationLog,
} = simulationStore();

const nowLoading = ref(true); //로딩창 기본 비활성화
const value = ref();
const options = [
  { name: "1호기" },
  { name: "2호기" },
  { name: "3호기" },
  { name: "4호기" },
  { name: "5호기" },
  { name: "6호기" },
  { name: "7호기" },
];
const selectOhtId = ref(2600);
const isSidePageOpen = ref(false);

// 날짜 변환 함수
function transformatDate(date) {
  return [
    moment(date).format("YYYY-MM-DD") + " " + moment(date).format("HH:mm:ss"),
  ];
}

//==시뮬레이션 관련 데이터
const simulationData = ref([null]);
//== 비교 관련 데이터
const comparedData = ref(null);
function formattedComparedDate(data) {
  const formattedData = {};
  for (const key in data) {
    if (data.hasOwnProperty(key)) {
      formattedData[key] = {
        data: formatNumber(data[key].data),
        percent: formatNumber(data[key].percent),
      };
    }
  }
  return formattedData;
}
const formatNumber = (value) => {
  const formattedValue = parseFloat(value).toFixed(2);
  return formattedValue.endsWith(".00")
    ? parseInt(formattedValue)
    : formattedValue;
};
//==차트 관련 데이터
const timeArray = ref([null]);
const meArray = ref([null]);
const averageArray = ref([null]);
function setChart(chartData) {
  timeArray.value = chartData["work-per-time"].map((item) => item.time);
  meArray.value = chartData["work-per-time"].map((item) => item.me);
  averageArray.value = chartData["work-per-time"].map((item) => item.average);
}

//== 작업별 분류 관련 데이터
const logPerWork = ref(null);
const totalCnt = ref(null);
function setclassificationLogData(data) {
  totalCnt.value = data["total-cnt"];
  logPerWork.value = formatLogPerWork(data["log-per-work"]);

  console.log(totalCnt.value);
  console.log(logPerWork.value);
}
const formatLogPerWork = (logs) => {
  return logs.map((log, index) => [
    index + 1,
    `${transformatDate(log["start-time"])} - ${transformatDate(
      log["end-time"]
    )}`,
    `${
      new Date(log["end-time"]).getTime() -
      new Date(log["start-time"]).getTime()
    } ms`,
    log.errors.join(", "),
    `${log["average-speed"].toFixed(2)} m/s`,
    log["out-of-deadline"].toString().toUpperCase(),
  ]);
};

//== axios 통신 ==
onMounted(async () => {
  //== 시뮬레이션 데이터 로드 : 시간단위로 잘라서 연속적으로 요청해야함
  //simulationData.value = await getSimulation();

  //== 기타데이터 로드
  comparedData.value = formattedComparedDate(await getComparedData());

  //== 작업량 평균 비교 로드
  setChart(await getChartData());

  //== 작업별로 분류된 로그 로드
  setclassificationLogData(await getClassificationLog());

  nowLoading.value = false;
});

//== 이벤트 핸들러 ==
function toggleSidePage(date) {
  isSidePageOpen.value = !isSidePageOpen.value;
}

function toggleSidePageHandler(data) {
  //emit 받는 핸들러
  toggleSidePage(data);
}
</script>

<template>
  <div v-if="nowLoading"><Loading /></div>
  <div v-else="!nowLoading" class="body-container">
    <!-- 설명 및 검색창 -->
    <section class="input">
      <Text
        text="개별이송체의 시스템로그를 통해 시뮬레이션과 분석결과를 받아보세요."
      />
      <div class="input-data">
        <SearchInput />
        <div>
          <multiselect
            id="oht-selector"
            v-model="value"
            :options="options"
            placeholder="OHT_ID"
            label="name"
            style="width: 200px"
            selectLabel="선택하기"
          ></multiselect>
        </div>
        <Button title="Simulation" backgroundColor="black" fontColor="white" />
      </div>
    </section>
    <Line></Line>
    <!-- 검색결과 -->
    <section class="result">
      <!-- 시뮬레이션 -->
      <div class="white-box simulation-box">
        <section class="title">
          <Cardhead
            headerText="Simulation"
            contentText="2024.01.07 12:00:00 - 2024.01.08 15:10:00 기간 1817호기의 시뮬레이션 입니다."
          ></Cardhead>
        </section>
        <section class="content">
          <Simulation />
        </section>
      </div>
      <!-- 가로정렬 : 작업량평균, 블랙데이터카드 -->
      <section class="garo">
        <!-- 작업량 평균비교 막대그래프 -->
        <div class="white-box barchart-box">
          <section class="title">
            <Cardhead
              headerText="작업량 평균 비교"
              contentText="동시간대 전체 OHT작업량과 시간별 평균을 비교합니다"
            ></Cardhead>
          </section>
          <div class="bar-chart">
            <div class="bar-chart-content">
              <StickChart
                :labels="timeArray"
                :work-per-all="averageArray"
                :work-per-one="meArray"
                :oht-id="selectOhtId"
              />
            </div>
          </div>
        </div>
        <!-- 블랙데이터카드 -->
        <div class="black-card-box">
          <div class="black-card-content">
            <BlackDataCard
              title="Total Work"
              :content="comparedData['total-work'].data"
              :percentage="comparedData['total-work'].percent + '%'"
              :fontColor="
                comparedData['total-work'].percent >= 0 ? 'red' : 'blue'
              "
              :height="'130px'"
              width="250px"
            />
            <BlackDataCard
              title="Out Of DeadLine"
              :content="comparedData['out-of-deadline'].data"
              :percentage="comparedData['out-of-deadline'].percent + '%'"
              :fontColor="
                comparedData['out-of-deadline'].percent >= 0 ? 'red' : 'blue'
              "
              :height="'130px'"
              width="250px"
            />
          </div>
          <div class="black-card-content">
            <BlackDataCard
              title="Average Speed"
              :content="comparedData['average-speed'].data"
              :percentage="comparedData['average-speed'].percent + '%'"
              :fontColor="
                comparedData['average-speed'].percent >= 0 ? 'red' : 'blue'
              "
              :height="'130px'"
              width="250px"
            />
            <BlackDataCard
              title="OHT ERROR"
              :content="comparedData['oht-error'].data"
              :percentage="comparedData['oht-error'].percent + '%'"
              :fontColor="
                comparedData['oht-error'].percent >= 0 ? 'red' : 'blue'
              "
              :height="'130px'"
              width="250px"
            />
          </div>
        </div>
      </section>
      <!-- Log By Total-work -->
      <div class="white-box log-table-box">
        <section class="title">
          <Cardhead
            :headerText="'Log By Total Work(' + totalCnt + '건)'"
            contentText="각 작업을 클릭하여 해당하는 로그를 시뮬레이션과 함께 확인하실 수 있습니다."
          />
        </section>
        <section class="table-container">
          <Table
            width="100%"
            bodyFontSize="14px"
            headerFontSize="12px"
            @toggle-side-page="toggleSidePageHandler"
            :columns="[
              'No.',
              'Period',
              'Time Taken',
              'ERROR',
              'Average Speed',
              'Out of DeadLine',
            ]"
            :data="logPerWork"
          />
        </section>
      </div>
    </section>
  </div>
  <div class="side-page" :class="{ open: isSidePageOpen }">
    <!-- 사이드 페이지 내용을 여기에 추가하세요 -->
    <section class="back-icon">
      <font-awesome-icon
        @click="toggleSidePage"
        :icon="['fas', 'angles-right']"
        size="2xl"
        style="color: #383839; margin-left: 15px; margin-top: 20px"
      />
    </section>
    <SimulationSideTapView />
  </div>
  <div class="footer"></div>
</template>

<style scoped>
.side-page {
  width: 48%;
  height: 100%;
  background-color: #f3f2f7;
  position: fixed;
  top: 0;
  right: -48%; /* 초기 위치는 오른쪽 바깥에 있습니다 */
  transition: right 0.5s;
  box-shadow: -2px 0 8px rgba(0, 0, 0, 0.09);
  overflow: scroll;
}

.side-page.open {
  right: 0; /* 열릴 때 위치 */
}

.garo {
  width: 97%;
  display: flex;
  justify-content: space-between;
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
  margin-bottom: 50px;
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
.result {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 25px;
}
#oht-selector {
  width: 200px;
  background-color: white;
  border-radius: 5px;
  border: 1px solid rgb(179, 179, 179);
  padding: 5px 13px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0px 5px 20px rgba(0, 0, 0, 0.05);
  font-size: 15px;
}

.bar-chart {
  width: 90%;
  height: 220px;
  overflow: scroll;
  overflow-y: hidden;
}

.bar-chart-content {
  width: 1200px;
  height: 100%;
}

.black-card-box {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  /* align-items: end; */
}
.simulation-box {
  min-height: 400px;
  width: 97%;
}

.log-table-box {
  min-height: 200px;
  width: 97%;
  display: flex;
  align-items: center;
}
.table-container {
  width: 97%;
}

.barchart-box {
  width: 54%;
}

.content {
  width: 96%;
}
</style>
