package ai.steel.analyzer.ml.module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ai.steel.analyzer.cmmn.CommonModule;
import ai.steel.analyzer.ml.vo.NBPriorProbVO;
import ai.steel.analyzer.ml.vo.NBResultVO;

/**
 * 나이브 베이즈 분류 클래스
 * 
 * @author Steel
 * @since 2020.12.14
 */
public class NaiveBayesClssfier {
	protected NBPriorProbVO PRIOR_PROB_VO;		// 사전확률
	protected Map<String, Double> LIKELIHOOD;	// 우도 
	
	CommonModule COMMON; 
	
	public NaiveBayesClssfier () {
		COMMON = new CommonModule();
	}
	
	
	/**
	 * 나이브베이즈 학습
	 * 
	 * @param map
	 * @param X (특징 벡터)
	 * @param Y (클래스)
	 * @param useSmooth (라플라스 스무딩)
	 */
	public boolean train(
			List<Map<String, String>> trainData,
			String[] X, String Y, boolean useSmooth) {
		
		// 빈도맵을 생성
		Map<String, Map<String, Map<String, Integer>>> freqMap = makeFreqMap(trainData, X, Y);
		// debug
		for(String feat : freqMap.keySet()) {
			System.out.println(feat + "=>" + freqMap.get(feat));
		}
		
		// 빈도맵을 기반으로 사전 확률을 구한다
		PRIOR_PROB_VO = calcPriorProb(freqMap);
		System.out.println("class : " + PRIOR_PROB_VO.getClssPriorProb());
		System.out.println("feat : " + PRIOR_PROB_VO.getFeatValPriorProb());
		
		// 빈도맵을 기반으로 우도를 구한다
		LIKELIHOOD = calcLikelihood(freqMap, PRIOR_PROB_VO.getClssCnt());
		System.out.println("======================= likelihood ====================");
		System.out.println(LIKELIHOOD);
		
		if(PRIOR_PROB_VO == null || LIKELIHOOD == null && LIKELIHOOD.size() == 0)
			return false;
		
		return true;
	}
	
		
	/**
	 * 모델을 기반으로 결과 생성
	 * 
	 * @param test X 
	 * @param clss
	 */
	public NBResultVO test(Map<String, String> paramMap, String[] clssArr) {
		NBResultVO vo = new NBResultVO();
		Map<String, Map<String, Double>> featValPriorProb = PRIOR_PROB_VO.getFeatValPriorProb();
		double probX = 0.0;
		boolean probXFlag = true;
		int cnt = 0;
		
		// result
		Map<String, Double> postProbMap = new HashMap<String, Double> ();
		double maxProb = 0.0;
		String maxClss = "";
		
		for(String clss : clssArr) {
			double clssXLike = COMMON.formatDouble(PRIOR_PROB_VO.getClssPriorProb().get(clss), 2);
			
			for(String feat : paramMap.keySet()) {
				// 우도값을 가져온다
				String key = feat + "_" + paramMap.get(feat) + "|" + clss;
				
				// P( X | Clss )				
				//System.out.println(key + "=>" + LIKELIHOOD.get(key));
				clssXLike *= COMMON.formatDouble(LIKELIHOOD.get(key), 2);
				
				Map<String, Double> valPriorProb = featValPriorProb.get(feat);
				
				if(probXFlag) {
					if(cnt == 0) {
						probX = COMMON.formatDouble(valPriorProb.get(paramMap.get(feat)), 2);
					} else {
						probX *= COMMON.formatDouble(valPriorProb.get(paramMap.get(feat)), 2);
					}
				}
				cnt++;
			}			
			probXFlag = false;
			
			System.out.println("=============" + clss + "===============");
			System.out.println("clssXlike => " + COMMON.formatDouble(clssXLike, 4));
			System.out.println("probX => " + COMMON.formatDouble(probX, 4));
			double prob = COMMON.formatDouble(clssXLike, 5)/COMMON.formatDouble(probX, 4);
			postProbMap.put(clss, prob);	// 사후확률 계산
			
			if(prob >= maxProb) {
				maxProb = prob;
				maxClss = clss;
			}
		}
		
		vo.setClss(maxClss);
		vo.setProb(maxProb);
		vo.setPostProbMap(postProbMap);
		
		return vo;
	}
	
	
	/**
	 * 원본 맵을 빈도 맵으로 변경
	 * 
	 * @param map
	 * @return
	 */
	public Map<String, Map<String, Map<String, Integer>>> makeFreqMap(
			List<Map<String, String>> list, String[] X, String Y) {
		
		// feature(temperature), val(cool,hot,mild), (clss=cnt, clss=cnt, clss=cnt)
		Map<String, Map<String, Map<String, Integer>>> freqMap 
						= new HashMap<String, Map<String, Map<String, Integer>>> ();
		
		for(Map<String, String> map : list) {
			for(String feat : X) {
				Map<String, Map<String, Integer>> valMap = null;
				Map<String, Integer> clssCntMap = null;
				
				// 해당 feature가 존재하는 경우
				if(freqMap.containsKey(feat)) {
					// feature 맵을 읽는다
					valMap = freqMap.get(feat);
					
					// 해당 feature맵 안에 value 값이 존재하는 경우
					if(valMap.containsKey(map.get(feat))) {	
						
						// value 맵을 읽어서, clssCntMap을 가져온다
						clssCntMap = valMap.get(map.get(feat));
						
						int cnt = 1;
						// value에 clss 카운트가 존재하면 가져온 후 + 1
						if(clssCntMap.containsKey(map.get(Y))) {
							cnt += clssCntMap.get(map.get(Y));	
						}						
						clssCntMap.put(map.get(Y), cnt);
					} else {
						// feature 안에 해당 value가 없을 경우, 우선 해당 클래스에 1을 생성
						clssCntMap = new HashMap<String, Integer> ();
						clssCntMap.put(map.get(Y), 1);
					}					
					// 클래스 카운트맵을 value맵에 세팅
					valMap.put(map.get(feat), clssCntMap);
					
				} else {
					// 해당 feature가 존재하지 않는 경우 1로 세팅한 map 생성
					valMap = new HashMap<String, Map<String, Integer>> ();
					// feature 안에 해당 value가 없을 경우, 우선 해당 클래스에 1을 생성
					clssCntMap = new HashMap<String, Integer> ();
					clssCntMap.put(map.get(Y), 1); 					
					valMap.put(map.get(feat), clssCntMap);
				}
				
				freqMap.put(feat, valMap);								
			}
			
		}
		
		return freqMap;
	}
	
	
	/**
	 * 사전 확률을 연산 한 후, 결과를 리턴
	 * 
	 * @param map
	 * @return
	 */
	public NBPriorProbVO calcPriorProb(
			Map<String, Map<String, Map<String, Integer>>> freqMap) {

		NBPriorProbVO vo = new NBPriorProbVO ();
		Map<String, Map<String, Double>> featValPriorProb 
							= new HashMap<String, Map<String, Double>> ();
		
		int total = 0;
		for(String feat : freqMap.keySet()) {
			// {mild={no=2, yes=4}, cool={no=1, yes=3}, hot={no=2, yes=2}
			Map<String, Map<String, Integer>> valMap = freqMap.get(feat);
			
			// 확률을 계산하기 위해 total값 세팅
			if(total == 0) {
				Map<String, Integer> clssCntMap = new HashMap<String, Integer> ();
				for(String val : valMap.keySet()) {
					Map<String, Integer> cntMap = valMap.get(val);
					for(String clss : cntMap.keySet()) {
						total += cntMap.get(clss);
						
						int cnt = cntMap.get(clss);
						if(clssCntMap.containsKey(clss)) {
							cnt += clssCntMap.get(clss);							
						}
						clssCntMap.put(clss, cnt);				
					}
				}				
				vo.setClssCnt(clssCntMap);	// 클래스별 카운트 값을 세팅(우도 연산 위함)
				
				// 클래스 사전 확률 세팅
				Map<String, Double> clssPriorProb = new HashMap<String, Double> ();
				for(String clss : clssCntMap.keySet()) {
					clssPriorProb.put(clss, (double)clssCntMap.get(clss)/total);
				}
				vo.setClssPriorProb(clssPriorProb);
			}
			
			// 특징별 사전확률 세팅
			Map<String, Double> valPriorProb = new HashMap<String, Double> (); 
			for(String val : valMap.keySet()) {
				int valCnt = 0;
				Map<String, Integer> cntMap = valMap.get(val);
				
				for(String clss : cntMap.keySet()) {
					valCnt += cntMap.get(clss);
				}
				
				valPriorProb.put(val, (double)valCnt/total);
				//System.out.println(val + "=>" + valCnt);				
			}			
			featValPriorProb.put(feat, valPriorProb);
		}		
		vo.setFeatValPriorProb(featValPriorProb);
		
		return vo;
	}	
	
	
	/**
	 * 우도 연산
	 * 
	 * @param freqMap
	 * @param clssCntMap
	 * @return
	 */
	public Map<String, Double> calcLikelihood(
			Map<String, Map<String, Map<String, Integer>>> freqMap,
			Map<String, Integer> clssCntMap) {
		
		Map<String, Double> likelihoodMap = new HashMap<String, Double> ();
		
		for(String feat : freqMap.keySet()) {
			Map<String, Map<String, Integer>> valMap = freqMap.get(feat);
			for(String val : valMap.keySet()) {
				String key = feat + "_" + val;	// feature와 value를 합쳐 키 생성
				
				Map<String, Integer> clssMap = valMap.get(val);
				for(String clss : clssMap.keySet()) {
					int cnt = clssMap.get(clss);
					
					// P(key | clss)
					likelihoodMap.put(key + "|" + clss, (double)cnt/clssCntMap.get(clss));					
				}					
			}
		}
		
		
		return likelihoodMap;		
	}	
}
