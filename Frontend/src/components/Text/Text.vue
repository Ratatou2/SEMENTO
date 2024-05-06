<!-- 
** Text 사용법 **

 <Text 
 locaion = false
 number =1
 text = "내용" />
-->
<script setup>
const props = defineProps({
  location: {
    type: Boolean,
    default: false,
  },
  number: {
    type: String,
    default: "",
  },
  text: {
    type: String,
    default: "내용",
  },
});
</script>

<template>
  <p class="tag" v-if="location">
    <font-awesome-icon
      class="icon"
      :icon="['fas', 'location-dot']"
      size="lg"
      style="color: #003cb0"
    />[DETECTION {{ number }}]
    <span class="content">{{ text }}</span>
  </p>
</template>

<style scoped>
.icon {
  margin-right: 10px;
}
.tag {
  font-size: 18px;
  color: #003cb0;
  font-weight: bold;
}
.content {
  margin-left: 5px;
  color: black;
}
</style>
