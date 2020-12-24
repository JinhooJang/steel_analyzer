package ai.steel.analyzer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ai.steel.analyzer.cmmn.FileModule;
import ai.steel.analyzer.ml.module.NaiveBayesClssfier;
import ai.steel.analyzer.ml.vo.NBResultVO;
import ai.steel.analyzer.nlp.api.KonlpAPI;
import ai.steel.analyzer.nlp.vo.KonlpConfigVO;

/**
 * 모듈들의 기능 테스트를 위한 클래스
 * 
 * @author Steel
 * @since 2020.12.22
 */
public class Test {
	

	/**
	 * 형태소 분석 테스트
	 * @param document 
	 */
	public void koNlpTest() {
		KonlpConfigVO vo = new KonlpConfigVO ();
		
		// 사전이 위치한 경로
		vo.setDicPath("D:/Project/steel/database/dictionary/");
		// 사용할 NER 리스트 
		vo.setNerList("SCH,JOB,SKLC,SKLS,MAJ,TSK,NAT,LOC,LIC,MTR".split(","));
		// 사용자 사전을 사용할 것인지
		vo.setUserYn(true);
		
		String document = "전자출판,마케팅전략·기획,시장조사·분석,업무제휴,전자출판,프로모션";
		
		try {
			KonlpAPI konlpAPI = new KonlpAPI(vo);
			List<String> nnList = konlpAPI.extractNoun(document);
			
			System.out.println(nnList);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 나이브 베이즈 텍스트 분류기
	 */
	public void nbTextClssfier() {
		FileModule file = new FileModule();
		List<Map<String, Object>> excelData = file.readExcelToListMap(
						"D:/Project/steel/database/raw-data/faq.xlsx", 0, false);
		
		System.out.println(excelData);
	}
	
	
	/**
	 * play tennis naive bayes test
	 */
	public void nb() {
		FileModule fileModule = new FileModule();		
		List<Map<String, String>> csvData 
					= fileModule.readCsvToListMap(
							"D:/project/steel/database/raw-data/play.csv", true, ",");
		
		System.out.println(csvData);
		String[] X = csvData.get(0).keySet().toArray(new String[csvData.get(0).size()]);
		
		// 이 값을 나이브베이즈에 연산한다
		NaiveBayesClssfier naiveBayes = new NaiveBayesClssfier ();
		
		if(naiveBayes.train(csvData, "outlook,temperature,humidity,windy".split(","),"play",false)) {
			Map<String, String> paramMap = new HashMap<String, String> ();
			paramMap.put("outlook", "rainy");
			paramMap.put("temperature", "cool");
			paramMap.put("humidity", "normal");
			
			NBResultVO vo = naiveBayes.test(paramMap, "yes,no".split(","));
			System.out.println(vo.getPostProbMap());
			System.out.println("max prob class => " + vo.getClss());
			System.out.println("max prob => " + vo.getProb());
		} else {
			System.out.println("Fail");
		}
	}
}
