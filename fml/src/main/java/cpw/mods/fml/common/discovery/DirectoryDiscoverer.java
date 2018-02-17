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

package cpw.mods.fml.common.discovery;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.logging.log4j.Level;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.LoaderException;
import cpw.mods.fml.common.MetadataCollection;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.ModContainerFactory;
import cpw.mods.fml.common.discovery.asm.ASMModParser;

public class DirectoryDiscoverer implements ITypeDiscoverer
{
    private static final boolean DEBUG_DD = Boolean.parseBoolean(System.getProperty("fml.debugDirectoryDiscoverer", "false"));
    
    private class ClassFilter implements FileFilter
    {
        @Override
        public boolean accept(File file)
        {
            return (file.isFile() && classFile.matcher(file.getName()).matches()) || file.isDirectory();
        }
    }

    private ASMDataTable table;

    @Override
    public List<ModContainer> discover(ModCandidate candidate, ASMDataTable table)
    {
        this.table = table;
        List<ModContainer> found = Lists.newArrayList();
        String candidate_name = candidate.getModContainer().getName();
        //skip search in folders
        if (candidate_name.equalsIgnoreCase("VoxelMods") || candidate_name.equalsIgnoreCase("carpentersblocks") || candidate_name.equalsIgnoreCase("matmos") || candidate_name.equalsIgnoreCase("railcraft") || candidate_name.equalsIgnoreCase("moarperipherals") || candidate_name.equalsIgnoreCase("gammabright") || candidate_name.equalsIgnoreCase("LOTR_UpdatedLangFiles") || candidate_name.equalsIgnoreCase("Reika") || candidate_name.equalsIgnoreCase("tabula") || candidate_name.equalsIgnoreCase("ThebombzenAPI") || candidate_name.equalsIgnoreCase("TerrainControl") || candidate_name.equalsIgnoreCase("presencefootsteps")) {
            if (DEBUG_DD) FMLLog.fine("Skip examining directory %s for potential mods", candidate_name);
            return found;
        }
        if (DEBUG_DD) FMLLog.fine("Examining directory %s for potential mods", candidate_name);
        exploreFileSystem("", candidate.getModContainer(), found, candidate, null);
        for (ModContainer mc : found)
        {
            table.addContainer(mc);
        }
        return found;
    }

    public void exploreFileSystem(String path, File modDir, List<ModContainer> harvestedMods, ModCandidate candidate, MetadataCollection mc)
    {
        if (path.length() == 0)
        {
            File metadata = new File(modDir, "mcmod.info");
            try
            {
                try (FileInputStream fis = new FileInputStream(metadata)) {
                try (BufferedInputStream bis  = new BufferedInputStream(fis)) {
                mc = MetadataCollection.from(bis,modDir.getName());
                }}//fis.close();
                if (DEBUG_DD) FMLLog.fine("Found an mcmod.info file in directory %s", modDir.getName());
            }
            catch (Exception e)
            {
                mc = MetadataCollection.from(null,"");
                if (DEBUG_DD) FMLLog.fine("No mcmod.info file found in directory %s", modDir.getName());
            }
        }

        File[] content = modDir.listFiles(new ClassFilter());

        // Always sort our content
        Arrays.sort(content);
        for (File file : content)
        {
            String file_name = file.getName();
            if (file.isDirectory())
            {
                if (DEBUG_DD) FMLLog.finer("Recursing into package %s", path + file_name);
                exploreFileSystem(path + file_name + ".", file, harvestedMods, candidate, mc);
                continue;
            }
            Matcher match = classFile.matcher(file_name);

            if (match.matches())
            {
                ASMModParser modParser = null;
                try
                {
                    try (FileInputStream fis = new FileInputStream(file)) {
                    try (BufferedInputStream bis  = new BufferedInputStream(fis)) {
                    modParser = new ASMModParser(bis);
                    }}//fis.close();
                    candidate.addClassEntry(path+file_name);
                }
                catch (LoaderException e)
                {
                    FMLLog.log(Level.ERROR, e, "There was a problem reading the file %s - probably this is a corrupt file", file.getPath());
                    throw e;
                }
                catch (Exception e)
                {
                    Throwables.propagate(e);
                }

                modParser.validate();
                modParser.sendToTable(table, candidate);
                ModContainer container = ModContainerFactory.instance().build(modParser, candidate.getModContainer(), candidate);
                if (container!=null)
                {
                    harvestedMods.add(container);
                    container.bindMetadata(mc);
                }
            }


        }
    }

}
