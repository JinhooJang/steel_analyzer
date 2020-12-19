package ai.steel.analyzer.cmmn;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 파일 처리에 관련된 모듈
 * 
 * @author Steel
 * @since 2020.12.14
 */
public class FileModule {
	// new line
	final String NEWLINE = System.getProperty("line.separator");

	
	/**
	 * 리스트 맵, 형태의 파일을 csv로 생성
	 * 
	 * @param map
	 * @param fullPath
	 * @return
	 */
	public boolean makeListMapToCsv(List<Map<String, Object>> map, String fullPath) {
		
		return true;
	}
	
	
	/**
	 * CSV 파일을 읽은 후, LisMap 형태로 변환
	 * 
	 * @param fullPath
	 * @param useLower
	 * @param sepa
	 * @return
	 */
	public List<Map<String, String>> readCsvToListMap(
			String fullPath, boolean useLower, String sepa) {
		
		List<Map<String, String>> rtnList = new ArrayList<Map<String, String>> ();
		BufferedReader br = null;
		try {
			br = new BufferedReader(
						new InputStreamReader(
							new FileInputStream(fullPath), "UTF8"));
			
			String line = "";
			int lineCnt = 0;
			String[] cols = null;
			
			while((line = br.readLine()) != null) {
				// header
				if(lineCnt == 0) {
					cols = line.trim().split(sepa);
				} else {
					Map<String, String> map = new LinkedHashMap<String, String> ();
					if(line.trim().length() > 0) {
						String[] values = line.trim().split(sepa);
						for(int i = 0; i < cols.length; i++) {
							String val = (useLower) ? values[i].toLowerCase() : values[i];
							map.put(cols[i], val);							
						}
						rtnList.add(map);
					}
				}
				lineCnt++;
			}				
		} catch (Exception e) {
			System.out.println("readCsvToListMap error : " + e.getMessage());
			return null;
		} finally {
			if(br != null) {
				try {
					br.close();
				} catch (Exception eo) {}
			}
		}
		
		return rtnList;
	}
}
