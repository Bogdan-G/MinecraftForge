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
import java.util.*;
//import net.minecraft.client.Minecraft;

public class Gzip {
	
	private static final int MAX_BLOCK_SIZE = 32 * 2;//old:32 * 1024 * 1024;
	private static final LZ4Compressor compressor = LZ4Factory.nativeInstance().fastCompressor();
	private static final LZ4FastDecompressor decompressor = LZ4Factory.nativeInstance().fastDecompressor();
	private static final byte[] empty_byte_array = new byte[0];
	private static final BitSet empty_bitset = new BitSet(0);
	private static final String empty_string = "";
	private static final String path_cGzip = /*Minecraft.getMinecraft().mcDataDir.getAbsolutePath()*/"."+File.separator+"cache"+File.separator;
	private static byte[] GSNO;
	private static BitSet GSNO_bs;
	private static byte[] EMPTY;
	private static int[] two_pow = new int[] {1, 2, 4, 8, 16, 32, 64, 128};
	
	static {
	    	String data="GSNO";//GzipStringNullObject
    		try {
        		ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length());
        		LZ4BlockOutputStream out = new LZ4BlockOutputStream( bos, MAX_BLOCK_SIZE, compressor );
        		out.write(data.getBytes("UTF-8"));
        		out.close();
        		GSNO = bos.toByteArray();
        		bos.close();
        		byte[] data0 = data.getBytes("UTF-8");
        		BitSet bitset_data = new BitSet(data0.length*8);
        		for (int i=0;i<data0.length;i++) {
        		int k = data0[i] + 128;
        		for (int j=0;k!=0;j++) {
        		int k1 = k % 2;
        		k=(k-k1)/2;
        		k= k==1?0:k;
        		if (k1==1) bitset_data.set(i+j);
        		}
        		}
        		GSNO_bs=bitset_data;
    		} catch (IOException e) {}
	    	data="";
    		try {
        		ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length());
        		LZ4BlockOutputStream out = new LZ4BlockOutputStream( bos, MAX_BLOCK_SIZE, compressor );
        		out.write(data.getBytes("UTF-8"));
        		out.close();
        		EMPTY = bos.toByteArray();
        		bos.close();
    		} catch (IOException e) {}
    	}
	
	public static BitSet compress(String data) {
		//if (/*data == null || */data.length() == 0) return empty_byte_array;
		if (data == null) return null;
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

	public static byte[] compress(String data, String idcc, int nline, int size) {
		//if (data == null || data.length() == 0) return empty_byte_array;
		return LZ4compress(data, idcc, nline, size);
	}
	public static byte[] compress(String data, String idcc, int n00) {
		//if (data == null || data.length() == 0) return empty_byte_array;
		return LZ4compress(data, idcc, n00, (short)0);
	}
	public static byte[] compress(String data, String idcc, int n00, short state) {
		return LZ4compress(data, idcc, n00, state);
	}
	public static byte[] compress(String data, String idcc, boolean b00) {
		return LZ4compress(data, idcc, b00);
	}
	
	public static String decompress(BitSet compressed) {
		//if (/*compressed == null || */compressed.length == 0) return empty_string;
		if (compressed==null) return null;//null support
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

	public static String decompress(String idcc, int nline, int size) {
		//if (idcc == null || idcc.length() == 0) return empty_string;
		return LZ4Uncompress(idcc, nline, size);
	}
	public static String decompress(String idcc, int n00) {
		return LZ4Uncompress(idcc, n00, (short)0);
	}
	public static String decompress(String idcc, int n00, short state) {
		return LZ4Uncompress(idcc, n00, state);
	}
	public static String decompress(String idcc, boolean b00) {
		return LZ4Uncompress(idcc, b00);
	}

	public static BitSet LZ4compress(String data) {
    		//if (data.length()==0) return empty_byte_array;//EMPTY;
    		//byte[] compressed = new byte[0];//empty_byte_array;
    		if (data.length()==0) return empty_bitset;
    		BitSet bitset = empty_bitset;
    		try {
        		/*ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length());
        		LZ4BlockOutputStream out = new LZ4BlockOutputStream( bos, MAX_BLOCK_SIZE, compressor );
        		out.write(data.getBytes("UTF-8"));
        		out.close();
        		compressed = bos.toByteArray();
        		bos.close();*/
        		byte[] data0 = data.getBytes("UTF-8");
        		bitset = new BitSet(data0.length*8);
        		for (int i=0;i<data0.length;i++) {
        		int k = data0[i] + 128;
        		for (int j=0;k!=0;j++) {
        		int k1 = k % 2;
        		k=(k-k1)/2;
        		k= k==1?0:k;
        		if (k1==1) bitset.set(i+j);
        		}
        		}
    		} catch (IOException e) {cpw.mods.fml.common.FMLLog.log(org.apache.logging.log4j.Level.WARN, (Throwable)e, "Gzip stacktrace: %s", (Throwable)e);}
    		//return compressed;
    		return bitset;
	}

	public static byte[] LZ4compress(String data, String idcc, int nline, int size) {
    		byte[] compressed = empty_byte_array;
    		String filename = path_cGzip+idcc;
    		try {
        		//ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length());
        		//LZ4BlockOutputStream out = new LZ4BlockOutputStream( bos, MAX_BLOCK_SIZE, compressor );
        		//out.write(data.getBytes("UTF-8"));
        		//out.close();
        		//compressed = bos.toByteArray();
        		//compressed= data == null ? "null".getBytes("UTF-8") : data.getBytes("UTF-8");
        		//bos.close();
        		final File file = new File(filename);
        		boolean flag=false;
        		if (!file.exists()) {
        		file.createNewFile();
        		//String[] sb = new String[size];
        		ArrayList<String> sb = new ArrayList(size);
        		sb.add(data==null ? "" : data);
        		StringBuilder split = new StringBuilder();
        		for (int i=0;i<sb.size();i++) { /*if (sb.get(i)==null) { split.append("null").append("\n"); } else { */split.append(sb.get(i)).append("\n"); /*}*/ }
        		compressed = (String.valueOf(split)).getBytes("UTF-8");
        		OutputStream fos = new BufferedOutputStream(new FileOutputStream(filename));
        		fos.write(compressed);
        		fos.flush();
        		fos.close();
        		} else {
        		InputStream fis0 = new FileInputStream(file);
        		InputStream fis  = new BufferedInputStream(fis0);
        		byte[] compressed0 = new byte[(int)file.length()];
        		fis.read(compressed0);
        		ByteArrayInputStream bis = new ByteArrayInputStream(compressed0);
        		BufferedReader br = new BufferedReader(new InputStreamReader(bis, "UTF-8"));
        		//String[] sb = new String[size];
        		ArrayList<String> sb = new ArrayList(size);
        		String line = new String((empty_byte_array), "UTF-8");
        		while((line = br.readLine()) != null){
            			sb.add(line);
        		}
        		try { sb.set(nline, data==null ? "" : data); } catch (IndexOutOfBoundsException e) { sb.add(data==null ? "" : data); }
        		br.close();
        		bis.close();
        		fis0.close();
        		fis.close();
        		StringBuilder split = new StringBuilder();
        		for (int i=0;i<sb.size();i++) { /*if (sb.get(i)==null) { split.append("null").append("\n"); } else { */split.append(sb.get(i)).append("\n"); /*}*/ }
        		compressed = (String.valueOf(split)).getBytes("UTF-8");
        		OutputStream fos = new BufferedOutputStream(new FileOutputStream(filename));
        		fos.write(compressed);
        		fos.flush();
        		fos.close();
        		}
    		} catch (IOException e) {cpw.mods.fml.common.FMLLog.log(org.apache.logging.log4j.Level.WARN, (Throwable)e, "Gzip stacktrace: %s", (Throwable)e);}
    		return compressed;
	}
	public static byte[] LZ4compress(String data, String idcc, boolean b00) {
    		byte[] compressed = empty_byte_array;
    		String filename = path_cGzip+idcc;
    		try {
        		compressed= data == null ? "".getBytes("UTF-8") : data.getBytes("UTF-8");
        		final File file = new File(filename);
        		if (!file.exists()) file.createNewFile();
        		OutputStream fos = new BufferedOutputStream(new FileOutputStream(filename));
        		fos.write(compressed);
        		fos.flush();
        		fos.close();
    		} catch (IOException e) {cpw.mods.fml.common.FMLLog.log(org.apache.logging.log4j.Level.WARN, (Throwable)e, "Gzip stacktrace: %s", (Throwable)e);}
    		return compressed;
	}
	public static byte[] LZ4compress(String data, String idcc, int n00, short state) {
    		byte[] compressed = empty_byte_array;
    		String filename = path_cGzip+"ce"+n00;
    		try {
        		compressed= data == null ? GSNO : data.length()==0 ? new byte[0]/*EMPTY*/ : data.getBytes("UTF-8");
        		final File file;
        		if (state > (short)0) { 
        		filename=filename+"-s"+state;
        		file = new File(filename);
        		if (!file.exists()) file.createNewFile();
        		/*} else if (state > (short)950 && state < (short)1000) { 
        		filename=filename+"-s"+state+"-id"+idcc;
        		file = new File(filename);
        		if (!file.exists()) file.createNewFile();*/
        		} else {
        		file = new File(filename);
        		}
        		byte[] compressed0 = new byte[(int)file.length()];
        		try (InputStream fis  = new BufferedInputStream(new FileInputStream(file))) {
        		fis.read(compressed0);
        		}
        		try (ByteArrayInputStream bis = new ByteArrayInputStream(compressed0)) {
        		try (BufferedReader br = new BufferedReader(new InputStreamReader(bis, "UTF-8"))) {
        		ArrayList<String> sb = new ArrayList();
        		String line = new String((empty_byte_array), "UTF-8");
        		while((line = br.readLine()) != null){
            			sb.add(line);
        		}
        		boolean flag0 = false;
        		if (n00==1) {
        		for (int i=0;i<sb.size();i++) {
        			if (sb.get(i).equals(idcc)) {
        			int j=i+1;
        			if (sb.get(j).equals("[")) {
        				//ArrayList<String> parse = new ArrayList();
        				int k=j+1;
        				while (!sb.get(k).equals("]")) { /*parse.add(sb.get(k));*/k++; }
        				int k1=j+1;
        				sb.set(k1, data==null?"GSNO":data.equals("")?"E_S":data);
        				k1++;
        				sb.subList(k1, k).clear();
        				flag0=true;
        				break;
        			}}
        		}} else {
        		for (int i=0;i<sb.size();) {
        			if (sb.get(i).equals(idcc)) {
        			int j=i+1;
        			sb.set(j, data==null?"GSNO":data.equals("")?"E_S":data);
        			flag0=true;
        			break;
        			}
        			i=i+2;
        		}
        		}
        		if (!flag0) {
        		//sb.add(idcc);sb.add("[");sb.add(data);sb.add("]");
        		StringBuilder split = new StringBuilder(32);
        		if (n00==1) {
        		split.append(idcc).append("\n").append("[").append("\n").append(data).append("\n").append("]").append("\n");
        		} else {
        		split.append(idcc).append("\n").append(data).append("\n");
        		}
        		try (FileWriter writer = new FileWriter(filename, true)) {
        		try (BufferedWriter bufferWriter = new BufferedWriter(writer)) {
        		String temp = String.valueOf(split);
        		bufferWriter.write(temp);
        		bufferWriter.flush();
        		}}
        		} else {
        		StringBuilder split = new StringBuilder(sb.size()*2);
        		for (int i=0;i<sb.size();i++) { /*if (sb.get(i)==null) { split.append("null").append("\n"); } else { */split.append(sb.get(i)).append("\n"); /*}*/ }
        		byte[] compressed1 = (String.valueOf(split)).getBytes("UTF-8");
        		try (OutputStream fos = new BufferedOutputStream(new FileOutputStream(filename))) {
        		fos.write(compressed1);
        		fos.flush();
        		}
        		//fos0.finalize();
        		//fos.close();
        		//fos0.close();
        		}}}
        		//sb.clear();
        		//br.close();
        		//bis.close();
        		//fis.close();
    		} catch (IOException e) {cpw.mods.fml.common.FMLLog.log(org.apache.logging.log4j.Level.WARN, (Throwable)e, "Gzip stacktrace: %s", (Throwable)e);}
    		return compressed;
	}

	public static String LZ4Uncompress(BitSet compressed) {
    		//if (compressed==EMPTY || compressed==empty_byte_array || compressed.length==0) return empty_string;
    		if (compressed.size()==0 || compressed.equals(empty_bitset)) return empty_string;
    		//StringBuilder sb = null;
    		byte[] conv = new byte[compressed.size()/8];
    		try {
        		/*ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
        		LZ4BlockInputStream in = new LZ4BlockInputStream( bis, decompressor );
        		BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        		sb = new StringBuilder();
        		String line = new String((empty_byte_array), "UTF-8");
        		while((line = br.readLine()) != null){
            			sb.append(line);
        		}
        		br.close();
        		in.close();
        		bis.close();*/
        		int l = 0;
        		for (int i=0;i<compressed.size();i++) {
        		int number0 = 0;
        		for (int j=7;j>-1;j--) {
        		int k = 0;
        		if (compressed.get(i)) k=1;
        		number0 += k*two_pow[j];
        		i++;
        		}
        		conv[l]=(byte)(number0-128);
        		l++;
        		}
        		return new String(conv, "UTF-8");
        	} catch (Exception e) {
        	cpw.mods.fml.common.FMLLog.log(org.apache.logging.log4j.Level.WARN, (Throwable)e, "Gzip stacktrace: %s", (Throwable)e);
        	return new String(conv);}
	}

	public static String LZ4Uncompress(String idcc, int nline, int size) {
    		String str = "";
    		String filename = path_cGzip+idcc;
    		try {
        		final File file = new File(filename);
        		InputStream fis  = new BufferedInputStream(new FileInputStream(file), (int)file.length());
        		byte[] compressed = new byte[(int)file.length()];
        		fis.read(compressed);
        		ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
        		//LZ4BlockInputStream in = new LZ4BlockInputStream( bis, decompressor );
        		BufferedReader br = new BufferedReader(new InputStreamReader(bis, "UTF-8"));//new BufferedReader(new InputStreamReader(in, "UTF-8"));
        		//String[] sb = new String[size];
        		ArrayList<String> sb = new ArrayList(size);
        		String line = new String((empty_byte_array), "UTF-8");
        		while((line = br.readLine()) != null){
            			sb.add(line);
        		}
        		str= /*(sb.get(nline)==null ? null : (sb.get(nline).equals("null") ? null : */sb.get(nline)/*))*/;
        		br.close();
        		//in.close();
        		bis.close();
        		fis.close();
   	 	} catch (IOException e) {cpw.mods.fml.common.FMLLog.log(org.apache.logging.log4j.Level.WARN, (Throwable)e, "Gzip stacktrace: %s", (Throwable)e);}
    		return str;
	}
	public static String LZ4Uncompress(String idcc, boolean b00) {
    		StringBuilder sb = null;
    		String filename = path_cGzip+idcc;
    		try {
        		final File file = new File(filename);
        		InputStream fis0 = new FileInputStream(file);
        		InputStream fis  = new BufferedInputStream(fis0);
        		byte[] compressed = new byte[(int)file.length()];
        		fis.read(compressed);
        		ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
        		BufferedReader br = new BufferedReader(new InputStreamReader(bis, "UTF-8"));
        		sb = new StringBuilder();
        		String line = new String((empty_byte_array), "UTF-8");
        		while((line = br.readLine()) != null){
            			sb.append(line);
        		}
        		br.close();
        		bis.close();
        		fis0.close();
        		fis.close();
   	 	} catch (IOException e) {cpw.mods.fml.common.FMLLog.log(org.apache.logging.log4j.Level.WARN, (Throwable)e, "Gzip stacktrace: %s", (Throwable)e);}
    		return String.valueOf(sb);
	}
	public static String LZ4Uncompress(String idcc, int n00, short state) {
    		String sb1 = empty_string;
    		String filename = path_cGzip+"ce"+n00;
    		try {
        		final File file;
        		if (state > (short)0) { 
        		filename=filename+"-s"+state;
        		file = new File(filename);
        		if (!file.exists()) file.createNewFile();
        		/*} else if (state > (short)950 && state < (short)1000) { 
        		filename=filename+"-s"+state+"-id"+idcc;
        		file = new File(filename);
        		if (!file.exists()) file.createNewFile();*/
        		} else {
        		file = new File(filename);
        		}
        		byte[] compressed0 = new byte[(int)file.length()];
        		try (InputStream fis  = new BufferedInputStream(new FileInputStream(file))) {
        		fis.read(compressed0);}
        		try (ByteArrayInputStream bis = new ByteArrayInputStream(compressed0)) {
        		try (BufferedReader br = new BufferedReader(new InputStreamReader(bis, "UTF-8"))) {
        		ArrayList<String> sb = new ArrayList();
        		String line = new String((empty_byte_array), "UTF-8");
        		while((line = br.readLine()) != null){
            			sb.add(line);
        		}
        		if (n00==1) {
        		StringBuilder parse = new StringBuilder(32);
        		for (int i=0;i<sb.size();i++) {
        			if (sb.get(i).equals(idcc)) {
        			int j=i+1;int j1=i+3;
        			if (sb.get(j).equals("[") && !sb.get(j1).equals("]")) {
        				int k=j+1;
        				while (!sb.get(k).equals("]")) { parse.append(sb.get(k)).append("\n");k++; }
        				break;
        			} else { int j2=i+2;parse.append(sb.get(j2)); }
        			}
        		}
        		sb1=String.valueOf(parse);
        		} else {
        		for (int i=0;i<sb.size();) {
        			if (sb.get(i).equals(idcc)) {
        			int j=i+1;
        			sb1=sb.get(j);
        			break;
        			}
        			i=i+2;
        		}}}}
        		//br.close();
        		//bis.close();
        		//fis.close();
   	 	} catch (IOException e) {cpw.mods.fml.common.FMLLog.log(org.apache.logging.log4j.Level.WARN, (Throwable)e, "Gzip stacktrace: %s", (Throwable)e);}
    		return sb1.equals("GSNO") ? null : sb1.equals("E_S") ? new String("") : new String(sb1);//null & empty support
	}
}