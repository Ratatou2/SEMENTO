<!-- 
** WhiteCard 사용법 **

 <WhiteCard
 title="Card 제목"
 startTime="시작 시간"
 endTime="끝나는 시간"
 height="높이값"
 width="너비값"
 />

-->
<script setup>
const props = defineProps({
  title: {
    type: String,
    default: "",
  },
  startTime: {
    type: String,
    default: "",
  },
  endTime: {
    type: String,
    default: "",
  },
});
</script>
<template>
  <div class="card-container">
    <div class="card-title">
      <font-awesome-icon
        :icon="['fas', 'circle']"
        size="2xs"
        style="color: lightgreen"
      />
      {{ title }}
    </div>
    <div class="card-content-container">
      <div class="card-content">{{ startTime }} * {{ endTime }}</div>
    </div>
  </div>
</template>
<style>
.card-container {
  background-color: white;
  color: black;
  border-radius: 8px;
  padding: 10px 20px;
  width: 345px;
  height: 83px;
  border: #6f7277 1 solid;
}

.card-title {
  font-weight: 600;
  font-size: 15px;
}

.card-content-container {
  display: flex; /* 가로로 나열 */
  justify-content: space-between; /* 양쪽 정렬 */
  align-items: center; /* 수직 가운데 정렬 */
}

.card-content {
  font-size: 15px;
  font-weight: 400;
  color: #6f7277;
}
</style>
