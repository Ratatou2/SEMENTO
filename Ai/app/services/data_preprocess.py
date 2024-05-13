import numpy as np
import pandas as pd
import category_encoders as ce
from concurrent.futures import ProcessPoolExecutor
from config import all_nodes, all_paths, path_and_before_path_info, length_info, facility_length_info
import os
from tqdm import tqdm

from elasticsearch import Elasticsearch

# Elasticsearch 서버에 연결
es = Elasticsearch("http://k10s106.p.ssafy.io:9200/")

# 검색을 수행할 인덱스 설정
index_name = "semento-mysql-logs-2024-07-01"

def get_logs(start_date, end_date):
    # Elastic Search
    # 검색 쿼리 초기화 및 스크롤 설정
    query = {
    "_source": ["oht_id", "mode", "status", "current_node", "next_node", "target_node", "carrier", "error", "oht_connect", "curr_node_offset", "speed", "is_fail", "curr_time", "start_time", "point_x", "point_y", "path"],
    "query": {
        "bool": {
            "must": [
                {
                    "range": {
                        "curr_time": {
                            "gte": f"{start_date}+09:00",
                            "lte": f"{end_date}+09:00"
                        }
                    }
                }
            ]
        }
    },
    "sort": [
        {"curr_time": {"order": "asc"}},
        {"oht_id": {"order": "asc"}}
    ]
    }


    # 스크롤 세션 시작
    scroll = '1m'  # 스크롤 유지 시간
    response = es.search(index=index_name, body=query, scroll=scroll, size=1000)

    arr = []
    # 스크롤을 통해 모든 결과 탐색
    while True:
        # 결과 출력
        for doc in response['hits']['hits']:
            arr.append(doc['_source'])

        # 'scroll_id'를 사용하여 다음 결과 배치 가져오기
        scroll_id = response['_scroll_id']
        response = es.scroll(scroll_id=scroll_id, scroll=scroll)
        
        # 결과가 더 이상 없으면 종료
        if not response['hits']['hits']:
            break
        
    # 스크롤 세션 정리
    es.clear_scroll(scroll_id=scroll_id)
    print(f"{start_date} ~ {end_date} 로그 데이터 조회 완료")
    print(len(arr))
    return pd.DataFrame(arr)


async def data_preprocessing_for_Conan(
    df: pd.DataFrame,
):
    try:

        # MinMaxScailing
        df["speed"] = df["speed"] / 5
        df["point_x"] = (df["point_x"] - 60) / (2040 - 60)
        df["point_y"] = (df["point_y"] - 320) / (560 - 320)
        df["curr_node_offset"] = df["curr_node_offset"] / 8.5

        encoder = ce.BinaryEncoder(cols=['node'])  # 임시 컬럼 인덱스 node 사용
        encoder.fit(pd.DataFrame(all_nodes, columns=['node']))

        # 인코딩할 데이터프레임의 컬럼 이름을 'node'로 변경하여 인코더에 맞춤
        encoded_target = encoder.transform(df[['target_node']].rename(columns={'target_node': 'node'}))
        encoded_current = encoder.transform(df[['current_node']].rename(columns={'current_node': 'node'}))

        encoder_status = ce.BinaryEncoder(cols=['status'])
        encoded_status = encoder_status.fit_transform(df['status'])

        df['is_idle'] = df['status'].apply(lambda x: True if x == 'I' else False)

        # 인코딩된 결과를 원래의 데이터프레임에 새로운 컬럼으로 추가
        df = pd.concat([df, encoded_target.add_suffix('_target'), encoded_current.add_suffix('_current'), encoded_status.add_suffix('_status')], axis=1)
        # df.drop(['target_node', 'current_node', 'curr_time', 'status', 'mode', 'start_time'], axis=1, inplace=True)

        # dataset에서 Fail일때의 deadline 범위 추출

        FAILURE_DEADLINE = 200

        n_ohts = 30

        SNAPSHOT_MATRIX = [[df.iloc[k*n_ohts + idx] for idx in range(n_ohts)] for k in range(len(df) // n_ohts)]
        dataset = []
        labels = []

        # 노드 앞 path 위에 몇대가 있는지의 정보를 담은 객체를 모아놓을 list
        path_count_arr = []
        path_and_second_arr = []
        path_visited = {}
        oht_visited = {}

        for path_info in path_and_before_path_info.iloc[:, 0]:
            path_visited[path_info] = False

        # oht_visited 초기화
        for oht_id in df["oht_id"].unique():
            oht_visited[oht_id] = False


        print("탐지 판단 시작")

        # 탐지 판단
        for now_second, ohts_arr in enumerate(SNAPSHOT_MATRIX):

            # 노드 앞 path 위에 몇대가 있는지를 저장할 객체
            path_count = {}

            for oht in ohts_arr:

                path_name = oht['path']
                path_count.setdefault(path_name, 0)
                # nan 처리
                if type(path_name) == float: continue

                # 유휴상태가 아닌 경우, 속도가 0.7보다 느린 경우 count
                if path_name in length_info.keys() and oht['is_idle'] == 0 and oht['speed'] <= 0.7:
                    path_count[path_name]+=1

            #path 정체가 풀렸는지 확인
            for oht in ohts_arr:
                path_name = oht['path']
                if path_name in length_info and path_name in path_count:
                    temp_count_criterion = 3 if length_info[path_name] >= 5 else 2
                    if path_count[path_name] < temp_count_criterion and path_visited[path_name] and not oht_visited[oht["oht_id"]]:
                        path_visited[path_name] = False
                        
                
            for idx, oth in enumerate(ohts_arr):
                oht_id = oht["oht_id"]
                path_name = oht["path"]
                # oht 에러인 경우
                if oth['error'] == 200 and not oht_visited[oht_id]:

                    is_deadlock_oht_error = False

                    for idx, other_oht in enumerate(ohts_arr):
                        other_offset = other_oht["curr_node_offset"]
                        other_path = other_oht["path"]

                        if other_oht["oht_id"] == oht_id: continue
                        elif other_path == path_name and other_offset != 0 and other_offset < oth["curr_node_offset"]:
                            # 고장난 oht 뒤에 있는 다른 oht의 속도가 0이면 정체
                            is_deadlock_oht_error = True
                            oht_visited[oht_id] = True
                            break
                        elif other_path in path_and_before_path_info[path_and_before_path_info["before_node"] == oht["current_node"]]["path"].values \
                                                and other_offset != 0 and other_oht["status"] != "W" and other_oht["speed"] == 0:
                            # 뒷길확인 로직 - oht에러가 난 호기의 이전 path에 존재하는 경우
                            is_deadlock_oht_error = True
                            oht_visited[oht_id] = True
                            break
                    if is_deadlock_oht_error:
                        path_visited[path_name] = True
                        path_and_second_arr.append((now_second, path_name))
                        break

                # 설비 길이만 참고하는 정체 로직 짜기 - facility_length_info
                elif path_name in facility_length_info and not path_visited[path_name]:

                    oht_visited[oht_id] = False

                    # 길이에 따른 oht 최대 수용 개수
                    count_criterion = 3 if facility_length_info[path_name] >= 5 else 2

                    # 정체 발생했을 경우 (노드 앞 path에 3개 혹은  2개 이상의 oht가 존재할 경우)
                    if path_count[path_name] >= count_criterion:
                        for idx, oth in enumerate(ohts_arr):
                            # 설비에서 일하고 있는 oht를 찾는다 ( status = W )
                            if now_second-FAILURE_DEADLINE >= 0:

                                # 지난 20초간 count_criterion 이상이면 혼잡/정체라고 판단
                                is_facility_deadlock = True
                                for temp_count in path_count_arr[now_second-20:now_second]:
                                    if temp_count.setdefault(path_name, 0) < count_criterion:
                                        is_facility_deadlock = False

                                if is_facility_deadlock:
                                    path_visited[path_name] = True
                                    path_and_second_arr.append((now_second, path_name))
                                    break
                else:
                    oht_visited[oht_id] = False

            path_count_arr.append(path_count)

        DROP_LENGTH = len(df.drop(['oht_id', 'mode', 'status', 'current_node', 'next_node', 'target_node', 'curr_time', 'start_time', 'path', 'error'], axis=1).columns)
        
        print("make path data")
        # 프로세스 풀 생성 및 멀티프로세싱 실행
        SNAPSHOT_MATRIX = [[df.iloc[k*n_ohts + idx] for idx in range(n_ohts)] for k in range(len(df) // n_ohts)]
        results = list(tqdm(make_matrix_by_path(df, path, moment_time, n_ohts, DROP_LENGTH, SNAPSHOT_MATRIX) for (moment_time, path) in path_and_second_arr))

        # # 멀티프로세싱으로 결과 계산
        # with ProcessPoolExecutor(max_workers=8) as executor:
        #     # results = await executor.submit(process_path_data, args)
        #     results = list(tqdm(executor.map(process_path_data, args), total=len(args)))

        return results
    except Exception as e:
        raise e(status_code=500, detail=str(e))


def process_path_data(args):
    df, path, moment_time, n_ohts, DROP_LENGTH, NEW_SNAPSHOT_MATRIX = args

    return make_matrix_by_path(df, path, moment_time, n_ohts, DROP_LENGTH, NEW_SNAPSHOT_MATRIX)

def make_matrix_by_path(df, path_name, moment_time, n_ohts, DROP_LENGTH, NEW_SNAPSHOT_MATRIX):
    FAILURE_DEADLINE = 200
    
    path_matrix = []
    oht_arr = NEW_SNAPSHOT_MATRIX[moment_time]
    
    working_oht = None
    on_facility_oht = None
    ohts = []
    for oht in oht_arr:
        if oht["path"] == path_name:
            if oht["status"] == "W":
                working_oht = oht
            elif oht["status"] != "I":
                if(oht["curr_node_offset"] <= 0):
                    on_facility_oht = oht
                else:
                    ohts.append(oht)
    
    # 'curr_node_offset'의 값을 기준으로 내림차순 정렬
    sorted_ohts = sorted(ohts, key=lambda x: x['curr_node_offset'], reverse=True)
    if working_oht is not None: 
        sorted_ohts.insert(0, working_oht)
    if on_facility_oht is not None:
        if working_oht is None: 
            sorted_ohts.insert(0, on_facility_oht)
        elif working_oht is not None:
            sorted_ohts.insert(1, on_facility_oht)


    # 혼잡 / 정체 발생 시점으로부터 DEADLINE만큼의 시간동안
    for second in range(moment_time-FAILURE_DEADLINE+1, moment_time):
        temp_path_data = [None] * 4 
        # 혼잡 / 정체 발생 시점에 설비 path에 존재했던 oht들을 추적
        for i in range(4):
            if i >= len(sorted_ohts):
                temp_path_data[i] = [0] * DROP_LENGTH
                continue

            oht = sorted_ohts[i]
            
            if oht is None:
                temp_path_data[i] = [0] * DROP_LENGTH
                continue
            
            for temp_oht in NEW_SNAPSHOT_MATRIX[second]:
                if temp_oht["oht_id"] == oht["oht_id"]:
                    temp = temp_oht.drop(['oht_id', 'mode', 'status', 'current_node', 'next_node', 'target_node', 'curr_time', 'start_time', 'path', 'error'])
                    temp_path_data[i] = temp
        
        path_matrix.append(temp_path_data)
    
    while len(sorted_ohts) < 4:
        sorted_ohts.append([0] * DROP_LENGTH)

    last_path = []
    for oht in sorted_ohts:
        if type(oht) == pd.Series:
            last_path.append(oht.drop(['oht_id', 'mode', 'status', 'current_node', 'next_node', 'target_node', 'curr_time', 'start_time', 'path', 'error']))
        else:
            last_path.append(oht)
    
    path_matrix.append(last_path)

    return (path_name, path_matrix)


def temp_get_df():
    directory = './xlsx/test'

        # 디렉토리 내 모든 파일 리스트 가져오기
    files = os.listdir(directory)

        # 'facility_error'로 시작하는 CSV 파일들만 필터링
    csv_files = [file for file in files if file.endswith('.csv')]
        #     print(len(csv_files))
        # 데이터프레임 리스트 초기화
    dataframes = []

        # 필터링된 파일들을 읽어서 데이터프레임 리스트에 추가
    for file in csv_files:
        file_path = os.path.join(directory, file)
        try:
            df = pd.read_csv(file_path)
        except:
            print("?")
        dataframes.append(df)
    return pd.concat(dataframes, axis=0)