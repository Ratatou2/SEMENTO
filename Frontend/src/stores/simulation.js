import { ref, computed } from "vue";
import { defineStore } from "pinia";
import { instance } from "@/util/axios-util";

export const simulationStore = defineStore("simulationStore", () => {
  const startDate = ref("2023-01-01T00:00:00");
  const endDate = ref("2023-01-01T23:59:59");
  const ohtId = ref(2600);

  //==시뮬레이션 데이터를 로드==
  const getSimulation = async () => {
    const ohtIdList = ref([ohtId.value]); //ohtId를 배열화
    const resp = await instance.post("/simulation/simulation-log", {
      "start-date": startDate.value,
      "end-date": endDate.value,
      "oht-id": ohtIdList.value,
    });
    const { data, error } = resp;
    if (error) alert("SimulatiomData Not Found \n", error);
    else return data;
  };

  //==작업별로 분류된 로그 데이터를 로드==
  const getClassificationLog = async () => {
    const resp = await instance.post("/simulation/classification-log", {
      "start-date": startDate.value,
      "end-date": endDate.value,
      "oht-id": ohtId.value,
    });
    const { data, error } = resp;
    if (error) alert("Classification Log Not Found \n", error);
    else return data;
  };

  //==작업량 평균비교==
  const getChartData = async () => {
    const resp = await instance.post("/simulation/compare-work", {
      "start-date": startDate.value,
      "end-date": endDate.value,
      "oht-id": ohtId.value,
    });
    const { data, error } = resp;
    if (error) alert("Work Data Not Found \n", error);
    else return data;
  };

  //==각종 비교 컴포넌트용==
  const getComparedData = async () => {
    const resp = await instance.post("/simulation/work-information", {
      "start-date": startDate.value,
      "end-date": endDate.value,
      "oht-id": ohtId.value,
    });
    const { data, error } = resp;
    if (error) alert("Compared Data Not Found \n", error);
    else return data;
  };

  return {
    startDate,
    endDate,
    getComparedData,
    getChartData,
    getSimulation,
    getClassificationLog,
  };
});
