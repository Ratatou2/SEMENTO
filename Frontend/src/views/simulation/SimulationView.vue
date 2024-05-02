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

// const nameWithLang = ({ name }) => `${name} `;

const cardData = [
  {
    title: "Card 제목 1",
    content: "value 값 1",
    percentage: "증감율 1",
    fontColor: "blue",
    height: "높이값",
    width: "너비값",
  },
  {
    title: "Card 제목 2",
    content: "value 값 2",
    percentage: "증감율 2",
    fontColor: "red",
    height: "높이값",
    width: "너비값",
  },
  {
    title: "Card 제목 3",
    content: "value 값 3",
    percentage: "증감율 3",
    fontColor: "blue",
    height: "높이값",
    width: "너비값",
  },
  {
    title: "Card 제목 4",
    content: "value 값 4",
    percentage: "증감율 4",
    fontColor: "red",
    height: "높이값",
    width: "너비값",
  },
];
</script>

<template>
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
    <Line id></Line>
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
              height="높이값"
              width="250px"
            />
            <BlackDataCard
              title="Out Of DeadLine"
              content="20"
              percentage="-1%"
              fontColor="red"
              height="높이값"
              width="250px"
            />
          </div>
          <div class="black-card-content">
            <BlackDataCard
              title="Average Speed"
              content="4.2m/s"
              percentage="+10%"
              fontColor="blue"
              height="높이값"
              width="250px"
            />
            <BlackDataCard
              title="OHT ERROR"
              content="12"
              percentage="+1.6%"
              fontColor="blue"
              height="높이값"
              width="250px"
            />
          </div>
        </div>
      </section>
    </section>
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

.barchart-box {
  width: 54%;
}

.white-box {
  gap: 15px;
  padding: 20px 10px 10px 0px;
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
