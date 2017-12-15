package decode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class Decoder {
	static String url = "input/resources.arsc";
	public final static short RES_NULL_TYPE = 0x0000, RES_STRING_POOL_TYPE = 0x0001, RES_TABLE_TYPE = 0x0002,
			RES_XML_TYPE = 0x0003,

			// Chunk types in RES_XML_TYPE
			RES_XML_FIRST_CHUNK_TYPE = 0x0100, RES_XML_START_NAMESPACE_TYPE = 0x0100,
			RES_XML_END_NAMESPACE_TYPE = 0x0101, RES_XML_START_ELEMENT_TYPE = 0x0102, RES_XML_END_ELEMENT_TYPE = 0x0103,
			RES_XML_CDATA_TYPE = 0x0104, RES_XML_LAST_CHUNK_TYPE = 0x017f,
			// This contains a uint32_t array mapping strings in the string
			// pool back to resource identifiers. It is optional.
			RES_XML_RESOURCE_MAP_TYPE = 0x0180,

			// Chunk types in RES_TABLE_TYPE
			RES_TABLE_PACKAGE_TYPE = 0x0200, RES_TABLE_TYPE_TYPE = 0x0201, RES_TABLE_TYPE_SPEC_TYPE = 0x0202;

	static int currentChunkStartIndex;
	private static StringBuffer sb;

	public static void main(String[] arc) throws Exception {
		File arsc = new File(url);
		FileInputStream fis = new FileInputStream(arsc);
		sb = new StringBuffer();
		while (fis.available() > 0) {
			String row = "00" + Integer.toHexString(fis.read());
			sb.append(row.substring(row.length() - 2));
		}
		fis.close();
		while (currentChunkStartIndex < sb.length() / 2) {
			handleNextChunk();
		}
	}

	private static void handleNextChunk() throws UnsupportedEncodingException {
		int head = getNumber(sb.substring(get(0), get(2)));
		System.out.println("head->" + head);
		switch (head) {
		case RES_TABLE_TYPE:
			handleTable();
			break;
		case RES_STRING_POOL_TYPE:
			handleString();
			break;
		case RES_TABLE_PACKAGE_TYPE:
			handlePackages();
			break;
		case RES_TABLE_TYPE_SPEC_TYPE:
			handleSpecType();
			break;
		case RES_NULL_TYPE:
			currentChunkStartIndex++;
		}
	}

	private static void handleSpecType() {
		readByte();
		System.out.println("?->" + readByte());
		System.out.println("?->" + readInt());
		System.out.println("?->" + read0());
		System.out.println("?->" + read3());
		System.out.println("?->" + readInt());
	}


	private static void handlePackages() {
		int index = currentChunkStartIndex;
		
		readByte();
		int chunk = readByte();
		System.out.println("chunk->" + chunk);
		int chunkSize = readInt();
		System.out.println("chunkSize->" + chunkSize);
		int packageId = readInt();
		System.out.println("packageId->" + packageId);
		String packageName = decodePackageName(read256());
		System.out.println("packageName->" + packageName);
		
		int stringPoolOffset = readInt();
		System.out.println("stringPoolOffset->" + stringPoolOffset);
		int lastPublicKey = readInt();
		System.out.println("lastPublicKey->" + lastPublicKey);
		
		int keywordsPoolOffset = readInt();
		System.out.println("keywordsPoolOffset->" + keywordsPoolOffset);
		
		int keywordsPulicKey = readInt();
		System.out.println("keywordsPulicKey->" + keywordsPulicKey);
		
		readInt();
		
		int type = readByte();
		System.out.println("type->" + type);
		
		int headerSize = readByte();
		System.out.println("headerSize->" + headerSize);
		
		int size = readInt();
		System.out.println("size->" + size);
		
		int stringCount = readInt();
		System.out.println("strings->" + stringCount);
		
		int styleCount = readInt();
		System.out.println("styles->" + styleCount);
		
		int flag = readInt();
		System.out.println("flag->" + flag);
		
		int stringStart = readInt();
		System.out.println("stringStart->" + stringStart);
		
		int styleStart = readInt();
		System.out.println("styleStart->" + styleStart);
		
		int[] stringOffsets = new int[stringCount];
		for(int i=0;i<stringCount;i++) {
			stringOffsets[i] =  readInt();
			System.out.println("string offset->" + stringOffsets[i]);
		}
		
		int[] styleOffsets = new int[styleCount];
		for(int i=0;i<styleCount;i++) {
			styleOffsets[i] =  readInt();
			System.out.println("style offset->" + styleOffsets[i]);
		}
		
		String[] strings = new String[stringCount];
		int end = 0;
		for(int i=0;i<stringCount-1;i++) {
			int $index = get(stringOffsets[i]);
			if(i == stringCount -1) {
				end = get(stringStart);
			}else {
				end = get(stringOffsets[i+1]); 
			}
			String substring = sb.substring($index,end);
			String string = decodeString2(substring);
			System.out.println("string->" +string);
			strings[i] = string;
		}
		
		String[] styles = new String[styleCount];
		for(int i=0;i<styleCount;i++) {
			int $index = get(styleOffsets[i]);
			if(i == stringCount -1) {
				end = stringStart;
			}else {
				end = get(styleOffsets[i+1]); 
			}
			String substring = sb.substring($index,end);
			String string = decodeString2(substring);
			System.out.println("string->" +string);
			styles[i] = string;
		}
		currentChunkStartIndex = end / 2;
	}
	
	private static String decodeString2(String string) {
		StringBuffer sb = new StringBuffer();
		for(int i=2;i<string.length()/2;i++) {
			String substring = string.substring(i*2, i*2+2);
			sb.append((char)Integer.parseInt(substring,16));
		}
		return sb.toString();
	}

	private static int readByte() {
		int value =getNumber(sb.substring(get(0), get(2)));
		currentChunkStartIndex += 2;
		return value;
	}
	
	private static int readInt() {
		int value =getNumber(sb.substring(get(0), get(4)));
		currentChunkStartIndex += 4;
		return value;
	}
	
	private static int read0() {
		int value =getNumber(sb.substring(get(0), get(1)));
		currentChunkStartIndex += 1;
		return value;
	}
	

	private static int read3() {
		int value =getNumber(sb.substring(get(0), get(3)));
		currentChunkStartIndex += 3;
		return value;
	}
	
	private static String read256() {
		String value =sb.substring(get(0), get(256));
		currentChunkStartIndex += 256;
		return value;
	}

	private static String decodePackageName(String string) {
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<string.length()/4;i++) {
			String substring = string.substring(i*4, i*4+2);
			if("00".equals(substring)) {
				break;
			}
			sb.append((char)Integer.parseInt(substring,16));
		}
		return sb.toString();
	}

	private static void handleString() throws UnsupportedEncodingException {
		int chunk = getNumber(sb.substring(get(2), get(4)));
		System.out.println("chunk->" + chunk);
		int chunkSize = getNumber(sb.substring(get(4), get(8)));
		System.out.println("chunkSize->" + chunkSize);
		int stringCount = getNumber(sb.substring(get(8), get(0x0c)));
		System.out.println("stringCount->" + stringCount);
		int styleCount = getNumber(sb.substring(get(0x0c), get(0x10)));
		System.out.println("styleCount->" + styleCount);
		int coding = getNumber(sb.substring(get(0x10), get(0x14)));
		System.out.println("coding->" + coding);
		
		int offset = getNumber(sb.substring(get(0x14), get(0x17)));
		int stringStart = offset/2 + currentChunkStartIndex;
		System.out.println("stringStart->" + stringStart);
		int styleStart = getNumber(sb.substring(get(0x18), get(0x1b)));
		if(styleStart != 0) {
			styleStart =  styleStart/2 + currentChunkStartIndex;	
		}
		System.out.println("styleStart->" + styleStart);
		
		currentChunkStartIndex += chunk;
		
		
		
		int[] stringOffsets = new int[stringCount];
		for(int i=0;i<stringCount;i++) {
			int offsetString = getNumber(sb.substring(get(i*4),get((i+1)*4)));
			stringOffsets[i] = offsetString;
		}
		
		currentChunkStartIndex += stringCount*4;
		
		int[] styleOffsets = new int[styleCount];
		for(int i=0;i<styleCount;i++) {
			int offsetStyle = getNumber(sb.substring(get(i*4),get((i+1)*4)));
			System.out.println("style offset ->" + offsetStyle);
			styleOffsets[i] = offsetStyle;
		}
		currentChunkStartIndex += styleCount*4;
		
		String[] strings = new String[stringCount];
		int end =0;
		for(int i=0;i<stringCount;i++) {
			end = sb.indexOf("00",get(stringOffsets[i]));
			String substring = sb.substring(get(stringOffsets[i]),end);
			String string = decodeString(substring);
			System.out.println("string->" +get(stringOffsets[i])+","+substring.substring(0,4)+","+ new String(string.getBytes(),"utf-8"));
			strings[i] = string;
			System.out.println(substring);
		}
		
		String[] styles = new String[styleCount];
		for(int i=0;i<styleCount-1;i++) {
			String substring = sb.substring(get(styleOffsets[i]),get(styleOffsets[i+1]));
			String string = decodeString(substring);
			System.out.println("string->" +substring.substring(0,4)+","+ new String(string.getBytes(),"utf-8"));
			strings[i] = string;
		}
		
		currentChunkStartIndex = end/2+1;
	}
	
	
	 private static String decodeString(String str) {
			StringBuffer sb = new StringBuffer();
			for(int i=2;i<str.length()/2;i++) {
				String substring = str.substring(i*2, (i+1)*2);
				sb.append((char)Integer.parseInt(substring,16));
			}
			return sb.toString();
	    }

	private static void handleTable() {
		int chunk = getNumber(sb.substring(get(2), get(4)));
		System.out.println("chunk->" + chunk);
		long chunkSize = getNumber(sb.substring(get(4), get(7)));
		System.out.println("chunkSize->" + chunkSize);
		long packageCount = getNumber(sb.substring(get(8), get(chunk)));
		System.out.println("packageCount->" + packageCount);
		currentChunkStartIndex += chunk;
	}

	private static int get(int location) {
		return (currentChunkStartIndex + location) * 2;
	}

	private static int parseInt(String input) {
		String value = "";
		for (int i = 0; i < input.length() ; i++) {
			value = input.substring(i ,i+1) + value;
		}
		return Integer.parseInt(value, 16);
	}
	
	public static int toInt(byte[] data) {
        ByteBuffer bb = ByteBuffer.wrap(data);
        bb.order(ByteOrder.nativeOrder());
        return bb.getInt();
    }

	private static int getNumber(String input) {
		String value = "";
		for (int i = 0; i < input.length() / 2; i++) {
			value = input.substring(i * 2, (i + 1) * 2) + value;
		}
		return Integer.parseInt(value, 16);
	}
}

