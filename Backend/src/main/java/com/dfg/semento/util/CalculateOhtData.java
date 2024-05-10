package com.dfg.semento.util;

/** OHT 데이터에 대해 다양한 통계를 만드는 클래스
 */
public class CalculateOhtData {

    /** 두 데이터의 차이 퍼센테이지를 구함
     * @author 최서현
     * @param ohtValue
     * @param overallValue
     * @return
     */
    public static double getDifferencePercentage(double ohtValue, double overallValue){
        return  ((ohtValue - overallValue) / overallValue) * 100;
    }
}
