<script setup>
import HeadText from "@/components/Text/HeadText.vue";
import Simulation from "@/components/simulation/Simulation.vue";
import Table from "@/components/table/Table.vue";
import Cardhead from "@/components/Text/Cardhead.vue";
import moment from "moment";

function transformatDate(date) {
  return moment(date).format("YYYY.MM.DD HH:MM:SS");
}
</script>

<template>
  <div class="background">
    <div class="container">
      <div class="left">
        <section class="head-title">
          <div class="badge">#6</div>
          <HeadText headerText="2024.01.07 12:03:21 - 2024.01.07 12:10:30" />
        </section>
      </div>
      <section class="white-box simulation-box">
        <section class="title">
          <Cardhead headerText="Simulation" contentText=""></Cardhead>
        </section>
        <section class="content">
          <Simulation />
        </section>
      </section>
      <section class="table">
        <Table
          width="100%"
          bodyFontSize="14px"
          headerFontSize="12px"
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
          ]"
        />
      </section>
    </div>
  </div>
</template>

<style scoped>
.left {
  width: 95%;
}
.head-title {
  margin-top: 30px;
  display: flex;
  gap: 20px;
  align-items: center;
}
.background {
  background-color: #f3f2f7;
  width: 100%;
}
.container {
  /* background-color: azure; */
  min-height: 100px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 30px;
}
.badge {
  border-radius: 5px;
  background-color: #383839;
  color: white;
  width: 50px;
  height: 40px;
  font-size: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
}
.simulation-box {
  min-height: 400px;
  overflow: hidden;
  width: 95%;
}
.table {
  width: 95%;
}
</style>
