<script setup>
import moment from "moment";
import { ref, watch } from "vue";

//== 초기값 변경
const props = defineProps({
  propsStartDate: {
    type: Date,
    default: null,
  },
  propsEndDate: {
    type: Date,
    default: null,
  },
});

//== 초기화코드
const startDate = ref(props.propsStartDate);
const endDate = ref(props.propsEndDate);

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

// 최소 및 최대 날짜 설정 (2024년 4월 1일부터 5월 31일까지)
const minDate = new Date(2024, 3, 1); // Months are zero-indexed (3 = April)
const maxDate = new Date(2024, 4, 31);

// VDatePicker에 필요한 옵션 설정
const datePickerOptions = {
  minDate,
  maxDate,
  disableYearPicker: true,
  disableMonthYearSelect: true,
};
</script>

<template>
  <section class="container">
    <!-- 시작 -->
    <VDatePicker
      v-model="startDate"
      mode="dateTime"
      is24hr
      :min-date="minDate"
      :max-date="maxDate"
      :disable-year-picker="true"
      :disable-month-year-select="true"
    >
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
    <VDatePicker
      v-model="endDate"
      mode="dateTime"
      is24hr
      :min-date="minDate"
      :max-date="maxDate"
      :disable-year-picker="true"
      :disable-month-year-select="true"
    >
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
