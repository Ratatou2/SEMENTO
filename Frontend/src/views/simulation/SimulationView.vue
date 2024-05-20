<script setup>
import Simulation from "@/components/simulation/Simulation.vue";
import Cardhead from "@/components/Text/Cardhead.vue";
import Text2 from "@/components/Text/Text2.vue";
import SearchInput from "@/components/searchBar/SearchInput.vue";
import Button from "@/components/button/Button.vue";
import Line from "@/components/line/Line.vue";
import { ref, onMounted, watch, computed } from "vue";
import Multiselect from "vue-multiselect";
import "vue-multiselect/dist/vue-multiselect.css";
import StickChart from "./components/StickChart.vue";
import BlackDataCard from "@/components/card/BlackDataCard.vue";
import SimulationSideTapView from "./SimulationSideTapView.vue";
import SideTabTable from "@/components/table/SideTabTable.vue";
import Loading from "@/components/loading/Loading.vue";
import { simulationStore } from "@/stores/simulation";
import moment from "moment";
import InitialPage from "@/components/loading/InitialPage.vue";

const usesimulationStore = simulationStore();
const { getNewResult, isDataLoaded } = simulationStore();

const nowLoading = ref(false); //로딩창 기본 비활성화
//==초기 화면
// const initialPage = ref(true);
const isSidePageOpen = ref(false); //사이드탭 기본 비활성화

function transformatDate(date) {
  return (
    moment(date).format("YYYY-MM-DD") + " " + moment(date).format("HH:mm:ss")
  );
}

//== 인풋창 설정 => 디폴트 셋팅
const options = [
  { name: "5205호기", value: 5205 },
  { name: "5206호기", value: 5206 },
  { name: "5207호기", value: 5207 },
  { name: "5208호기", value: 5208 },
  { name: "5209호기", value: 5209 },
  { name: "5210호기", value: 5210 },
  { name: "5211호기", value: 5211 },
  { name: "5212호기", value: 5212 },
  { name: "5213호기", value: 5213 },
  { name: "5214호기", value: 5214 },
  { name: "5215호기", value: 5215 },
  { name: "5216호기", value: 5216 },
  { name: "5217호기", value: 5217 },
  { name: "5218호기", value: 5218 },
  { name: "5219호기", value: 5219 },
  { name: "5220호기", value: 5220 },
  { name: "5221호기", value: 5221 },
  { name: "5222호기", value: 5222 },
  { name: "5223호기", value: 5223 },
  { name: "5224호기", value: 5224 },
  { name: "5225호기", value: 5225 },
  { name: "5226호기", value: 5226 },
  { name: "5227호기", value: 5227 },
  { name: "5228호기", value: 5228 },
  { name: "5229호기", value: 5229 },
  { name: "5230호기", value: 5230 },
  { name: "5231호기", value: 5231 },
  { name: "5232호기", value: 5232 },
  { name: "5233호기", value: 5233 },
  { name: "5234호기", value: 5234 },
];
const selectedOhtId = ref();
const newStartDate = ref();
const newEndDate = ref();

const handleStartDate = (newDate) => {
  console.log("handleStartDate");
  newStartDate.value = newDate;
};
const handleEndDate = (newDate) => {
  console.log("handleEndDate");
  newEndDate.value = newDate;
};
//시뮬레이션 버튼 핸들링 이벤트
const handleSimulationButton = async () => {
  nowLoading.value = true;
  await getNewResult(newStartDate, newEndDate, selectedOhtId);
  nowLoading.value = false;
  // initialPage.value = false;
};

const initialPage = computed(() => {
  console.log("isDataLoaded: ", !usesimulationStore.isDataLoaded);
  return !usesimulationStore.isDataLoaded;
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
      <Text2
        text="개별이송체의 시스템로그를 통해 시뮬레이션과 분석결과를 받아보세요."
      />
      <div class="input-data">
        <SearchInput
          :props-start-date="simulationStore().startDate"
          :props-end-date="simulationStore().endDate"
          @update-start="handleStartDate"
          @update-end="handleEndDate"
        />
        <div>
          <multiselect
            id="oht-selector"
            v-model="selectedOhtId"
            :options="options"
            placeholder="OHT 호기 선택"
            label="name"
            style="width: 200px"
            selectLabel="선택하기"
          ></multiselect>
        </div>
        <Button
          @click="handleSimulationButton"
          title="Simulation"
          backgroundColor="black"
          fontColor="white"
        />
      </div>
    </section>
    <Line></Line>
    <!-- 아직 검색 안했을때 -->
    <div v-if="initialPage"><InitialPage /></div>
    <!-- 검색결과 -->
    <div v-else>
      <section class="result">
        <!-- 시뮬레이션 -->
        <div class="white-box simulation-box">
          <section class="title">
            <Cardhead
              headerText="Simulation"
              :contentText="`${transformatDate(
                simulationStore().startDate
              )} - ${transformatDate(simulationStore().endDate)} 기간동안 ${
                simulationStore().ohtId
              }호기의 시뮬레이션 입니다.`"
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
                  :labels="simulationStore().timeArray"
                  :work-per-all="simulationStore().averageArray"
                  :work-per-one="simulationStore().meArray"
                  :oht-id="`${ohtId}호기`"
                />
              </div>
            </div>
          </div>
          <!-- 블랙데이터카드 -->
          <div class="black-card-box">
            <div class="black-card-content">
              <BlackDataCard
                title="총 작업량"
                :content="simulationStore().totalWork.data"
                :percentage="simulationStore().totalWork.percent + '%'"
                :fontColor="
                  simulationStore().totalWork.percent >= 0 ? 'red' : 'blue'
                "
                :height="'130px'"
                width="250px"
              />
              <BlackDataCard
                title="데드라인 초과"
                :content="simulationStore().outOfDeadline.data"
                :percentage="simulationStore().outOfDeadline.percent + '%'"
                :fontColor="
                  simulationStore().outOfDeadline.percent >= 0 ? 'red' : 'blue'
                "
                :height="'130px'"
                width="250px"
              />
            </div>
            <div class="black-card-content">
              <BlackDataCard
                title="평균 속도"
                :content="simulationStore().averageSpeed.data"
                :percentage="simulationStore().averageSpeed.percent + '%'"
                :fontColor="
                  simulationStore().averageSpeed.percent >= 0 ? 'red' : 'blue'
                "
                :height="'130px'"
                width="250px"
              />
              <BlackDataCard
                title="OHT 에러"
                :content="simulationStore().ohtError.data"
                :percentage="simulationStore().ohtError.percent + '%'"
                :fontColor="
                  simulationStore().ohtError.percent >= 0 ? 'red' : 'blue'
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
              :headerText="
                '총 작업 로그(' + simulationStore().totalCnt + '건)'
              "
              contentText="각 작업을 클릭하여 해당하는 로그를 시뮬레이션과 함께 확인하실 수 있습니다."
            />
          </section>
          <section class="table-container">
            <SideTabTable
              class="table-component"
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
              :data="simulationStore().logPerWork"
              :ohtId="simulationStore().ohtId"
            />
          </section>
        </div>
      </section>
    </div>
  </div>
</template>

<style scoped>
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
  width: 100%;
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
  max-height: 550px;
  overflow-x: hidden;
}
.table-component {
  padding: 10px;
}

.barchart-box {
  width: 54%;
}

.content {
  width: 96%;
}
</style>
