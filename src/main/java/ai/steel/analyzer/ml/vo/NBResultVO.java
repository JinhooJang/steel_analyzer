package ai.steel.analyzer.ml.vo;

import java.util.Map;

/**
 * 나이브 베이브 결과를 담는 Value Object
 * 
 * @author Steel
 * @since 2020.12.14
 */
public class NBResultVO {
	
	/** 클래스별 사후 통계값 */
	private Map<String, Double> postProbMap;
	
	/** 선택 분류값 */
	private String clss;
	
	/** 선택 분류값의 사후 확률 */
	private double prob;

	public Map<String, Double> getPostProbMap() {
		return postProbMap;
	}

	public void setPostProbMap(Map<String, Double> postProbMap) {
		this.postProbMap = postProbMap;
	}

	public String getClss() {
		return clss;
	}

	public void setClss(String clss) {
		this.clss = clss;
	}

	public double getProb() {
		return prob;
	}

	public void setProb(double prob) {
		this.prob = prob;
	}	
}


/**
 * 나이브 베이즈 클래스별 결과를 담는 Value Object
 */
class NaiveBayesVO {
	/** 클래스 */
	private String clss;
	/** 해당 클래스의 확률 */
	private double prob;
	
	public String getClss() {
		return clss;
	}
	public void setClss(String clss) {
		this.clss = clss;
	}
	public double getProb() {
		return prob;
	}
	public void setProb(double prob) {
		this.prob = prob;
	}
}