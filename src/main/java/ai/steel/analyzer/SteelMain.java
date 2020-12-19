package ai.steel.analyzer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ai.steel.analyzer.cmmn.FileModule;
import ai.steel.analyzer.ml.module.NaiveBayesClssfier;
import ai.steel.analyzer.ml.vo.NBResultVO;


/**
 * Steel 분석기를 테스트하는 main 클래스 
 * 
 * @author Steel
 * @since 2020.12.14
 */
public class SteelMain {

	public static void main(String[] args) {
		FileModule fileModule = new FileModule();
		
		List<Map<String, String>> csvData 
					= fileModule.readCsvToListMap("./sample/play.csv", true, ",");
		
		System.out.println(csvData);	// CSV값 출력
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
