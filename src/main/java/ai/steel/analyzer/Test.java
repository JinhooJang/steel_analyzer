package ai.steel.analyzer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ai.steel.analyzer.cmmn.FileModule;
import ai.steel.analyzer.ml.module.NaiveBayesClssfier;
import ai.steel.analyzer.ml.vo.NBResultVO;

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
		String document = "업무 욕이 과도하여 하나의 일에 집중하기 보다 여러가지 "
				+ "업무를 동시적으로 수행하려고 하여 다소 시간이 지체되는 경우가 있습니다.";
		
		
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
