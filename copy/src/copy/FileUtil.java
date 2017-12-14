package copy;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by luowp on 2016/7/13.
 */
public class FileUtil {
	static String str = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n"
			+ "<LinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\r\n"
			+ "    xmlns:tools=\"http://schemas.android.com/tools\"\r\n" + "    android:gravity=\"center\"\r\n"
			+ "    android:layout_width=\"match_parent\"\r\n" + "    android:layout_height=\"match_parent\">\r\n"
			+ "\r\n" + "\r\n" + "</LinearLayout>";

	public static void copyp(File f1, File f2) {
		try {
			int length = 2097152;
			FileInputStream in = new FileInputStream(f1);
			FileOutputStream out = new FileOutputStream(f2);
			byte[] buffer = new byte[length];
			while (true) {
				int ins = in.read(buffer);
				if (ins == -1) {
					in.close();
					out.flush();
					out.close();
				} else
					out.write(buffer, 0, ins);
			}	
		}catch(Exception e) {
			
		}
	}

	public static void writeBlankLayout(File $f) throws Exception {
		FileWriter file = new FileWriter($f);
		try {
			file.write(str);
		} finally {
			file.flush();
			file.close();
		}

	}

}
