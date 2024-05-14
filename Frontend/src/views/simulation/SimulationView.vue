<script setup>
import Simulation from "@/components/simulation/Simulation.vue";
import Cardhead from "@/components/Text/Cardhead.vue";
import Text from "@/components/Text/Text.vue";
import SearchInput from "@/components/searchBar/SearchInput.vue";
import Button from "@/components/button/Button.vue";
import Line from "@/components/line/Line.vue";
import { ref } from "vue";
import Multiselect from "vue-multiselect";
import "vue-multiselect/dist/vue-multiselect.css";
import StickChart from "./components/StickChart.vue";
import BlackDataCard from "@/components/card/BlackDataCard.vue";
import SimulationSideTapView from "./SimulationSideTapView.vue";
import Table from "@/components/table/Table.vue";
import Loading from "@/components/loading/Loading.vue";

const nowLoading = ref(false); //로딩창 기본 비활성화

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

const isSidePageOpen = ref(false);

function toggleSidePage(date) {
  //emit으로 받은 date정보를 통해 사이드페이지의 내용을 load

  //show상태로 변경
  isSidePageOpen.value = !isSidePageOpen.value;
}

//emit 받는 핸들러
function toggleSidePageHandler(data) {
  toggleSidePage(data);
}
</script>

<template>
  <div v-if="nowLoading"><Loading /></div>
  <div class="body-container">
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
            <div class="bar-chart-content"><StickChart /></div>
          </div>
        </div>
        <!-- 블랙데이터카드 -->
        <div class="black-card-box">
          <div class="black-card-content">
            <BlackDataCard
              title="Total Work"
              content="1,986"
              percentage="-1.43%"
              fontColor="red"
              :height="'130px'"
              width="250px"
            />
            <BlackDataCard
              title="Out Of DeadLine"
              content="20"
              percentage="-1%"
              fontColor="red"
              :height="'130px'"
              width="250px"
            />
          </div>
          <div class="black-card-content">
            <BlackDataCard
              title="Average Speed"
              content="4.2m/s"
              percentage="+10%"
              fontColor="blue"
              :height="'130px'"
              width="250px"
            />
            <BlackDataCard
              title="OHT ERROR"
              content="12"
              percentage="+1.6%"
              fontColor="blue"
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
            headerText="Log By Total Work(1986건)"
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
            :data="[
              [
                '1',
                '2024.01.07 12:03:21 - 2024.01.07 12:10:30',
                '7m 9',
                '300',
                '2.3m/s',
                'FALSE',
              ],
              [
                '1',
                '2024.01.07 12:03:21 - 2024.01.07 12:10:30',
                '7m 9',
                '300',
                '2.3m/s',
                'FALSE',
              ],
              [
                '1',
                '2024.01.07 12:03:21 - 2024.01.07 12:10:30',
                '7m 9',
                '300',
                '2.3m/s',
                'FALSE',
              ],
            ]"
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
