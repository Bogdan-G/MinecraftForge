package org.io;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Gzip {
	
	public static byte[] compress(String data) {
		if (data == null || data.length() == 0) return new byte[0];
		ByteArrayOutputStream bos = null;
		GZIPOutputStream gzip = null;
		byte[] compressed = new byte[0];
		try {
			bos = new ByteArrayOutputStream(data.length());
			gzip = new GZIPOutputStream(bos);
			gzip.write(data.getBytes());
		} catch (IOException e) {
		} finally {
			try{if (gzip!=null) gzip.close();}catch (IOException e){}
			compressed = bos.toByteArray();
			try{if (bos!=null) bos.close();}catch (IOException e){}
		}
		return compressed;
	}
	
	public static String decompress(byte[] compressed) {
		if (compressed == null || compressed.length == 0) return "";
		ByteArrayInputStream bis = null;
		GZIPInputStream gis = null;
		BufferedReader br = null;
		StringBuilder sb = null;
		try {
			bis = new ByteArrayInputStream(compressed);
			gis = new GZIPInputStream(bis);
			br = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
			sb = new StringBuilder();
			String line;
			while((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (UnsupportedEncodingException e) {
		} catch (IOException e) {
		} finally {
			try{if (br!=null) br.close();}catch (IOException e){}
			try{if (gis!=null) gis.close();}catch (IOException e){}
			try{if (bis!=null) bis.close();}catch (IOException e){}
		}
		return String.valueOf(sb);
	}
}