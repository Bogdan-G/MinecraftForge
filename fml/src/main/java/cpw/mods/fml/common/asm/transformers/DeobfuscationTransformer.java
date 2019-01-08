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

package cpw.mods.fml.common.asm.transformers;

import java.util.Arrays;

import net.minecraft.launchwrapper.IClassNameTransformer;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.RemappingClassAdapter;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import cpw.mods.fml.common.asm.transformers.deobf.FMLRemappingAdapter;

public class DeobfuscationTransformer implements IClassTransformer, IClassNameTransformer {
    private static final String[] EXEMPT_LIBS = new String[] {
            "com.google.",
            "com.mojang.",
            "joptsimple.",
            "io.netty.",
            "it.unimi.dsi.fastutil.",
            "oshi.",
            "com.sun.",
            "com.ibm.",
            "paulscode.",
            "com.jcraft"
    };
    private static final String[] EXEMPT_DEV = new String[] {
            "net.minecraft.",
            "net.minecraftforge."
    };

    private boolean deobfuscatedEnvironment = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes)
    {
        if (bytes == null)
        {
            return null;
        }

        if (!shouldTransform(name)) return bytes;

        ClassReader classReader = new ClassReader(bytes);
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        RemappingClassAdapter remapAdapter = new FMLRemappingAdapter(classWriter);
        classReader.accept(remapAdapter, ClassReader.EXPAND_FRAMES);
        return classWriter.toByteArray();
    }

    private boolean shouldTransform(String name)
    {
        boolean transformLib = true;for (int i=0; i<EXEMPT_LIBS.length; i++) {if(name.startsWith(EXEMPT_LIBS[i])) {transformLib=false; break;}}

        if (deobfuscatedEnvironment)
        {
            boolean transformDev = true;for (int i=0; i<EXEMPT_DEV.length; i++) {if(name.startsWith(EXEMPT_DEV[i])) {transformDev=false; break;}}
            return transformLib && transformDev;
        }
        else
        {
            return transformLib;
        }
    }

    @Override
    public String remapClassName(String name)
    {
        return FMLDeobfuscatingRemapper.INSTANCE.map(name.replace('.','/')).replace('/', '.');
    }

    @Override
    public String unmapClassName(String name)
    {
        return FMLDeobfuscatingRemapper.INSTANCE.unmap(name.replace('.', '/')).replace('/','.');
    }

}
