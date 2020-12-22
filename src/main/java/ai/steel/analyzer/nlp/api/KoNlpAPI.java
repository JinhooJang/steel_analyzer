package ai.steel.analyzer.nlp.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ai.steel.analyzer.nlp.module.Dictionary;
import ai.steel.analyzer.nlp.module.KoNlp;
import ai.steel.analyzer.nlp.vo.MorphemeVO;
import ai.steel.analyzer.nlp.vo.NlpConfigVO;



/**
 * NLP API 클래스
 * 
 * @author jinhoo.jang
 * @since 2020.12.22
 * 
 * @version 0.8.0 (2020.12.22)
 * 외부 설정 파일을 통해서, 설정을 사용할 수 있게 Flexible하게 변경
 */
public class KoNlpAPI {
	
	protected String NEWLINE = System.getProperty("line.separator");
	
	private KoNlp ANALYZE;
	private NlpConfigVO CONFIG;;
	
	// 기분석된 형태소 사전
	protected HashMap<String, String> morphemeDic;
	// 태그 사전(명사, 동사,형용사)
	protected HashMap<String, String[]> tagDictionary;
	// 부사/관형/감탄 사전
	protected HashMap<String, String[]> tagSubDictionary;
	// 명사+복합명사 사전
	protected HashMap<String, String[]> nnDictionary;
	// 태그 사전(조사,어미)
	protected HashMap<String, String> lastTagDictionary;
	// 개체명 사전
	protected HashMap<String, ArrayList<String>> nerDictionary;
	// 개체명 동의어 사전
	protected HashMap<String, String> nerSynDictionary;
	// 숫자를 위한 사전
	protected HashMap<String, String> snDictionary;
	
	// 종성 ㄱㄲㄳㄴㄵㄶㄷㄹㄺ ㄻ ㄼ ㄽ ㄾ ㄿ ㅀ ㅁ ㅂ ㅄ ㅅ ㅆ ㅇ ㅈ ㅊ ㅋ ㅌ ㅍ ㅎ
    private static final char[] JON = 
			    {0x0000, 0x3131, 0x3132, 0x3133, 0x3134, 0x3135, 0x3136, 
			        0x3137, 0x3139, 0x313a, 0x313b, 0x313c, 0x313d, 
			        0x313e, 0x313f, 0x3140, 0x3141, 0x3142, 0x3144, 
			        0x3145, 0x3146, 0x3147, 0x3148, 0x314a, 0x314b, 
			        0x314c, 0x314d, 0x314e};
	
	
    /**
     * 생성자
     * 
     * @param DICPATH
     * @throws Exception
     */
	public KoNlpAPI(NlpConfigVO CONFIG) throws Exception {
		this.CONFIG = CONFIG;
		this.reloadDictionary();
		
		ANALYZE = new KoNlp(
				morphemeDic, 
				tagDictionary,
				tagSubDictionary, 
				nnDictionary, 
				lastTagDictionary,
				nerDictionary, 
				nerSynDictionary, 
				snDictionary
		);
	}
	
	
	/**
	 * 사전 재기동
	 */
	public boolean reloadDictionary() {
		try {
			Dictionary dictionary = new Dictionary();
			tagDictionary = new HashMap<String, String[]> ();
			tagSubDictionary = new HashMap<String, String[]> ();
			nnDictionary = new HashMap<String, String[]> ();
			lastTagDictionary = new HashMap<String, String> ();		
			nerDictionary = new HashMap<String, ArrayList<String>> ();
			nerSynDictionary = new HashMap<String, String> ();
			snDictionary = new HashMap<String, String> ();
			
			morphemeDic = dictionary.setMorpheme(CONFIG.getDicPath() + "morpheme.dic");	// 기분석된 형태소 분석기
			
			// 한단어로 끝나는 사전 처리
			dictionary.setDictionary(CONFIG.getDicPath() + "mm.dic", tagSubDictionary, "MM");	// 관형사
			dictionary.setDictionary(CONFIG.getDicPath() + "ic.dic", tagSubDictionary, "IC");	// 감탄사
			dictionary.setDictionary(CONFIG.getDicPath() + "ma.dic", tagSubDictionary, "MA");	// 부사
			dictionary.setDictionary(CONFIG.getDicPath() + "va.dic", tagDictionary, "VA");	// 형용사
			dictionary.setDictionary(CONFIG.getDicPath() + "vv.dic", tagDictionary, "VV");	// 동사
			dictionary.setDictionary(CONFIG.getDicPath() + "nn.dic", tagDictionary, "NN");	// 명사
			dictionary.setDictionary(CONFIG.getDicPath() + "cn.dic", tagDictionary, "CN");	// 복합명사
			dictionary.setDictionary(CONFIG.getDicPath() + "vp.dic", tagDictionary, "VP");	// 긍정지정사
			dictionary.setDictionary(CONFIG.getDicPath() + "vn.dic", tagDictionary, "VN");	// 부정지정사
			
			// 아직 태깅이 안된 사전
			dictionary.setDictionary(CONFIG.getDicPath() + "uu.dic", tagDictionary, "UU");	// 미분류
			
			dictionary.setDictionary(CONFIG.getDicPath() + "nn.dic", nnDictionary, "NN");	// 명사
			for(String ner : CONFIG.getNerList()) {
				dictionary.setDictionary(CONFIG.getDicPath() + "ner/" + ner + ".dic", nnDictionary, "NN");
				dictionary.setDictionary(CONFIG.getDicPath() + "ner/" + ner + ".dic", tagDictionary, "NN");
				dictionary.setNerDictionary(CONFIG.getDicPath() + "ner/" + ner + ".dic", nerDictionary, nerSynDictionary, ner);
			}			
			dictionary.setDictionary(CONFIG.getDicPath() + "cn.dic", nnDictionary, "CN");	// 복합명사
			
			// 유저 사전을 사용할지 여부
			if(CONFIG.isUserYn())
				dictionary.setDictionary(CONFIG.getDicPath() + "user.dic", tagDictionary, "USER");	// 유저사전
			
			
			// 단어의 마지막에 붙는 태그 처리
			dictionary.setLastTagDictionary(CONFIG.getDicPath() + "em.dic", lastTagDictionary, "EM");	// 어미
			dictionary.setLastTagDictionary(CONFIG.getDicPath() + "js.dic", lastTagDictionary, "JS");	// 조사
			dictionary.setLastTagDictionary(CONFIG.getDicPath() + "nb.dic", lastTagDictionary, "NB");	// 의존
			//dictionary.setLastTagDictionary(DICPATH + "suf.dic", lastTagDictionary, "SF");	// 접미
			
			// 숫자를 처리하기 위한 처리
			dictionary.setLastTagDictionary(CONFIG.getDicPath() + "sn.dic", snDictionary, "SN");
		} catch(Exception e) {
			System.out.println("reloadDictionary error : " + e.getMessage());
			return false;
		}
		
		return true;
	}
	
	
	/**
	 * 사전 세팅
	 * @param nerNm
	 * @param fileNm
	 */
	public void setDictionary(String folderNm, String fileNm, String nerNm) {
		Dictionary dictionary = new Dictionary();
		
		dictionary.setNerDictionary(CONFIG.getDicPath() + folderNm + "/" + fileNm + ".dic", nerDictionary, nerSynDictionary, nerNm.toUpperCase());
		dictionary.setDictionary(CONFIG.getDicPath() + folderNm + "/" + fileNm + ".dic", tagDictionary, "NN");
		dictionary.setDictionary(CONFIG.getDicPath() + folderNm + "/" + fileNm + ".dic", nnDictionary, "NN");
	}
	
	
	/**
	 * 형태소 분석
	 */
	public List<HashMap<String, MorphemeVO>> morphemeAnalyze(String document) {
		if(document == null || document.trim().length() == 0) return null;
		
		//LOGGER.debug("document : " + document);
		List<HashMap<String, MorphemeVO>> morphemeMap = new ArrayList<HashMap<String, MorphemeVO>> ();
		List<HashMap<String, MorphemeVO>> sentenceMorphMap = null;
		
		document = document.trim()
				.replaceAll("\n", ".")
				.replaceAll("\r", ".");
		String[] sentences = document.split("\\.");
		
		//System.out.println(sentences.length + " sentences.");
		String sentence = "";
		
		// 문장별로 형태소 분석 수행
		for(int i = 0; i < sentences.length; i++) {
			sentence = sentences[i].toLowerCase().trim();
			if(sentence.length() == 0) continue;
			sentenceMorphMap = new ArrayList<HashMap<String, MorphemeVO>> ();
			
			// 문장을 다시, 단어로 자른다
			String[] word = sentence.trim().split(" ");
			for(int j = 0; j < word.length; j++) {
				if(word[j].trim().length() > 0) {
					HashMap<String, MorphemeVO> map = new HashMap<String, MorphemeVO> ();
					MorphemeVO morphVO = new MorphemeVO ();
					morphVO.setWord(word[j]);
					ANALYZE.wordAnalyze(word[j], morphVO, false);
					
					// 2019.10.25 UK가 있을 경우, 모두 UK로 변환
					if(morphVO.getTag().contains("UK")) {
						morphVO = ANALYZE.setUKMorph(word[j]);
					}
					
					map.put(word[j], morphVO);
					sentenceMorphMap.add(map);
				}
			}
			//morphemeList.addAll(wordAnalyze(word[i]));
			
			// 한칸씩 띄어져 있는 단어들을 분석하여 복합명사일 경우 합친다
			morphemeMap.addAll(sentenceAnalyze(sentenceMorphMap));
		}
		
		return morphemeMap;
	}
	
	
	/**
	 * 명사만 추출하는 메소드
	 */
	public List<String> extractNoun(String document) {
		List<String> rtnList = new ArrayList<String> ();
		
		if(document == null || document.trim().length() == 0) return rtnList;
		
		//LOGGER.debug("document : " + document);
		List<HashMap<String, MorphemeVO>> sentenceMorphMap = null;
		
		document = document.trim().replaceAll("\n", ".").replaceAll("\r", ".");
		String[] sentences = document.split("\\.");
		
		String sentence = "";
		
		// 문장별로 형태소 분석 수행
		for(int i = 0; i < sentences.length; i++) {
			sentence = sentences[i].toLowerCase().trim();
			if(sentence.length() == 0) continue;
			sentenceMorphMap = new ArrayList<HashMap<String, MorphemeVO>> ();
			//LOGGER.debug(i+ "=>" + sentence[i].trim());
			
			// 문장을 다시, 단어로 자른다
			String[] word = sentence.trim().split(" ");
			for(int j = 0; j < word.length; j++) {
				if(word[j].trim().length() > 0 && word[j].trim().length() < 20) {
					HashMap<String, MorphemeVO> map = new HashMap<String, MorphemeVO> ();
					MorphemeVO morphVO = new MorphemeVO ();
					morphVO.setWord(word[j].trim());
					ANALYZE.wordAnalyze(word[j].trim(), morphVO, false);
					
					map.put(word[j], morphVO);
					sentenceMorphMap.add(map);
				}
			}
			
			// 한칸씩 띄어져 있는 단어들을 분석하여 복합명사일 경우 합친다
			sentenceMorphMap = sentenceAnalyze(sentenceMorphMap);
			for(HashMap<String, MorphemeVO> map : sentenceMorphMap) {				
				for(String key : map.keySet()) {
					MorphemeVO morphVO = map.get(key);
					
					if(morphVO.getTag() != null && morphVO.getTag().size() > 0) {
						List<String> tagList = morphVO.getTag();
						List<String> tokenList = morphVO.getToken();
						
						for(int k = 0; k < tagList.size(); k++) {
							if(tagList.get(k).equals("NN") 
									|| tagList.get(k).equals("CN") 
									|| tagList.get(k).equals("USER")) {
								rtnList.add(tokenList.get(k));
							}
						}
					}
				}
			}
			//morphemeMap.addAll(sentenceAnalyze(sentenceMorphMap));
		}
		
		return rtnList;
	}
	
	
	/**
	 * 원하는 품사만 뽑는다
	 * 
	 * @param document
	 * @param manualMap
	 * @return
	 */
	public List<String> extractManual(String document, HashMap<String, String> manualMap) {
		List<String> rtnList = new ArrayList<String> ();
		
		if(document == null || document.trim().length() == 0) return rtnList;
		
		//LOGGER.debug("document : " + document);
		List<HashMap<String, MorphemeVO>> sentenceMorphMap = null;
		
		document = document.trim().replaceAll("\n", ".").replaceAll("\r", ".");
		String[] sentences = document.split("\\.");
		
		String sentence = "";
		
		// 문장별로 형태소 분석 수행
		for(int i = 0; i < sentences.length; i++) {
			sentence = sentences[i].toLowerCase().trim();
			if(sentence.length() == 0) continue;
			sentenceMorphMap = new ArrayList<HashMap<String, MorphemeVO>> ();
			//LOGGER.debug(i+ "=>" + sentence[i].trim());
			
			// 문장을 다시, 단어로 자른다
			String[] word = sentence.trim().split(" ");
			for(int j = 0; j < word.length; j++) {
				if(word[j].trim().length() > 0 && word[j].trim().length() < 20) {
					HashMap<String, MorphemeVO> map = new HashMap<String, MorphemeVO> ();
					MorphemeVO morphVO = new MorphemeVO ();
					morphVO.setWord(word[j].trim());
					ANALYZE.wordAnalyze(word[j].trim(), morphVO, false);
					
					map.put(word[j], morphVO);
					sentenceMorphMap.add(map);
				}
			}
			
			// 한칸씩 띄어져 있는 단어들을 분석하여 복합명사일 경우 합친다
			sentenceMorphMap = sentenceAnalyze(sentenceMorphMap);
			for(HashMap<String, MorphemeVO> map : sentenceMorphMap) {
				
				for(String key : map.keySet()) {
					MorphemeVO morphVO = map.get(key);
					
					if(morphVO.getTag() != null && morphVO.getTag().size() > 0) {
						List<String> tagList = morphVO.getTag();
						List<String> tokenList = morphVO.getToken();
						
						for(int k = 0; k < tagList.size(); k++) {
							if(tagList.get(k).equals("NN") || tagList.get(k).equals("CN") || tagList.get(k).equals("USER")) {
								rtnList.add(tokenList.get(k));
							}
						}
					}
				}
			}
			//morphemeMap.addAll(sentenceAnalyze(sentenceMorphMap));
		}
		
		return rtnList;
	}
	
	
	/**
	 * 조사, 어미를 제거하고 전체를 추출하는 메소드
	 */
	public List<String> extractAll(String document) {
		if(document == null || document.trim().length() == 0) return null;
		
		List<String> rtnList = new ArrayList<String> ();
		document = document.trim().replaceAll("\n", ".").replaceAll("\r", ".");
		String[] sentences = document.split("\\.");
		
		//System.out.println(sentences.length + " sentences.");
		String sentence = "";
		// 문장별로 형태소 분석 수행
		for(int i = 0; i < sentences.length; i++) {
			//sentence = stringReplace(sentences[i].toLowerCase().trim());
			sentence = sentences[i].toLowerCase().trim();
			if(sentence.length() == 0) continue;
			//LOGGER.debug(i+ "=>" + sentence[i].trim());
			
			// 문장을 다시, 단어로 자른다
			String[] word = sentence.trim().split(" ");
			for(int j = 0; j < word.length; j++) {
				if(word[j].trim().length() > 0) {
					MorphemeVO morphVO = new MorphemeVO ();
					morphVO.setWord(word[j].trim());
					ANALYZE.wordAnalyze(word[j].trim(), morphVO, false);
					
					if(morphVO.getTag() != null && morphVO.getTag().size() > 0) {
						List<String> tagList = morphVO.getTag();
						List<String> tokenList = morphVO.getToken();
						
						for(int k = 0; k < tagList.size(); k++) {
							if(!tagList.get(k).equals("JS") && !tagList.get(k).equals("EM") && !tagList.get(k).equals("NB")) {
								rtnList.add(tokenList.get(k));
							}
						}
					}
				}
			}
		}
		
		return rtnList;
	}
	
	
	/**
	 * 조사, 어미를 제거하고 전체를 추출하는 메소드
	 */
	/*public List<String> extractTag(String document, String tags) {
		if(document == null || document.trim().length() == 0) return null;
		
		
		String[] tag = tags.split(",");
		HashMap<String, String> tagMap = new HashMap<String, String> ();
		for(int i = 0; i < tag.length; i++) {
			tagMap.put(tag[i], "");
		}
		
		List<String> rtnList = new ArrayList<String> ();
		
		//LOGGER.debug("document : " + document);
		LinkedHashMap<String, MorphemeVO> morphemeMap = new LinkedHashMap<String, MorphemeVO> ();
		
		document = document.trim().replaceAll("\n", ".").replaceAll("\r", ".");
		String[] sentences = document.split("\\.");
		
		LOGGER.debug(sentences.length + " sentences.");
		String sentence = "";
		
		// 문장별로 형태소 분석 수행
		for(int i = 0; i < sentences.length; i++) {
			sentence = stringReplace(sentences[i].toLowerCase().trim());
			if(sentence.length() == 0) continue;
			
			// 문장을 다시, 단어로 자른다
			String[] word = sentence.trim().split(" ");
			for(int j = 0; j < word.length; j++) {
				if(word[j].trim().length() > 0) {
					MorphemeVO morphVO = new MorphemeVO ();
					morphVO.setWord(word[j].trim());
					wordAnalyze(word[j].trim(), morphVO);
					
					if(morphVO.getTag() != null && morphVO.getTag().size() > 0) {
						List<String> tagList = morphVO.getTag();
						List<String> tokenList = morphVO.getToken();
						
						for(int k = 0; k < tagList.size(); k++) {
							if(tagMap.containsKey(tagList.get(k))) {
								rtnList.add(tokenList.get(k));
							}
						}
					}
				}
			}
		}
		
		return rtnList;
	}*/
	
	
	/**
	 * 문장의 의미를 분석하여, 형태소를 재조립한다
	 * 
	 * @param sentenceMorphMap
	 * @return
	 */
	public List<HashMap<String, MorphemeVO>> 
				sentenceAnalyze(List<HashMap<String, MorphemeVO>> sentenceMorphMap) {
		
		List<HashMap<String, MorphemeVO>> rtnList = new ArrayList<HashMap<String, MorphemeVO>>();
		
		// 1차. 복합명사로 변경. 2018.06.26
		MorphemeVO beforeMorphVO = null;
		HashMap<String, MorphemeVO> tempMap = null;
		
		for(HashMap<String, MorphemeVO> wordMap : sentenceMorphMap) {
			for(String word : wordMap.keySet()) {
				MorphemeVO morphVO = wordMap.get(word);
				
				// 바로 전 단어가, 명사로 끝날 경우
				if(beforeMorphVO != null && beforeMorphVO.getWord().length() > 0 &&
						(beforeMorphVO.getTag().get(beforeMorphVO.getTag().size()-1).equals("NN") 
						|| beforeMorphVO.getTag().get(beforeMorphVO.getTag().size()-1).equals("CN"))) {
					
					// 첫번째 단어가 명사나 복합명사일 경우 합쳐본다
					if(morphVO.getTag().get(0).equals("NN") 
							|| morphVO.getTag().get(0).equals("CN")){
						
						// 합쳐졌을 때, 명사사전에 있는 경우...
						if(nnDictionary.containsKey(beforeMorphVO.getWord() + morphVO.getToken().get(0))) {
							
							// word map을 합친다
							beforeMorphVO.setWord(beforeMorphVO.getWord() + morphVO.getWord());
							ArrayList<String> tokenList = beforeMorphVO.getToken();
							ArrayList<String> tagList = beforeMorphVO.getTag();
							
							String token = tokenList.get(tokenList.size()-1) + morphVO.getToken().get(0);
							
							tokenList.set(tokenList.size()-1, token);
							tagList.set(tokenList.size()-1, "CN");
							
							for(int i = 0; i < morphVO.getToken().size(); i++) {
								if(i > 0) {
									tokenList.add(morphVO.getToken().get(i));
									tagList.add(morphVO.getTag().get(i));
								}
							}
							
							beforeMorphVO.setToken(tokenList);
							beforeMorphVO.setTag(tagList);
							
							continue;
						}						
					}
				}	
				// before를 세팅
				if(beforeMorphVO != null) {
					tempMap = new HashMap<String, MorphemeVO> ();
					tempMap.put(beforeMorphVO.getWord(), beforeMorphVO);
					rtnList.add(tempMap);
				}
				
				beforeMorphVO = wordMap.get(word);
			}
		}
		
		// 한문장이 끝날 경우
		if(beforeMorphVO != null) {
			tempMap = new HashMap<String, MorphemeVO> ();
			tempMap.put(beforeMorphVO.getWord(), beforeMorphVO);
			rtnList.add(tempMap);
		}
		
		return rtnList;
	}
}
