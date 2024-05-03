<script setup>
import BlackDataCard from "@/components/card/BlackDataCard.vue";
import Table from "@/components/table/Table.vue";
import Info from "@/components/info/Info.vue";
import WhiteCard from "./components/state-analysis/WhiteCard.vue";
</script>
<template>
  <div>Lorem</div>
  <!-- <BlackDataCard
    title="OHT Usage"
    content="30 대"
    percentage="+1.43%"
    :width="'377px'"
    :height="'142px'"
  /> -->
  <Table
    :columns="['No.', 'OHT ID', 'ERROR', 'COUNT']"
    :data="[
      ['1', '1923', '300', '23회'],
      ['2', '1932', '300', '2회'],
    ]"
  />
  <Info />
  <WhiteCard
    title="작업이 가장 많은 시간대"
    startTime="14:00"
    endTime="16:00"
  />
</template>
<style scoped></style>
