<script setup>
import { reactive, onMounted } from 'vue';
import { Chart, registerables } from 'chart.js';
import { Bar } from 'vue-chartjs';
import ChartDataLabels from 'chartjs-plugin-datalabels';

// 시간
const labels = [
    '13h', '14h', '15h', '16h', '17h', '18h',
    '19h', '20h', '21h', '22h', '23h', '24h'
];
// 값
const data = [40, 20, 12, 39, 10, 40, 39, 80, 40, 20, 12, 11];


// Chart.js 컴포넌트 등록
Chart.register(...registerables);
Chart.register(ChartDataLabels);

// char 데이터
const chartData = reactive({
  labels,
  datasets: [
    {
      label: false,
      backgroundColor: [
          '#BCE0F2',
          '#BCE0F2',
          '#BCE0F2',
          '#BCE0F2',
          '#BCE0F2',
          '#BCE0F2',
          '#BCE0F2',
          '#BCE0F2',
          '#BCE0F2',
          '#BCE0F2',
          '#BCE0F2',
          '#003CB0', // 마지막 막대만 색깔 다르게
      ],
      data,
      borderRadius: 5,
      barThickness: 20
    }
  ]
});

// chart 옵션
const chartOptions = reactive({
    layout: { 
        padding: 20
    },
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
        datalabels: { // 막대별 값 설정
            color: '#555555',
            font: {
                weight: 'bold',
                size: 13,
            },
            anchor: 'end',
            align: 'top',
            offset: -1,
            formatter: function(value, context) {
                return context.dataset.data[context.dataIndex];// 각 막대 위에 데이터 값을 표시
            }
        },
        legend: { // 범례 제거
            display: false
        },
    },
    scales: {
        x: {
            grid: { // 그리드 제거
                color: 'transparent',
            },
            border: { // x축 제거
                display: false,
            }
        },
        y: {
            ticks: { // Y축 라벨 제거
                drawTicks: false,
                callback: (value, index) => {
                    return "";
                }
            },
            grid: { // 그리드 제거
                color: 'transparent',
            },
            border: { // y축 제거
                display: false,
            },
        }
    },

});

// Chart.js가 반응형 데이터를 제대로 처리하도록 setup
onMounted(() => {
  chartData.datasets.forEach(dataset => {
    dataset.data = [...dataset.data]; // 데이터 배열을 새로운 배열로 교체
  });
});
</script>

<template>
  <div>
    <Bar :data="chartData" :options="chartOptions"/>
  </div>
</template>

<style scoped></style>
