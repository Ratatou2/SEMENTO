<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";

const activeItem = ref("");
const router = useRouter();

function setActive(item, path) {
  activeItem.value = item;
  router.push(path);
}
</script>

<template>
  <div class="side-bar-container">
    <div class="logo-container">
      <div class="logo">Semento<span class="logo-dot">.</span></div>
      <div class="logo-subheading">Modern Admin Dashboard</div>
    </div>
    <div class="nav-bar-container">
      <div
        class="dashboard-container"
        :class="{ active: activeItem === 'dashboard' }"
        @click="setActive('dashboard', '/dashboard')"
      >
        <font-awesome-icon :icon="['fas', 'house']" style="color: white" />
        <span class="dashboard">Dashboard</span>
      </div>
      <div
        class="analytics-container"
        :class="{ active: activeItem === 'analytics' }"
        @click="setActive('analytics', '/analytics')"
      >
        <font-awesome-icon
          :icon="['fas', 'chart-simple']"
          style="color: white"
        />
        <span class="analytics">Analytics</span>
      </div>
      <div
        class="simulation-log-container"
        :class="{ active: activeItem === 'simulation-logs' }"
        @click="setActive('simulation-logs', '/simulation')"
      >
        <font-awesome-icon
          :icon="['fas', 'square-poll-horizontal']"
          style="color: white"
        />
        <span class="simulation-log">Simulation Logs</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
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

.dashboard-container :hover {
  background-color: #0057ff6e;
}

.dashboard {
  padding-left: 10%;
  color: white;
}

.analytics-container {
  padding: 15% 0 0 15%;
}

.analytics-container :hover {
  background-color: #0057ff6e;
}

.analytics {
  padding-left: 10%;
  color: white;
}

.simulation-log-container {
  padding: 15% 0 0 15%;
}

.simulation-log-container :hover {
  background-color: #0057ff6e;
}

.simulation-log {
  padding-left: 10%;
  color: white;
}
</style>
