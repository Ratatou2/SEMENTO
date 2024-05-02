<script setup>
import { reactive, onMounted } from 'vue';
import { Chart, registerables } from 'chart.js';
import { Doughnut } from 'vue-chartjs';
import ChartDataLabels from 'chartjs-plugin-datalabels';

const props = defineProps({
  width: {
    type: String,
    default: "100%",
  },
  height: {
    type: String,
    default: "300px",
  },
});

const data = [10, 30, 60];

Chart.register(...registerables);
Chart.register(ChartDataLabels);

const chartData = reactive({
  labels: ['Scheduling Error', 'OHT Error', 'Facility Error'],
  datasets: [
    {
      backgroundColor: ['#BCE0F2', '#59A7FF', '#292D30'],
      data,
      borderWidth: 0
    }
  ]
}); 

const chartOptions = reactive({
  responsive: true,
  maintainAspectRatio: false,
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
            color: '#ffffff',
            formatter: function(value, context) {
                return value+'%';
            },
        },
        legend: {
            labels: {
                usePointStyle: true,
                boxHeight: 6
            }
        },
    }, 
})

// Chart.js가 반응형 데이터를 제대로 처리하도록 setup
onMounted(() => {
  chartData.datasets.forEach(dataset => {
    dataset.data = [...dataset.data]; // 데이터 배열을 새로운 배열로 교체
  });
});
</script>

<template>
    <div :style="{width:width, height:height}">
        <Doughnut :data="chartData" :options="chartOptions" />
    </div>
</template>

<style scoped></style>
