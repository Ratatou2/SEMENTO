<!-- 
** SearchInput 사용법 **

 <SearchInput />

-->
<script setup>
import moment from "moment";
import { ref, watch, defineEmits } from "vue";

const startDate = ref(new Date().setHours(0, 0, 0, 0));
const endDate = ref(new Date.setHours(new Date().getHours(), 0, 0, 0));

const emit = defineEmits(["update-start", "update-end"]);
watch(startDate, (newValue) => {
  emit("update-start", newValue);
});

watch(endDate, (newValue) => {
  emit("update-end", newValue);
});

function transformatDate(date) {
  return moment(date).format("YYYY.MM.DD HH:mm:ss");
}
</script>

<template>
  <section class="container">
    <!-- 시작 -->
    <VDatePicker v-model="startDate" mode="dateTime" is24hr>
      <template #default="{ togglePopover }">
        <button class="date-box" @click="togglePopover">
          {{ startDate == null ? "시작 시간" : transformatDate(startDate) }}
          <font-awesome-icon
            :icon="['fas', 'calendar']"
            size="xs"
            style="color: #667085"
          />
        </button>
      </template>
    </VDatePicker>
    <p>-</p>
    <!-- 끝 -->
    <VDatePicker v-model="endDate" mode="dateTime" is24hr>
      <template #default="{ togglePopover }">
        <button class="date-box" @click="togglePopover">
          {{ endDate == null ? "종료 시간" : transformatDate(endDate) }}
          <font-awesome-icon
            :icon="['fas', 'calendar']"
            size="xs"
            style="color: #667085"
          />
        </button>
      </template>
    </VDatePicker>
  </section>
</template>

<style scoped>
p {
  font-weight: bold;
  font-size: 15px;
}
.container {
  display: flex;
  gap: 10px;
  align-items: center;
}
.date-box {
  width: 200px;
  height: 44px;
  background-color: white;
  border-radius: 5px;
  border: 1px solid rgb(229, 229, 229);
  padding: 5px 13px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0px 5px 20px rgba(0, 0, 0, 0.05);
  font-size: 15px;
}

.date-box:hover {
  cursor: pointer;
}
</style>
