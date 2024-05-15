from fastapi import HTTPException, APIRouter
import numpy as np
import pandas as pd
import category_encoders as ce
import json
from services.logs import get_logs
from services.data_preprocess_by_path import data_preprocessing_for_Conan
from services.data_preprocess_by_time import data_preprocessing_by_time
import os
import tensorflow as tf
from tensorflow.keras.models import load_model
from tqdm import tqdm
from multiprocessing import Pool

conan = load_model('./ai_models/path_model')
time_model = load_model('./ai_models/time_model.h5')

root_router = APIRouter()

@root_router.get('/analytics/path')
async def analyze_by_Conan(
    start_date: str,
    end_date: str
):
    try:
        # 데이터 조회
        conan.summary()
        logs = get_logs(start_date, end_date)

        # 정체 path 조회 - 데이터 전처리 
        preprocess_result = await data_preprocessing_for_Conan(logs)    
        deadlock_paths = preprocess_result["dataset"]    
        x_input = [path_matrix for (path_name, path_matrix) in deadlock_paths]
        analyse_results = conan.predict(tf.convert_to_tensor(np.array(x_input, dtype=np.float32), dtype=tf.float32))
        paths = [path_name for (path_name, path_matrix) in deadlock_paths]
        results = [float(analyse_result[0]) for analyse_result in analyse_results]
        
        return {"analyse_result": results, "error_info": preprocess_result["error_info"]}
    except Exception as e:
        print(e)
        raise HTTPException(status_code=500, detail=str(e))
    

@root_router.get('/analytics/time')
async def analyze_by_Conan(
    start_date: str,
    end_date: str
):
    try:
    #     데이터 조회
        time_model.summary()
        logs = get_logs(start_date, end_date)
        
        deadlock_paths_and_error_info = await data_preprocessing_by_time(logs)        
        analyse_results = time_model.predict(tf.convert_to_tensor(np.array(deadlock_paths_and_error_info["dataset"], dtype=np.float32), dtype=tf.float32))
        results = []
        for analyse_result in analyse_results:
            result_obj = {}
            result_obj["etc error probability"] = float(analyse_result[0])
            result_obj["facility error probability"] = float(analyse_result[1])
            result_obj["oht error probability"] = float(analyse_result[2])
            results.append(result_obj)
    
        return {"error_info" : deadlock_paths_and_error_info["error_info"], "analyse_result": results}
    except Exception as e:
        print(e)
        raise HTTPException(status_code=500, detail=str(e))