from elasticsearch import Elasticsearch
import pandas as pd

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
    response = es.search(index=index_name, body=query, scroll=scroll, size=10000)

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