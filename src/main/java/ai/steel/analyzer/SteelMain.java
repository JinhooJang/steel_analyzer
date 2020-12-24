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
		Test test = new Test();
		
		//test.nbTextClssfier();
		//nlpTest();
		test.koNlpTest();
	}	
}
