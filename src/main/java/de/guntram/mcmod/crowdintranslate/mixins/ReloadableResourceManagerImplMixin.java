package de.guntram.mcmod.crowdintranslate.mixins;

import de.guntram.mcmod.crowdintranslate.CTResourcePack;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.resource.*;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ReloadableResourceManagerImpl.class)

public abstract class ReloadableResourceManagerImplMixin implements ReloadableResourceManager
{
    @Shadow
    @Final
    private ResourceType type;

    @Shadow
    public abstract void addPack(ResourcePack resourcePack);

    // Compatibility with 22w03a+ using slf4j logger instead of log4j
    @Inject(
            method = "reload",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/slf4j/Logger;isDebugEnabled()Z",
                    shift = At.Shift.BEFORE
            ),
            require = 0
    )
    private void onPostReload(Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage, List<ResourcePack> packs, CallbackInfoReturnable<ResourceReload> cir)
    {
        if (this.type != ResourceType.CLIENT_RESOURCES)
            return;

        this.addPack(new CTResourcePack());
    }

    // Compatibility with 1.18.1 and below using log4j
    @Inject(
            method = "reload",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/apache/logging/log4j/Logger;isDebugEnabled()Z",
                    shift = At.Shift.BEFORE
            ),
            require = 0
    )
    private void onPostReloadLegacy(Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage, List<ResourcePack> packs, CallbackInfoReturnable<ResourceReload> cir)
    {
        if (this.type != ResourceType.CLIENT_RESOURCES)
            return;

        this.addPack(new CTResourcePack());
    }
}
