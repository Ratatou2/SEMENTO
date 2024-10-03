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
const fixedDate = new Date(2024, 3, 10); // 4월 10일

const startDate = ref(
  props.propsStartDate ? props.propsStartDate : new Date(fixedDate)
);
const endDate = ref(
  props.propsEndDate ? props.propsEndDate : new Date(fixedDate)
);

const emit = defineEmits(["update-start", "update-end"]);

watch(startDate, (newValue) => {
  // 날짜를 고정된 날짜로 설정
  newValue.setFullYear(fixedDate.getFullYear());
  newValue.setMonth(fixedDate.getMonth());
  newValue.setDate(fixedDate.getDate());
  emit("update-start", newValue);
});

watch(endDate, (newValue) => {
  // 날짜를 고정된 날짜로 설정
  newValue.setFullYear(fixedDate.getFullYear());
  newValue.setMonth(fixedDate.getMonth());
  newValue.setDate(fixedDate.getDate());
  emit("update-end", newValue);
});

function transformatDate(date) {
  return moment(date).format("YYYY.MM.DD HH:mm:ss");
}

// 최소 및 최대 날짜를 4월 10일로 고정
const minDate = new Date(fixedDate);
const maxDate = new Date(fixedDate);

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
      :show-calendar="false"
    >
      <template #default="{ togglePopover }">
        <button class="date-box" @click="togglePopover">
          {{ startDate == null ? "시작 시간" : transformatDate(startDate) }}
          <font-awesome-icon
            :icon="['fas', 'clock']"
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
      :show-calendar="false"
    >
      <template #default="{ togglePopover }">
        <button class="date-box" @click="togglePopover">
          {{ endDate == null ? "종료 시간" : transformatDate(endDate) }}
          <font-awesome-icon
            :icon="['fas', 'clock']"
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
