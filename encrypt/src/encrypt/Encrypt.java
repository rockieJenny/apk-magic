package encrypt;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Encrypt {
	public static String ENCRYPTION_KEY = "lionmobikey$)!1";
	public static String ENCRYPTION_IV = "4e5Wa71fYoT7MFEX";

	public static void main(String[] args) throws IOException {
		String dat = args[0];
		String outputPath = args[1];

		byte[] data = readData(dat);
		String encrypt = encrypt(new String(data));
		FileOutputStream localFileOutputStream = new FileOutputStream(outputPath);
		localFileOutputStream.write(encrypt.getBytes());
		localFileOutputStream.flush();
		localFileOutputStream.close();
	}

	/**
	 * @return
	 * @throws IOException
	 */
	public static byte[] readData(String dataPath) throws IOException {
		ByteArrayOutputStream dexByteArrayOutputStream = new ByteArrayOutputStream();
		InputStream is = null;
		try {
			is = new FileInputStream(new File(dataPath));
			int ch = -1;
			while ((ch = is.read()) != -1) {
				dexByteArrayOutputStream.write(ch);
			}
		} finally {
			if (is != null) {
				is.close();
			}
		}
		return dexByteArrayOutputStream.toByteArray();
	}

	public static String encrypt(String src) {
		try {
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, makeKey(), makeIv());
			return Base64.encodeBytes(cipher.doFinal(src.getBytes()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String decrypt(String src) {
		try {
			byte[] decryptedStr = Base64.decode(src);
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, makeKey(), makeIv());
			byte[] originalByte = cipher.doFinal(decryptedStr);
			String originalStr = new String(originalByte);
			return originalStr;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	static Key makeKey() {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] key = md.digest(ENCRYPTION_KEY.getBytes("UTF-8"));
			return new SecretKeySpec(key, "AES");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	static AlgorithmParameterSpec makeIv() {
		try {
			return new IvParameterSpec(ENCRYPTION_IV.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
