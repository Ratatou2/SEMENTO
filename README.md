## :trophy: SSAFY 10기 자율프로젝트 결선 3위 (전국 159개팀 中 3위)

> [![Hits](https://hits.seeyoufarm.com/api/count/incr/badge.svg?url=https%3A%2F%2Fgithub.com%2Fgjbae1212%2Fhttps%3A%2F%2Fgithub.com%2FDofarming%2FSEMENTOhit-counter&count_bg=%23468FDD&title_bg=%23555555&icon=github.svg&icon_color=%23FFFFFF&title=SEMENTO&edge_flat=false)](https://hits.seeyoufarm.com)  </br>
Samsung Software Academy For Youth 10기 자율프로젝트

 </br>
 
도메인 : `SEMES 기업 연계 AI 프로젝트` </br>
주제 : `AI를 활용한 물류데이터 로그 분석 시스템` </br>
개발 기간 : `2024.04.08 ~ 2024.05.20`

</br>

## :small_blue_diamond: 프로젝트 소개

![메인](https://github.com/user-attachments/assets/74671ba3-963f-48e0-a4c8-3c2eecd35fc0)

기업 SEMES 에서는 많은 양의 OHT들이 Wapper를 옮기며 작업을 합니다. 
OHT들이 작업을 하다보면 다양한 에러들이 발생합니다. 예를 들자면, 설비와의 통신 에러, OHT 자체 에러, 명령 쏠림등이 있습니다. 

이렇게 에러가 발생하면 어떻게 될까요? 바로 정체가 발생합니다. 기존 방식에서는 이렇게 에러로 인하여 정체가 발생하게 되면 OHT가 남긴 로그를 분석하여 문제 발생 지점을 찾고 해결하였습니다. 
하지만 기존 방식은 전문성이 없는 사람의 경우, 시간이 오래 걸리고 이렇게 시간이 길어지게 되면 생산성이 많이 낮아지는 결과를 초래하였습니다. 

따라서, SEMENTO가 로그들을 분석하여 정체 구간을 탐지 하고, 원인을 분석하여 시각화함으로써 전문성이 없는 사람도 문제 및 원인을 빠르게 파악하고 해결을 돕고, 공장들의 생산 라인의 생산 효율 증가에 도움을 드릴 수 있습니다.

</br>

## :small_blue_diamond: 주요 기능 소개
### (1) AnyLogic 시뮬레이터를 통한 실시간 로그 데이터 생성
시뮬레이터를 실행하면 OHT들이 Wapper를 나르는 것을 실시간을 확인할 수 있습니다. 또한, OHT들이 이동하는 것을 1초 단위로 로그를 생성하고 이를 데이터베이스에 저장하고 있습니다.
![simulation](https://github.com/user-attachments/assets/7e451530-6b3e-4a53-a928-ce84fb1ce722)


</br>

### (2) ELK 파이프라인을 통한 대용량 데이터 저장
하루 10만 8천개 데이터가 생성되는 로그데이터 특성상 일반 RDB 사용시 쿼리성능이 떨어질것을 예상하여 그 해결방안으로 Elasticsearch를 도입하였습니다. 초단위 로그가 Elasticsearch에 저장됩니다.
이때 데이터 insert시점에 일별로 Index가 분리되도록 하여 쿼리성능을 대폭 향상시키는 등 방대한 로그를 효율적으로 처리할 수 있도록 구조화 시켰습니다.
![elk](https://github.com/user-attachments/assets/1bb3b28a-118a-4fe0-ac42-af680b193ad3)


</br>


### (3) DashBoard
전체적인 시스템의 작업성공률, 에러 비율, 평균 작업 시간, 시간대별 OHT 상태를 확인할 수 있습니다.
OHT의 한달 기준으로 전 달과 현재 달의 데이터를 비교하여 보여줍니다.
또한, 생성형 AI를 통해 대시보드를 요약 및 정리하여 보여줍니다. 이를 토대로 AI가 제시하는 해결방안을 확인하실 수 있습니다.

![dash1](https://github.com/user-attachments/assets/b9742502-28f5-4da2-bc1a-1c165011c83b)
![dash2](https://github.com/user-attachments/assets/0abf7fce-9bb4-48fc-8d2a-7cfc4d29587d)


</br>

### (4) AI Detection
알고리즘과 신경망을 토대로 구축된 AI를 활용하여 데이터를 분석하는 페이지입니다.

시작시간과 종료시간을 입력 후 AI 탐지 요청 시, 정체 일어난 부분과 정체 원인을 분석하여보여줍니다. 또한, 각각의 정체를 상세하게 보여주어 어느 OHT 혹은 설비가 정체의 원인이 되었는지 한눈에 파악할 수 있도록 도와줍니다.

![ai](https://github.com/user-attachments/assets/35b645d0-c844-419e-b004-58cfdfb80dd7)

</br>

### (5) Simulation
로그를 기반으로 시뮬레이션을 보여주는 페이지입니다.

시뮬레이션을 진행하는 시간을 설정하고 보고 싶은 OHT 선택하면 시뮬레이션을 확인할 수 있습니다. 해당 시뮬레이션에서 보여주는 OHT의 색을 통해 현재의 상황을 파악할 수 있습니다. 또한, 선택한 OHT의 시간별 작업량, 속도들을 파악할 수 있습니다.

![sim](https://github.com/user-attachments/assets/adeee4a4-b6c9-485a-83b3-204c0fb9e213)

</br>
</br>


## :small_blue_diamond: 시스템 아키텍처
![아키텍처](https://github.com/user-attachments/assets/ba308729-27a5-4161-9103-f6d0d08fd2fd)
![데이터 흐름](https://github.com/user-attachments/assets/571c0379-d661-4a5b-9d8f-471593769772)


</br>
</br>

## :small_blue_diamond: AI
> SEMENTO는 **두가지 AI모델**을 도입하였습니다.
</br>

![10만8천개로그생성](https://github.com/user-attachments/assets/7a93cdd9-2852-4ba6-bfd2-76695f2d7969)

</br>

위 그림은 시뮬레이터를 통해 생성한 로그 데이터입니다. 쏟아지는 데이터 속에서 정체가 발생했는지 판단하는 것은 쉬운 작업이 아니었기에 **정체를 탐지하는 AI 모델**을 필요로 하게 되었습니다. 로그 자체가 시계열 데이터이기 때문에 시계열 분석 모델인 `LSTM`과 이상 탐지에 유용한 Auto-Encoder 모델을 응용한 `LSTM-AE` 모델을 사용하였습니다. 

</br>

또한 신입사원이 **“왜” 정체가 발생했는지**를 파악하는 것 또한 어려우며 원인 파악 정확도도 낮았습니다. 그렇기에 로그를 기반으로 정체 발생 원인 별로 이동 패턴을 분석하기 위해 로그 데이터를 3D로 구성하였습니다. 이후 `Convolution Neural Network` 모델을 통해 Classificiation을 진행함으로써 **원인 분석 모델**을 개발하였습니다.

<br>


## :small_blue_diamond: 팀원소개
> `AI팀`과 `시뮬레이션+WEB팀`으로 분리해 진행하였습니다.
<table>
  <tr>
    <td align="center" width="33%"><img src="https://avatars.githubusercontent.com/u/86724344?v=4" width="130px;" alt=""></td>
    <td align="center" width="33%"><img src="https://avatars.githubusercontent.com/u/59328108?v=4" width="130px;" alt=""></td>
    <td align="center" width="33%"><img src="https://avatars.githubusercontent.com/u/61686603?v=4" width="130px;" alt=""></td>
  </tr>
  <tr>
    <td align="center" width="33%"><a href="https://github.com/KHJHK"><b>@KHJHK</b></a></td>
    <td align="center" width="33%"><a href="https://github.com/godjuhyuk"><b>@godjuhyuk</b></a></td>
    <td align="center" width="33%"><a href="https://github.com/Ratatou2"><b>@Ratatou2</b></a></td>
  </tr>
  <tr>
    <td align="center" width="33%"><b>김희중</b></td>
    <td align="center" width="33%"><b>이주혁</b></td>
    <td align="center" width="33%"><b>최진학</b></td>
  </tr>
  <tr>
    <td align="left" width="33%">
      <p>- AI(path 기준, 머신러닝 담당)</p>
      <p>- 프론트엔드 시뮬레이션 파트</p>
      <p>- 인프라 구축</p>
      <p>- 시뮬레이션을 통한 데이터 수집</p>
    </td>
    <td align="left" width="33%">
      <p>- 정체 탐지 로직 구현</p>
      <p>- 시뮬레이션을 통한 데이터 수집</p>
      <p>- 데이터 전처리</p>
      <p>- 모델 학습</p>
      <p>- AI 서버 인프라 구축</p>
      <p>- AI 모델 서빙</p>
    </td>
    <td align="left" width="33%">
      <p>- 시뮬레이션을 통한 데이터 수집</p>
      <p>- AI, LSTM 모델 담당 (기본적인 세팅, 오버샘플링, DataGenerator, Optimizer 테스트)</p>
      <p>- 모델 테스트 및 성능 측정</p>
      <p>- GPU 서버 담당</p>
      <p>- Raw 데이터셋 필터링하여 npy 파일로 변환</p>
    </td>
  </tr>
</table>
<table>
  <tr>
    <td align="center" width="33%"><img src="https://avatars.githubusercontent.com/u/62375220?v=4" width="130px;" alt=""></td>
    <td align="center" width="33%"><img src="https://avatars.githubusercontent.com/u/39721753?v=4" width="130px;" alt=""></td>
    <td align="center" width="33%"><img src="https://avatars.githubusercontent.com/u/89832538?v=4" width="130px;" alt=""></td>
  </tr>
  <tr>
    <td align="center" width="33%"><a href="https://github.com/miraekwak"><b>@miraekwak</b></a></td>
    <td align="center" width="33%"><a href="https://github.com/tngur1101"><b>@tngur1101</b></a></td>
    <td align="center" width="33%"><a href="https://github.com/cheshireHYUN"><b>@cheshireHYUN</b></a></td>
  </tr>
  <tr>
    <td align="center" width="33%"><b>곽미래</b></td>
    <td align="center" width="33%"><b>김수혁</b></td>
    <td align="center" width="33%"><b>최서현</b></td>
  </tr>
  <tr>
    <td align="left" width="33%">
      <p>- AnyLogic을 통한 반도체 물류 반송 시스템 시뮬레이션 구현</p>
      <p>- 시뮬레이션을 통한 데이터 수집</p>
      <p>- Docker를 활용한 인프라 환경 및 Jenkins, GitLab을 활용한 CI/CD 구축</p>
      <p>- 대용량 데이터 처리를 위한 백엔드 파이프라인 설계 및 구축</p>
      <p>- Spring 서버에 Elasticsearch 집계 쿼리 작성</p>
      <p>- UI/UX 디자인 및 프론트엔드 Char.js를 활용한 대시보드 컴포넌트 개발</p>
    </td>
    <td align="left" width="33%">
      <p>- AnyLogic을 통한 반도체 물류 반송 시스템 시뮬레이션 구현</p>
      <p>- 시뮬레이션을 통한 데이터 수집</p>
      <p>- 프론트엔드 개발</p>
      <p>- 데이터 전처리</p>
      <p>- 정체 이상탐지 모델 개발</p>
    </td>
    <td align="left" width="33%">
      <p>- AnyLogic을 통한 반도체 물류 반송 시스템 시뮬레이션 구현</p>
      <p>- 시뮬레이션을 통한 데이터 수집</p>
      <p>- 대용량 데이터 처리를 위한 백엔드 파이프라인 설계</p>
      <p>- Spring 서버에 Elasticsearch 집계 쿼리 작성</p>
      <p>- Spring 서버와 AI서버간 연결 API 작성</p>
      <p>- Exception 핸들링 및 공통로직 Util 작성</p>
      <p>- 프론트엔드 기획(디자인 및 컴포넌트 구조도 작성)</p>
      <p>- D3.js를 이용한 시뮬레이션(OHT 로그 시각화) 컴포넌트 개발</p>
    </td>
  </tr>
</table>

