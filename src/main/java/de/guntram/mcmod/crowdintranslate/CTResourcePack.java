package de.guntram.mcmod.crowdintranslate;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;

/**
 * Code taken from LambdAurora, oral permission on Discord on 2020-10-04
 */

public class CTResourcePack implements ResourcePack
{
    private final List<String>                     namespaces = new ArrayList<>();
    
    public CTResourcePack() {
        for (String s: CrowdinTranslate.registeredMods()) {
            put(s+"/lang");
        }
        put("crowdintranslate/lang");
    }
    
    public boolean put(String resource)
    {
        Identifier id = fromPath(resource);
        if (!this.namespaces.contains(id.getNamespace())) {
            this.namespaces.add(id.getNamespace());
        }
        return true;
    }

    @Override
    public InputStream openRoot(String fileName) throws IOException
    {
        File file = new File(CrowdinTranslate.getRootDir(), fileName);
        return new FileInputStream(file);
    }

    @Override
    public InputStream open(ResourceType type, Identifier id) throws IOException
    {
        if (type == ResourceType.SERVER_DATA) throw new IOException("Reading server data from MCPatcherPatcher client resource pack");
        return this.openRoot(type.getDirectory() + "/" + id.getNamespace() + "/" + id.getPath());
    }

    @Override
    public Collection<Identifier> findResources(ResourceType type, String namespace, String prefix, int maxDepth, Predicate<String> pathFilter)
    {
        if (type == ResourceType.SERVER_DATA) return Collections.emptyList();
        String start = CrowdinTranslate.getRootDir()+"/assets/" + namespace + "/" + prefix;
        String[] files = new File(start).list();
        if (files == null || files.length == 0) {
            return Collections.EMPTY_LIST;
        }
        List<Identifier> result = Arrays.asList(files)
                .stream()
                .map(CTResourcePack::fromPath)
                .collect(Collectors.toList());
        return result;
    }

    @Override
    public boolean contains(ResourceType type, Identifier id)
    {
        String path = CrowdinTranslate.getRootDir() + "/" + type.getDirectory() + "/" + id.getNamespace() + "/" + id.getPath();
        return new File(path).exists();
    }

    @Override
    public Set<String> getNamespaces(ResourceType type)
    {
        return new HashSet<>(this.namespaces);
    }

    @Override
    public <T> T parseMetadata(ResourceMetadataReader<T> metaReader) throws IOException
    {
        return null;
    }

    @Override
    public String getName()
    {
        return "CrowdinTranslate internal Resource Pack";
    }

    @Override
    public void close()
    {
    }

    private static Identifier fromPath(String path)
    {
        if (path.startsWith("assets/"))
            path = path.substring("assets/".length());
        String[] split = path.split("/", 2);
        return new Identifier(split[0], split[1]);
    }
}
