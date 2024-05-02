<script setup>
import { reactive, onMounted } from 'vue';
import { Chart, registerables } from 'chart.js';
import { Pie } from 'vue-chartjs';
import ChartDataLabels from 'chartjs-plugin-datalabels';

Chart.register(...registerables);
Chart.register(ChartDataLabels);

const data = [87, 13]; // Success, Fail 순 퍼센트

const chartData = reactive({
  labels: [
    'Success',
    'Fail'
  ],
  datasets: [{
    data,
    backgroundColor: [
      '#003CB0',
      '#9AC8FC'
    ],
    hoverOffset: 4,
    pointStyle: 'Rounded',
    pointRadius: 1,
  }]
});

const chartOptions = reactive({
    responsive: true,
    maintainAspectRatio: false,
    rotation: 360 * (data[1] / 100) + 45, // 파이차트 시작 위치 조정
    plugins: {
        tooltip: {
            backgroundColor: '#ffffff',
            xAlign: 'center',
            yAlign: 'center',
            titleColor: '#555555',
            titleAlign: 'center',
            titleFont: {
                weight: 'bold',
                size: 20,
                lineHeight: 1.5
            },
            bodyColor: '#555555',
            bodyAlign: 'center',
            bodyFont: {
                size: 15,
                lineHeight: 1.5
            },
            displayColors: false, // 색상 제거
        },
        datalabels: {
            formatter: function(value, context) {
                return '';
            },
        },
        legend: {
            labels: {
                usePointStyle: true,
                boxHeight: 6
            }
        },
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
        <Pie :data="chartData" :options="chartOptions" />
    </div>
</template>

<style scoped></style>
