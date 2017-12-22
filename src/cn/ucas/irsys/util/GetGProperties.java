package cn.ucas.irsys.util;

import java.io.InputStream;
import java.util.Properties;



public class GetGProperties {
	private static Properties gprops; // Global Props
	public static int  pageSize = 10;
	static {
		try {
			InputStream gin = GetGProperties.class.getClassLoader().getResourceAsStream("globalproperties.properties");

			Properties prop = new Properties();
			gprops = new Properties();
			gprops.load(gin);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Properties getGProperties() {
		return gprops;
	}
	
}
