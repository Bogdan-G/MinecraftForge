package cpw.mods.fml.common.asm.transformers;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;
import cpw.mods.fml.relauncher.FMLRelaunchLog;

import cpw.mods.fml.relauncher.FMLLaunchHandler;
import java.io.*;
import java.util.*;

public class ModAccessTransformer extends AccessTransformer {
    public static Map<String, String> embedded = FMLLaunchHandler.SerializableObjects ? null : new HashMap(); //Needs to be primitive so that both classloaders get the same class.
    private static final boolean logDebugInfo = Boolean.parseBoolean(System.getProperty("fml.debugAccessTransformer", "false"));
    static {
    if (FMLLaunchHandler.SerializableObjects) {
         try {
        FileInputStream inputStream = new FileInputStream("."+File.separator+"cache2"+File.separator+"fml.embedded.ser");
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        embedded = (Map) objectInputStream.readObject();
        objectInputStream.close();
        inputStream.close();
        } catch (Exception e) {cpw.mods.fml.common.FMLLog.log(org.apache.logging.log4j.Level.WARN, (Throwable)e, "FML stacktrace: %s", (Throwable)e);}
    }
    }
    
    @SuppressWarnings("unchecked")
	public ModAccessTransformer() throws Exception
    {
        super(ModAccessTransformer.class);
        if (!FMLLaunchHandler.SerializableObjects) {
        //We are in the new ClassLoader here, so we need to get the static field from the other ClassLoader.
        ClassLoader classLoader = this.getClass().getClassLoader().getClass().getClassLoader(); //Bit odd but it gets the class loader that loaded our current class loader yay java!
        Class<?> otherClazz = Class.forName(this.getClass().getName(), true, classLoader);
        Field otherField = otherClazz.getDeclaredField("embedded");
        otherField.setAccessible(true);
        embedded = (Map<String, String>)otherField.get(null);}

        for (Map.Entry<String, String> e : embedded.entrySet())
        {
            int old_count = getModifiers().size();
            processATFile(CharSource.wrap(e.getValue()));
            int added = getModifiers().size() - old_count;
            if (logDebugInfo && added > 0)
            {
                FMLRelaunchLog.fine("Loaded %d rules from AccessTransformer mod jar file %s\n", added, e.getKey());
            }
        }
    }

    public static void addJar(JarFile jar) throws IOException
    {
        if (!FMLLaunchHandler.SerializableObjects) {
        Manifest manifest = jar.getManifest();
        String atList = manifest.getMainAttributes().getValue("FMLAT");
        if (atList == null) return;
        for (String at : atList.split(" "))
        {
            JarEntry jarEntry = jar.getJarEntry("META-INF/"+at);
            if (jarEntry != null)
            {
                embedded.put(String.format("%s!META-INF/%s", jar.getName(), at),
                        new JarByteSource(jar,jarEntry).asCharSource(Charsets.UTF_8).read());
            }
        }}
    }

    private static class JarByteSource extends ByteSource
    {
        private JarFile jar;
        private JarEntry entry;
        public JarByteSource(JarFile jar, JarEntry entry)
        {
            this.jar = jar;
            this.entry = entry;
        }
        @Override
        public InputStream openStream() throws IOException
        {
            return jar.getInputStream(entry);
        }
    }
}
