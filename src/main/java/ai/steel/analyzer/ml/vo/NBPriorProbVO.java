package ai.steel.analyzer.ml.vo;

import java.util.Map;

/**
 * 사전 확률용 Value Object
 * 
 * @author Steel
 * @since 2020.12.14
 */
public class NBPriorProbVO {

	/** 클래스 사전 확률 */
	private Map<String, Double> clssPriorProb;

	/** 특징별->값별 사전 확률 */
	private Map<String, Map<String, Double>> featValPriorProb;
	
	/** 클래스별 카운트 */
	private Map<String, Integer> clssCnt;

	public Map<String, Double> getClssPriorProb() {
		return clssPriorProb;
	}

	public void setClssPriorProb(Map<String, Double> clssPriorProb) {
		this.clssPriorProb = clssPriorProb;
	}

	public Map<String, Map<String, Double>> getFeatValPriorProb() {
		return featValPriorProb;
	}

	public void setFeatValPriorProb(Map<String, Map<String, Double>> featValPriorProb) {
		this.featValPriorProb = featValPriorProb;
	}

	public Map<String, Integer> getClssCnt() {
		return clssCnt;
	}

	public void setClssCnt(Map<String, Integer> clssCnt) {
		this.clssCnt = clssCnt;
	}	
}