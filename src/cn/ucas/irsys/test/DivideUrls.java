package cn.ucas.irsys.test;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

import org.junit.Test;


public class DivideUrls {
	private static BufferedInputStream bin;
	private static BufferedReader bufr;
	
	@Test
	public void divideUrls() {
		
		try {
			String totalUrls = "/home/lockjk/urls/urls";
			File file = new File(totalUrls);
			bin = new BufferedInputStream(new FileInputStream(file));
			bufr = new BufferedReader(new InputStreamReader(bin, "utf-8"),1024*1024);
			
			int count = 1;
			int fileNum = 1;
			
			File sFile = new File(file.getParent()+"/url"+fileNum);
			if(!sFile.exists()) {
				sFile.createNewFile();
			}
			FileWriter fw =  new FileWriter(sFile);
			
			while(bufr.ready()) {
				
				String line = bufr.readLine();
				fw.append(line+"\n");
				
				
				if(count >= 100000) {
					fw.flush();
					fw.close();
					break;
				}
				
				if(count % 10000 == 0) {
//					Thread.sleep(100);
					fw.flush();
					fw.close();
					fileNum++;
					sFile = new File(file.getParent()+ "/url" + fileNum);
					sFile.createNewFile();
					fw =  new FileWriter(sFile);
					
				}
				count++;
			}
			bufr.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	
	
	
	
}
