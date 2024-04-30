<script setup></script>

<template>
  <div class="side-bar-container">
    <div class="logo-container">
      <div class="logo">Semento<span class="logo-dot">.</span></div>
      <div class="logo-subheading">Modern Admin Dashboard</div>
    </div>
    <div class="nav-bar-container">
      <div class="dashboard-container">
        <font-awesome-icon :icon="['fas', 'house']" style="color: white" />
        <span class="dashboard">Dashboard</span>
      </div>
      <div class="analytics-container">
        <span class="analytics">Analytics</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.side-bar-container {
  height: 100%;
  width: 18%;
  background-color: black;
  position: fixed;
  top: 0;
  left: 0;
  line-height: 1.2;
}

.logo-container {
  padding-left: 10%;
}

.logo {
  color: white;
  font-size: 2rem;
  font-weight: bold;
}

.logo .logo-dot {
  color: #5932ea;
  font-size: 4rem;
}

.logo-subheading {
  color: #b9bbbd;
}

.dashboard-container {
  padding: 15% 0 0 15%;
}

.dashboard {
  padding-left: 10%;
  color: white;
}
</style>
