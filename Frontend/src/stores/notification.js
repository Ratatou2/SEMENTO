import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { instance } from '@/util/axios-util'


export const useNotificationStore = defineStore('notification', () => {
  //플로팅알림 변수
  const showNotification = ref(true);

  //플로팅 알림 띄우기
  const sendNotification = async (data) => {
    showNotification.value = true;
    setTimeout(() => {
      closeNotification();
    }, 3000); // 3초 후에 알림을 닫음 (조절 가능)
  };

  //플로팅 알림 닫기
  const closeNotification = () => {
    showNotification.value = false;
  };

  return { 
    showNotification,
    sendNotification,
    closeNotification
  }
})
