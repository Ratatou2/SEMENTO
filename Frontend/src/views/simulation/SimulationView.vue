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
      <div class="white-box">
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
    </section>
  </div>
</template>

<style scoped>
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

.white-box {
  width: 97%;
  min-height: 400px;
  gap: 15px;
  padding: 20px 0px 30px 0px;
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
