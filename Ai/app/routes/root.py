from fastapi import HTTPException, APIRouter
import numpy as np
import pandas as pd
import category_encoders as ce
import json
from services.data_preprocess import get_logs, data_preprocessing_for_Conan
import os
import tensorflow as tf
from tensorflow.keras.models import load_model
from tqdm import tqdm
from multiprocessing import Pool

conan = load_model('./ai_models/jh_conv2D_by_path')
conan.summary()

root_router = APIRouter()

@root_router.get('/analytics/analysation')
async def analyze_by_Conan(
    start_date: str,
    end_date: str
):
    try:
        # 데이터 조회
        logs = get_logs(start_date, end_date)

        # 정체 path 조회 - 데이터 전처리 
        deadlock_paths = await data_preprocessing_for_Conan(logs)        
        x_input = [path_matrix for (path_name, path_matrix) in deadlock_paths]
        analyse_results = conan.predict(tf.convert_to_tensor(np.array(x_input, dtype=np.float32), dtype=tf.float32))
        paths = [path_name for (path_name, path_matrix) in deadlock_paths]
        results = [float(analyse_result[0]) for analyse_result in analyse_results]
        
        return {"path_result" : paths, "analyse_result": results}
    except Exception as e:
        print(e)
        raise HTTPException(status_code=500, detail=str(e))