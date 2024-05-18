<script setup>
import Text from "@/components/Text/Text.vue";
import Table from "@/components/table/Table.vue";
import CongestionSimulation from "@/components/simulation/CongestionSimulation.vue";
import Line from "@/components/line/Line.vue";
import { useAnalysisStore } from "@/stores/analysis";

const { detectionResult } = useAnalysisStore();

const props = defineProps({
  location: {
    type: Boolean,
    default: false,
  },
  number: {
    type: Number,
    default: 0,
  },
  text: {
    type: String,
    default: "2024.01.07 12:03:21 ~ 2024.01.07 12:05:59 [총 2분 38초]",
  },
  cause: {
    type: String,
    default: "Facility Error",
  },
});
</script>

<template>
  <div class="transparency-box">
    <!-- 제목 -->
    <Text class="title" :location="location" :number="number" :text="text" />
    <Line />
    <!-- 본문 -->
    <section class="content">
      <div class="ai">
        <div class="blue block">[ SEMENTO AI 분석결과 ]</div>
        <div class="white block">{{ cause }}</div>
      </div>
      <div class="simulation-and-table">
        <CongestionSimulation
          class="simulation"
          :errorData="detectionResult[props.number - 1]"
        />
        <div class="table">
          <Table
            width="100%"
            bodyFontSize="14px"
            headerFontSize="12px"
            headerColor="white"
            headerTextColor="black"
            :columns="[
              'TIME',
              'OHT ID',
              'PATH',
              'STATUS',
              'CARRIER',
              'ERROR',
              'SPEED',
              'IS_FAIL',
            ]"
            :data="[
              [
                '2024.01.07 12:03:21',
                '1817',
                'path230',
                'IDLE',
                'FALSE',
                '300',
                '2.3m/s',
                'FALSE',
              ],
              [
                '2024.01.07 12:03:21',
                '1817',
                'path230',
                'IDLE',
                'FALSE',
                '300',
                '2.3m/s',
                'FALSE',
              ],
              [
                '2024.01.07 12:03:21',
                '1817',
                'path230',
                'IDLE',
                'FALSE',
                '300',
                '2.3m/s',
                'FALSE',
              ],
              [
                '2024.01.07 12:03:21',
                '1817',
                'path230',
                'IDLE',
                'FALSE',
                '300',
                '2.3m/s',
                'FALSE',
              ],
              [
                '2024.01.07 12:03:21',
                '1817',
                'path230',
                'IDLE',
                'FALSE',
                '300',
                '2.3m/s',
                'FALSE',
              ],
              [
                '2024.01.07 12:03:21',
                '1817',
                'path230',
                'IDLE',
                'FALSE',
                '300',
                '2.3m/s',
                'FALSE',
              ],
              [
                '2024.01.07 12:03:21',
                '1817',
                'path230',
                'IDLE',
                'FALSE',
                '300',
                '2.3m/s',
                'FALSE',
              ],
              [
                '2024.01.07 12:03:21',
                '1817',
                'path230',
                'IDLE',
                'FALSE',
                '300',
                '2.3m/s',
                'FALSE',
              ],
            ]"
          />
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.simulation-and-table {
  /* border: 1px solid black; */
  width: 95%;
  height: 400px;
  display: flex;
  justify-content: space-between;
}
.simulation {
  width: 33%;
  background-color: white;
  border-radius: 5px;
}
.table {
  width: 65%;
  overflow: scroll;
  overflow-x: hidden;
}
.content {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 15px;
}
.ai {
  display: flex;
  width: 90%;
}
.block {
  border-radius: 5px;
  padding: 10px;
  display: flex;
  justify-content: center;
  font-weight: bold;
}
.blue {
  border-radius: 5px;
  background-color: #003cb0;
  color: white;
  width: 50%;
}
.white {
  color: #003cb0;
  background-color: white;
  width: 50%;
}
.title {
  margin-left: 23px;
}
.transparency-box {
  width: 97%;
  background-color: rgb(255, 255, 255, 0.3);
  display: flex;
  flex-direction: column;
  border-radius: 5px;
  box-shadow: 0px 5px 20px rgba(0, 0, 0, 0.05);
  gap: 15px;
  padding: 20px 10px 10px 0px;
  min-height: 400px;
}
</style>
