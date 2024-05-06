<!-- 
** Text 사용법 **

 <Text 
 text = "내용" 
 size = "폰트크기" />
-->
<script setup>
const props = defineProps({
  text: {
    type: String,
    default: "내용",
  },
  size: {
    type: String,
    default: "15px",
  }
});
</script>

<template>
  <p :style="{ fontSize : size}">{{ text }}</p>
</template>

<style scoped>
</style>
