<script setup>
import BlackDataCard from "@/components/card/BlackDataCard.vue";
import Table from "@/components/table/Table.vue";
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
</template>
<style scoped></style>
