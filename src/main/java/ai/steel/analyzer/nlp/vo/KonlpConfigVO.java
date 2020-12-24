package ai.steel.analyzer.nlp.vo;


/**
 * 설정값을 읽기 위한 Value Object
 * 
 * @author jinhoo.jang
 * @since 2020.04.21
 */
public class KonlpConfigVO {
	
	/** 사전 경로 */
	private String dicPath;
	/** NER 리스트 */
	private String[] nerList;
	/** 사용자 사전 사용 여부 */
	private boolean userYn;
	
	
	public String getDicPath() {
		return dicPath;
	}
	public void setDicPath(String dicPath) {
		this.dicPath = dicPath;
	}	
	public String[] getNerList() {
		return nerList;
	}
	public void setNerList(String[] nerList) {
		this.nerList = nerList;
	}
	public boolean isUserYn() {
		return userYn;
	}
	public void setUserYn(boolean userYn) {
		this.userYn = userYn;
	}
}
