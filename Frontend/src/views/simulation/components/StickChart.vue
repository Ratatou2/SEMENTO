<script setup>
import moment from "moment";
import { Bar } from "vue-chartjs";
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  BarElement,
  CategoryScale,
  LinearScale,
} from "chart.js";

ChartJS.register(
  Title,
  Tooltip,
  Legend,
  BarElement,
  CategoryScale,
  LinearScale
);

const workPerOne = [
  7, 9, 6, 7, 10, 2, 8, 9, 3, 5, 11, 7, 9, 6, 7, 10, 2, 8, 9, 3, 5, 11,
];
const workPerAll = [
  8, 11, 5, 4, 5, 6, 9, 10, 7, 2, 3, 7, 9, 6, 7, 10, 2, 8, 9, 3, 5, 11,
];

const chartData = {
  labels: [
    [transformatDate(new Date())[0], transformatDate(new Date())[1]],
    [transformatDate(new Date())[0], transformatDate(new Date())[1]],
    [transformatDate(new Date())[0], transformatDate(new Date())[1]],
    [transformatDate(new Date())[0], transformatDate(new Date())[1]],
    [transformatDate(new Date())[0], transformatDate(new Date())[1]],
    [transformatDate(new Date())[0], transformatDate(new Date())[1]],
    [transformatDate(new Date())[0], transformatDate(new Date())[1]],
    [transformatDate(new Date())[0], transformatDate(new Date())[1]],
    [transformatDate(new Date())[0], transformatDate(new Date())[1]],
    [transformatDate(new Date())[0], transformatDate(new Date())[1]],
    [transformatDate(new Date())[0], transformatDate(new Date())[1]],
    [transformatDate(new Date())[0], transformatDate(new Date())[1]],
    [transformatDate(new Date())[0], transformatDate(new Date())[1]],
  ],
  datasets: [
    {
      barPercentage: 0.5,
      label: "1918호기 작업량",
      backgroundColor: "#003CB0",
      data: workPerOne,
      barThickness: 20, // 막대의 두께(px).
    },
    {
      barPercentage: 0.5,
      label: "전체 OHT 작업량 평균",
      backgroundColor: "#E8E8E8",
      data: workPerAll,
      barThickness: 20, // 막대의 두께(px).
    },
  ],
};

const chartOptions = {
  borderRadius: 5,
  plugins: {
    legend: {
      //막대의미설명
      display: false,
      align: "start",
    },
    tooltip: {
      enabled: true,
      backgroundColor: "#000",
      padding: 10,
    },
    datalabels: {
      display: false,
    },
  },

  scales: {
    y: {
      //y축 글씨
      ticks: {
        display: true,
        stepSize: () => {
          const max1 = Math.max(...workPerOne);
          const max2 = Math.max(...workPerAll);
          const max = Math.max(max1, max2);
          // 대략 4단계로 나뉘면 좋을것같으므로
          console.log(max);
          return max % 4;
        },
      },
      grid: {
        drawTicks: false, //글씨쪽 튀어나온부분 선
        color: "#ECE9F1",
        lineWidth: 1,
      },
      border: {
        display: false, //y축 안보이게
      },
    },
    x: {
      grid: {
        display: false,
      },
    },
  },
  maintainAspectRatio: false,
  responsive: true, //크기를 부모요소에 맞춤
};

function transformatDate(date) {
  return [moment(date).format("MM.DD"), moment(date).format("HH:MM:SS")];
}
</script>

<template>
  <div class="container"><Bar :options="chartOptions" :data="chartData" /></div>
</template>

<style scoped>
.container {
  width: 100%;
  height: 100%;
}
</style>
