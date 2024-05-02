<script setup>
import HeadText from "@/components/Text/HeadText.vue";
import Simulation from "@/components/simulation/Simulation.vue";
import Table from "@/components/table/Table.vue";
</script>

<template>
  <div class="background">
    <div class="container">
      <section class="head-title">
        <div class="badge">#6</div>
        <HeadText />
      </section>
      <section class="simulation"><Simulation /></section>
      <section class="table"><Table />테이블!</section>
    </div>
  </div>
</template>

<style scoped>
.background {
  background-color: #f3f2f7;
  min-height: 100%;
}
.container {
  background-color: azure;
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
</style>
