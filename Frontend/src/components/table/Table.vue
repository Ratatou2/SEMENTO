<!-- 
** Table 사용법 **

 <Table
 column = array
 data = array

 모르겠으면 DashBoardView.vue를 참고하세요
 />

-->
<script setup>
import { ref } from "vue";
const props = defineProps({
  columns: Array, // 테이블의 컬럼명 배열
  data: Array, // 테이블 데이터
  height: {
    type: String,
    default: "auto",
  },
  width: {
    type: String,
    default: "100%",
  },
  headerColor: {
    type: String,
    default: "#000000bd",
  },
  headerFontSize: {
    type: String,
    default: "19px",
  },
  bodyFontSize: {
    type: String,
    default: "15px",
  },
});
const emits = defineEmits(["toggle-side-page"]);
const handleColumnClick = (row) => {
  emits("toggle-side-page", row[1]); // 두 번째 셀의 데이터를 이벤트로 전달
};

function getColumnColor(column) {
  if (column === "ERROR") {
    return "red";
  } else if (column === "STATUS") {
    return "blue";
  }
  return "black";
}
</script>

<template>
  <div
    class="table-container"
    :style="{
      width: width,
      height: height,
    }"
  >
    <table>
      <thead>
        <tr>
          <!-- 각 컬럼명을 헤더로 표시, 배경 및 글자색 설정 -->
          <th
            v-for="(column, index) in columns"
            :key="column"
            :class="{
              'first-th': index === 0,
              'last-th': index === columns.length - 1,
            }"
            class="table-header"
            :style="{ backgroundColor: headerColor, fontSize: headerFontSize }"
          >
            {{ column }}
          </th>
        </tr>
      </thead>
      <tbody>
        <!-- 데이터를 행과 셀로 반복하여 표시, 셀 텍스트 중앙 정렬 -->
        <tr
          v-for="(row, index) in data"
          :key="index"
          @click="handleColumnClick(row)"
        >
          <td
            v-for="(cell, columnIndex) in row"
            :key="columnIndex"
            :style="{
              fontSize: bodyFontSize,
            }"
            class="table-cell"
          >
            <div
              :class="{
                'error-cell': columns[columnIndex] === 'ERROR',
                'status-cell': columns[columnIndex] === 'STATUS',
              }"
            >
              <div class="inner-content">
                {{ cell }}
              </div>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
.table-container {
  border-radius: 5px;
  background-color: white;
}
table {
  width: 100%;
  border-collapse: collapse;
}
th.table-header {
  padding: 10px;
  text-align: center;
  /* background-color: #000000bd; */
  color: white;
  font-weight: 600;
  /* font-size: 19px; */
}
th.first-th {
  border-top-left-radius: 5px; /* 첫 번째 th에 왼쪽 둥근 모서리 적용 */
  border-bottom-left-radius: 5px;
}
th.last-th {
  border-top-right-radius: 5px; /* 마지막 th에 오른쪽 둥근 모서리 적용 */
  border-bottom-right-radius: 5px;
}
td.table-cell {
  padding: 10px;
  text-align: center;
  /* background-color: white; */
  color: black;
  /* font-size: 15px; */
  font-weight: 200;
}
td.table-cell div.error-cell {
  display: flex;
  justify-content: center;
  align-items: center;
}
td.table-cell div.error-cell div.inner-content {
  padding: 5px 10px;
  border: 1px solid #e08989;
  display: inline-block;
  color: #ac2e2e;
  font-weight: bold;
}
td.table-cell div.status-cell div.inner-content {
  padding: 5px 10px;
  border: 1px solid #89a7e0;
  display: inline-block;
  color: #2e5aac;
  font-weight: bold;
}
</style>
