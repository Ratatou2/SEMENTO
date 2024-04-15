from enum import Enum
from random import random

# 에러 종류 ENUM
class Error(Enum):
    COMMUNICATION = "COMMUNICATION"
    OHT = "OHT"
    FACILITY = "FACILITY"

# 에러 발생시키는 함수
# 에러의 확률 순위 : 확률은 [통신에러] > [OHT에러] > [설비에러] 순으로 높다
def error_occur(communication_prob=0.7, OHT_prob=0.2, facility_prob=0.1):
        # 각 확률을 따로 계산
        communication_random = random()
        OHT_random = random()
        facility_random = random()
        delay = 0

        # print("[통신] 에러 확률 : ", communication_random, " / ", communication_prob)
        # print("[OHT] 에러 확률 : ", OHT_random, " / ", OHT_prob)
        # print("[설비] 에러 확률 : ", facility_random, " / ", facility_prob)

        if communication_random < communication_prob:
            delay += error_delay("COMMUNICATION")
        elif OHT_random < communication_prob + OHT_prob:
            delay += error_delay("OHT")
        elif facility_random < communication_prob + OHT_prob + facility_prob:
            delay += error_delay("FACILITY")


        print("Delay : ", delay)


# 에러 발생시 딜레이 발생
def error_delay(cause):
    # 각 에러에 대한 딜레이 (단위 : sec)
    communication_delay = 2
    OHT_delay = 5
    facility_delay = 10

    if cause == Error.COMMUNICATION.value:
        print("[System] 통신 에러")
        return communication_delay

    elif cause == Error.OHT.value:
        print("[System] OHT 에러")
        return OHT_delay

    elif cause == Error.FACILITY.value:
        print("[System] 설비 에러")
        return facility_delay

    else:
        print("알 수 없는 에러")


if __name__ == '__main__':

    # 함수 호출
    error_occur()
