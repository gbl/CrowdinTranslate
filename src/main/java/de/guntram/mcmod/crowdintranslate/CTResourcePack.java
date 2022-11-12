package de.guntram.mcmod.crowdintranslate;

import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

/**
 * Code taken from LambdAurora, oral permission on Discord on 2020-10-04
 */

public class CTResourcePack implements ResourcePack
{
    private final List<String> namespaces = new ArrayList<>();
    private static final Logger LOGGER = LogUtils.getLogger();
    
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
    public InputSupplier<InputStream> openRoot(String ... fileName)
    {
        File file = new File(CrowdinTranslate.getRootDir(), fileName[0]);
        if (file.exists()) {
            return InputSupplier.create(file.toPath());
        } else {
            return null;
        }
    }

    @Override
    public InputSupplier<InputStream> open(ResourceType type, Identifier id)
    {
        return this.openRoot(type.getDirectory() + "/" + id.getNamespace() + "/" + id.getPath());
    }

    @Override
    public void findResources(ResourceType type, String namespace, String prefix, ResourcePack.ResultConsumer consumer) {
        String start = CrowdinTranslate.getRootDir()+"/assets/" + namespace + "/" + prefix;
        String[] files = new File(start).list();
        //LOGGER.info("finding resources for {} {}", namespace, prefix);
        if (files == null || files.length == 0) {
            //LOGGER.info("found nothing");
            return;
        }
        //LOGGER.info("found {} files, first is {}", files.length, files[0]);
        List<Identifier> resultList = Arrays.asList(files)
                .stream()
                .map(CTResourcePack::fromPath)
                .collect(Collectors.toList());
        for(Identifier result: resultList) {
            //LOGGER.info("sending {} to consumer", result.toString());
            consumer.accept(result, open(type, result));
        }
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
    
    @Override
    public boolean isAlwaysStable() {
        return true;
    }

    private static Identifier fromPath(String path)
    {
        if (path.startsWith("assets/"))
            path = path.substring("assets/".length());
        String[] split = path.split("/", 2);
        return new Identifier(split[0], split[1]);
    }
}
