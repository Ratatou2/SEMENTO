<script setup>
import { hierarchy, pack } from 'd3-hierarchy'

const errorData = [
  {
    name: 'Facility Error',
    amount: 35,
    color: '#292D30'
  },
  {
    name: 'OHT Error',
    amount: 60,
    color: '#34B3F1'
  },
  {
    name: 'Scheduling Error',
    amount: 5,
    color: '#BCE0F2'
  }
];

const transformedErrorData = {
  name: 'Top Level',
  children: errorData.map(error => ({
    ...error,
    size: error.amount,
    parent: 'Top Level'
  }))
};

// Generate a D3 hierarchy
const rootHierarchy = hierarchy(transformedErrorData)
  .sum(d => d.size)
  .sort((a, b) => b.value - a.value);

// Pack the circles inside the viewbox
const layoutData = pack()
  .size([300, 350])
  .padding(-10)(rootHierarchy);

</script>

<template>
  <div>
    <svg width="300" height="500">
      <g 
        class="error"
        v-for="error in layoutData.children"
        :key="error.data.name"
        :style="{
          transform: `translate(${error.x}px, ${error.y}px)`
        }"
      >
        <circle class="error__circle" :r="error.r" :fill="error.data.color"></circle>
        <text class="error__label" y="-5">{{ error.data.name }}</text>
        <text class="error__amount" y="20">{{ error.data.amount }}</text>
      </g>
    </svg>
    <div class="controls">
      <div class="control" v-for="error in errors" :key="error.name">
        <label>{{ error.name }}</label>
      </div>
    </div>
  </div>
</template>

<style scoped>
svg {
  display: block;
  margin: 0 auto;
}

.error {
  transition: transform 0.2s ease-in-out;
  text-anchor: middle;
}

.error__circle {
  transition: r 0.2s ease-in-out;
}

.error__label {
  fill: #fff;
  font-weight: bold;
  text-shadow: 0 3px 10px rgba(0, 0, 0, 0.6);
}

.error__amount {
  fill: #fff;
  font-weight: bold;
  font-size: 20px;
  text-shadow: 0 3px 10px rgba(0, 0, 0, 0.6);
}

.controls {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.control {
  display: inline-flex;
  flex-direction: column;
  margin: 0 4px;
}

.control label {
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 4px;
}

.control input {
  display: block;
  font: inherit;
  width: 100px;
}
</style>
