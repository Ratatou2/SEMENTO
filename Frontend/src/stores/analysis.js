import { ref, computed } from "vue";
import { defineStore } from "pinia";
import { instance } from "@/util/axios-util";

export const useAnalysisStore = defineStore("analysisStore", () => {
  const startDate = ref("2024-05-11T20:30:00");
  const endDate = ref("2024-05-11T20:40:00");
  const detectionResult = ref([]);

  const getAiDetection = async () => {
    console.log(startDate.value, endDate.value)
    const resp = await instance.post("/analytics/ai-detection", {
      "start-time": startDate.value,
      "end-time": endDate.value,
    });
    const { data, error } = resp;
    if (error) alert("Ai Detection Data Not Found \n", error);
    else {
      detectionResult.value = data["detection-result"];
      console.log(
        "Inside getAiDetection - detectionResult.value: ",
        detectionResult.value
      );
    }
  };
  const computedDetectionResult = computed(() => detectionResult.value);

  return {
    startDate,
    endDate,
    computedDetectionResult,
    getAiDetection,
  };
});
