package ai.steel.analyzer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;

import ai.steel.analyzer.cmmn.FileModule;
import ai.steel.analyzer.ml.module.NaiveBayesClssfier;
import ai.steel.analyzer.ml.vo.NBResultVO;
import ai.steel.analyzer.nlp.api.KonlpAPI;
import ai.steel.analyzer.nlp.vo.KonlpConfigVO;
import ai.steel.analyzer.nlp.vo.MorphemeVO;

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
		
		String document = "ㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁ지원자격ㆍ경력 : 신입aaaaaaaaaaaaaaa";
		Set<String> STOP_KWD_SET = new HashSet<> ();
		Map<String, Integer> kwdScoreMap = new HashMap<> ();
		
		try {
			KonlpAPI konlpAPI = new KonlpAPI(vo);
			//List<String> nnList = konlpAPI.extractNoun(document);
			List<HashMap<String, MorphemeVO>> morphList = konlpAPI.morphemeAnalyze(document);
			Set<String> duplicateKwd = new HashSet<String> ();
			
			for(HashMap<String, MorphemeVO> morphMap : morphList) {
				for(String word : morphMap.keySet()) {
					MorphemeVO morphVO = morphMap.get(word);
					
					// ner이 있을 경우 ner로
					if(morphVO.getNer() != null	) {
						
						for(String kwd : morphVO.getNer().keySet())	{
							List<String> nerList = morphVO.getNer().get(kwd);
							if(!STOP_KWD_SET.contains(kwd) && !duplicateKwd.contains(kwd) &&
								(nerList.contains("JOB") || nerList.contains("TSK") ||
								nerList.contains("SKLC") || nerList.contains("SKLS"))) {
								
								// 추후 직종별 해당 키워드가 아니면 버리는 알고리즘 필요함	
								int score = 2;
								if(kwdScoreMap.containsKey(kwd)) {
									score += kwdScoreMap.get(kwd);
								}
								kwdScoreMap.put(kwd, score);
								duplicateKwd.add(kwd);
							}
						}
					} 
					// ner이 없으면 CN만 가져온다
					else if(morphVO.getTag().contains("CN")){
						for(int i = 0; i < morphVO.getTag().size(); i++) {
							if(!STOP_KWD_SET.contains(morphVO.getToken().get(i)) 
									&& !duplicateKwd.contains(morphVO.getToken().get(i))
									&& morphVO.getTag().get(i).equals("CN")) {
								
								int score = 1;
								if(kwdScoreMap.containsKey(morphVO.getToken().get(i))) {
									score += kwdScoreMap.get(morphVO.getToken().get(i));
								}
								kwdScoreMap.put(morphVO.getToken().get(i), score);
								duplicateKwd.add(morphVO.getToken().get(i));
							}
						}
					}
				}
			}
			
			
			
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
	
	
	/**
	  * Tika 테스트
	  * 
	  * @param filePath
	  * @throws Exception
	  */
	 public String tika(String filePath) throws Exception {
		 String rtnStr = "";
		 File file = new File(filePath);
		 BodyContentHandler handler = new BodyContentHandler();  
		 AutoDetectParser parser = new AutoDetectParser();  
		 Metadata metadata = new Metadata();  
		 try (InputStream stream = new FileInputStream(file)) {  
			 parser.parse(stream, handler, metadata);	             
	         rtnStr = handler.toString();
		 }
		 
		 return rtnStr;
	 }
}
