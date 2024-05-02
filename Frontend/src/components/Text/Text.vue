<!-- 
** Text 사용법 **

 <Text 
 text = "내용" />
-->
<script setup>
const props = defineProps({
  text: {
    type: String,
    default: "내용",
  },
});
</script>

<template>
  <p>{{ text }}</p>
</template>

<style scoped></style>
