package copy;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class Copy {
	static String SRC = "C:\\workbench\\powerlight3";
	static String DST = "C:\\workbench\\Shell";
	static String RESOURCE = "\\app\\src\\main\\res";

	public static void main(String[] args) throws Exception {
		
		File input = new File(SRC + RESOURCE);
		copy(input);
	}

	private static void copy(File input) throws Exception {
		File[] list = input.listFiles();
		for (File file : list) {
			String substring = file.getAbsolutePath().substring((SRC).length());
			File $f = new File(DST + substring);
			if (file.isDirectory()) {
				if (!$f.exists()) {
					$f.mkdirs();
				}
				copy(file);
			} else {
				if(file.getParentFile().getName().equals("layout")) {
					FileUtil.writeBlankLayout($f);
				}else {
					FileUtil.copyp(file, $f);	
				}
			}
		}
		
	}
}
