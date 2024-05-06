<script setup>
import { RouterLink, RouterView } from "vue-router";

import SideBar from "@/components/sideBar/SideBar.vue";
import Header from "@/components/header/Header.vue";
</script>

<template>
  <div class="container">
    <SideBar id="side-bar" />
    <div class="content">
      <Header />
      <RouterView />
    </div>
  </div>
</template>

<style scoped>
@import "@/assets/reset.css";

.container {
  width: 100%;
  height: 100%;
  background-color: #f3f2f7;
  overflow: scroll;
  overflow-x: hidden; /* 가로스크롤 숨기기 */
}

#side-bar {
  height: 100%;
  width: 17.5%;
  background-color: black;
  position: fixed;
  top: 0;
  left: 0;
  line-height: 1.2;
  z-index: 100;
}

.content {
  margin-left: 18%;
}
</style>

<style>
.white-box {
  background-color: white;
  display: flex;
  flex-direction: column;
  align-items: center;
  border-radius: 5px;
  box-shadow: 0px 5px 20px rgba(0, 0, 0, 0.05);
  gap: 15px;
  padding: 20px 10px 10px 0px;
}

.title {
  width: 100%;
  display: flex;
  flex-direction: start;
}
</style>
