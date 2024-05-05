<script setup>
import { ref, onMounted, watchEffect } from "vue";

const currentPercent = ref(10);
const percent = ref(45);

onMounted(async () => {
  //프로그래스바
  startAnimation();
});

function startAnimation() {
  const framesPerSecond = 60;
  const totalFrames = 0.2 * framesPerSecond; // Animation duration in seconds

  const interval = setInterval(() => {
    if (currentPercent.value < percent.value) {
      currentPercent.value += (1 / totalFrames) * percent.value;
    } else {
      clearInterval(interval);
    }
  }, 1000 / framesPerSecond);
}
</script>

<template>
    <div class="progress-container">
        <div
        class="progress-bar"
        :style="{ width: currentPercent + '%' }"
        ></div>
    </div>
</template>

<style scoped>
.progress-container {
  width: 100%;
  height: 50px;
  background-color: #e0eaff;
  border-radius: 5px;
  overflow: hidden;
  margin-bottom: 10px;
}

.progress-bar {
  height: 100%;
  background-color: #255bc6;
  transition: width 0.3s ease;
  border-radius: 5px;
}

</style>
