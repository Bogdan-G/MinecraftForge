package org.io;

/*import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;*/

import net.jpountz.lz4.*;
import java.io.*;

public class Gzip {
	
	private static final int MAX_BLOCK_SIZE = 32 * 2;//old:32 * 1024 * 1024;
	private static final LZ4Compressor compressor = LZ4Factory.nativeInstance().fastCompressor();
	private static final LZ4FastDecompressor decompressor = LZ4Factory.nativeInstance().fastDecompressor();
	
	public static byte[] compress(String data) {
		if (data == null || data.length() == 0) return new byte[0];
		return LZ4compress(data);
		/*ByteArrayOutputStream bos = null;
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
		return compressed;*/
	}
	
	public static String decompress(byte[] compressed) {
		if (compressed == null || compressed.length == 0) return "";
		return LZ4Uncompress(compressed);
		/*ByteArrayInputStream bis = null;
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
		return String.valueOf(sb);*/
	}

	public static byte[] LZ4compress(String data) {
    		byte[] compressed = new byte[0];
    		try {
        		ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length());
        		LZ4BlockOutputStream out = new LZ4BlockOutputStream( bos, MAX_BLOCK_SIZE, compressor );
        		out.write(data.getBytes("UTF-8"));
        		out.close();
        		compressed = bos.toByteArray();
        		bos.close();
    		} catch (IOException e) {}
    		return compressed;
	}

	public static String LZ4Uncompress(byte[] compressed) {
    		StringBuilder sb = null;
    		try {
        		ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
        		LZ4BlockInputStream in = new LZ4BlockInputStream( bis, decompressor );
        		BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        		sb = new StringBuilder();
        		String line = new String((new byte[0]), "UTF-8");
        		while((line = br.readLine()) != null){
            			sb.append(line);
        		}
        		br.close();
        		in.close();
        		bis.close();
   	 	} catch (IOException e) {}
    		return String.valueOf(sb);
	}
}