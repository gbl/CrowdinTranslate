package de.guntram.mcmod.crowdintranslate.mixins;

import de.guntram.mcmod.crowdintranslate.CTResourcePack;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.resource.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ReloadableResourceManagerImpl.class)

public abstract class ReloadableResourceManagerImplMixin
{
    @Shadow
    @Final
    private ResourceType type;

    @ModifyArg(
            method = "reload",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/LifecycledResourceManagerImpl;<init>(Lnet/minecraft/resource/ResourceType;Ljava/util/List;)V"),
            index = 1
    )
    private List<ResourcePack> onPostReload(List<ResourcePack> packs)
    {
        if (this.type != ResourceType.CLIENT_RESOURCES)
            return packs;

        List<ResourcePack> list = new ArrayList<>(packs);
        list.add(new CTResourcePack());
        return list;
    }
}
