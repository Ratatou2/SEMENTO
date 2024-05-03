<script setup>
import { ref } from "vue";
const showInfo = ref(false);
</script>
<template>
  <div class="info-container">
    <font-awesome-icon
      :icon="['fas', 'circle-info']"
      @click="showInfo = !showInfo"
      class="info-icon"
      style="color: #717171; cursor: pointer"
      size="2xl"
    />
    <div v-if="showInfo" class="info-text" :class="{ visible: showInfo }">
      숫자는 해당 구간의 정체 횟수를 나타냅니다.
    </div>
  </div>
</template>
<style scoped>
.info-container {
  display: flex;
  align-items: center;
  flex-direction: row-reverse;
}

.info-icon {
  font-size: 24px; /* 아이콘 크기 조정 */
  line-height: 24px; /* 라인 높이를 아이콘 크기에 맞춤 */
}

.info-text {
  white-space: nowrap;
  background-color: #000000bf;
  color: white;
  padding: 8px;
  border-radius: 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  font-size: 14px;
  line-height: 24px; /* 텍스트 라인 높이를 아이콘 높이에 맞춤 */
  visibility: hidden;
  transform: translateX(-100%); /* 왼쪽으로 숨김 */
  transition: transform 0.5s ease, visibility 0.5s; /* 변환 및 가시성 전환 */
}

.info-container:hover .info-text {
  transform: translateX(0%); /* 왼쪽에서 원래 위치로 슬라이드 */
  visibility: visible;
}
</style>
