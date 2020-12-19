package ai.steel.analyzer.cmmn;


/**
 * 공통으로 사용되는 메소드 모음
 * 
 * @author Steel
 * @since 2020.12.14
 */
public class CommonModule {
	
	
	/**
	 * 더블형을 받아온 후, 소수점을 자르고 리턴
	 * 
	 * @param value
	 * @param point
	 */
	public double formatDouble(double value, int point) {
		return Double.parseDouble(String.format("%." + point + "f", value));
	}
}
