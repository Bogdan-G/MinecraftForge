/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
 */

package cpw.mods.fml.relauncher;

import java.io.File;
import org.apache.logging.log4j.Level;

import net.minecraft.launchwrapper.LaunchClassLoader;

import com.google.common.base.Throwables;

import cpw.mods.fml.common.launcher.FMLTweaker;

public class FMLLaunchHandler
{
    private static FMLLaunchHandler INSTANCE;
    static Side side;
    private LaunchClassLoader classLoader;
    private FMLTweaker tweaker;
    private File minecraftHome;

    public static void configureForClientLaunch(LaunchClassLoader loader, FMLTweaker tweaker)
    {
        instance(loader, tweaker).setupClient();
    }

    public static void configureForServerLaunch(LaunchClassLoader loader, FMLTweaker tweaker)
    {
        instance(loader, tweaker).setupServer();
    }

    private static FMLLaunchHandler instance(LaunchClassLoader launchLoader, FMLTweaker tweaker)
    {
        if (INSTANCE == null)
        {
            INSTANCE = new FMLLaunchHandler(launchLoader, tweaker);
        }
        return INSTANCE;

    }

    private FMLLaunchHandler(LaunchClassLoader launchLoader, FMLTweaker tweaker)
    {
        this.classLoader = launchLoader;
        this.tweaker = tweaker;
        this.minecraftHome = tweaker.getGameDir();
        this.classLoader.addClassLoaderExclusion("cpw.mods.fml.relauncher.");
        this.classLoader.addClassLoaderExclusion("net.minecraftforge.classloading.");
        this.classLoader.addTransformerExclusion("cpw.mods.fml.common.asm.transformers.deobf.");
        this.classLoader.addTransformerExclusion("cpw.mods.fml.common.patcher.");
    }

    private void setupClient()
    {
        FMLRelaunchLog.side = Side.CLIENT;
        side = Side.CLIENT;
        setupHome();
    }

    private void setupServer()
    {
        FMLRelaunchLog.side = Side.SERVER;
        side = Side.SERVER;
        setupHome();

    }

    private void setupHome()
    {
        FMLInjectionData.build(minecraftHome, classLoader);
        FMLRelaunchLog.minecraftHome = minecraftHome;
        File dir_cache = new File("."+File.separator+"cache");
        if (dir_cache.exists()) {
            File[] files_in_cache = dir_cache.listFiles();
            for(int i=0; i < files_in_cache.length; i++) { files_in_cache[i].delete(); }
            //dir_cache.delete();
        }
        dir_cache.mkdir();
        File cache = new File("."+File.separator+"cache"+File.separator+"ce");
        if (!cache.exists()) { try{cache.createNewFile();}catch(java.io.IOException e){} }
        cache = new File("."+File.separator+"cache"+File.separator+"ce0");
        if (!cache.exists()) { try{cache.createNewFile();}catch(java.io.IOException e){} }
        cache = new File("."+File.separator+"cache"+File.separator+"ce1");
        if (!cache.exists()) { try{cache.createNewFile();}catch(java.io.IOException e){} }
        cache = new File("."+File.separator+"cache"+File.separator+"ce2");
        if (!cache.exists()) { try{cache.createNewFile();}catch(java.io.IOException e){} }
        cache = new File("."+File.separator+"cache"+File.separator+"ce3");
        if (!cache.exists()) { try{cache.createNewFile();}catch(java.io.IOException e){} }
        cache = new File("."+File.separator+"cache"+File.separator+"ce4");
        if (!cache.exists()) { try{cache.createNewFile();}catch(java.io.IOException e){} }
        cache = new File("."+File.separator+"cache"+File.separator+"ce5");
        if (!cache.exists()) { try{cache.createNewFile();}catch(java.io.IOException e){} }
        cache = new File("."+File.separator+"cache"+File.separator+"ce6");
        if (!cache.exists()) { try{cache.createNewFile();}catch(java.io.IOException e){} }
        cache = new File("."+File.separator+"cache"+File.separator+"ce7");
        if (!cache.exists()) { try{cache.createNewFile();}catch(java.io.IOException e){} }
        cache = new File("."+File.separator+"cache"+File.separator+"ce8");
        if (!cache.exists()) { try{cache.createNewFile();}catch(java.io.IOException e){} }
        cache = new File("."+File.separator+"cache"+File.separator+"ce9");
        if (!cache.exists()) { try{cache.createNewFile();}catch(java.io.IOException e){} }
        FMLRelaunchLog.info("Forge Mod Loader version %s.%s.%s.%s for Minecraft %s loading", FMLInjectionData.major, FMLInjectionData.minor,
                FMLInjectionData.rev, FMLInjectionData.build, FMLInjectionData.mccversion, FMLInjectionData.mcpversion);
        FMLRelaunchLog.info("Java is %s, version %s, running on %s:%s:%s, installed at %s", System.getProperty("java.vm.name"), System.getProperty("java.version"), System.getProperty("os.name"), System.getProperty("os.arch"), System.getProperty("os.version"), System.getProperty("java.home"));
        FMLRelaunchLog.fine("Java classpath at launch is %s", System.getProperty("java.class.path"));
        FMLRelaunchLog.fine("Java library path at launch is %s", System.getProperty("java.library.path"));
        FMLRelaunchLog.fine("Minecraft Forge Fork name-file version forge-1.7.10-10.13.5.0-1.7.10-sw22-10");

        try
        {
            CoreModManager.handleLaunch(minecraftHome, classLoader, tweaker);
        }
        catch (Throwable t)
        {
            t.printStackTrace();
            FMLRelaunchLog.log(Level.ERROR, t, "An error occurred trying to configure the minecraft home at %s for Forge Mod Loader", minecraftHome.getAbsolutePath());
            throw Throwables.propagate(t);
        }
    }

    public static Side side()
    {
        return side;
    }


    private void injectPostfixTransformers()
    {
        CoreModManager.injectTransformers(classLoader);
    }

    public static void appendCoreMods()
    {
        INSTANCE.injectPostfixTransformers();
    }
}
