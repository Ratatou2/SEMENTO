import time
from queue import Queue
from itertools import permutations
from random import choice


OHT_list = ['1', '2', '3', '4', '5', '6', '7', '8', '9', '10', '11', '12', '13', '14', '15',
            '16', '17', '18', '19', '20', '21', '22', '23', '24', '25', '26', '27', '28', '29', '30']

load_list = ['36', '85', '14', '59', '72', '3', '68', '93', '50', '9',
             '46', '88', '71', '99', '16', '29', '87', '30', '95', '61',
             '1', '83', '7', '69', '42', '35', '74', '10', '94', '76',
             '12', '5', '26', '38', '49', '77', '98', '43', '65', '90']

wait_queue = Queue()  # 중복 제거를 위해 set

# 화물 배송을 위한 get_point, drop_point 설정
def generate_delivery_order(dock_list=None, pick=2):
    # 따로 선착장 리스트가 주어지는게 아니라면, 디폴트 실행
    if dock_list is None: dock_list = ['a', 'b', 'c', 'd']

    # 순열 제작
    perm = permutations(dock_list, pick)

    # 순열에서 무작위로 하나를 선택
    selected_permutation = choice(list(perm))

    return selected_permutation


def find_way(get_point, drop_point):
    # 랜덤 start_point
    OHT_location = choice(load_list)

    # start_point부터 get_point까지 길찾기
    # 함수(OHT_location, get_point),

    # get_point부터 end_point까지 길찾기
    # 함수(get_point, end_point)

    return OHT_location, get_point, drop_point


if __name__ == '__main__':
    # 초기 작업 대상 OHT, queue 세팅
    for OHT in OHT_list: wait_queue.put(OHT)

    while (True):
        if not wait_queue.empty():
            current_OHT = wait_queue.get()
            get_point, drop_point = generate_delivery_order()
            print("[길찾기 Info]", current_OHT, "호기", find_way(get_point, drop_point))
            print("큐의 내용물:", end=" ")
            contents = [str(item) for item in wait_queue.queue]
            print(", ".join(contents))
            print("=======================")
        else: print("Queue 대기중")
        time.sleep(1)  # 1초 대기

