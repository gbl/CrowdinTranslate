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

    @Inject(
            method = "beginMonitoredReload",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/resource/ReloadableResourceManagerImpl;beginReloadInner(Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/List;Ljava/util/concurrent/CompletableFuture;)Lnet/minecraft/resource/ResourceReloadMonitor;",
                    shift = At.Shift.BEFORE
            )
    )
    private void onPostReload(Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage, List<ResourcePack> packs, CallbackInfoReturnable<ResourceReloadMonitor> cir)
    {
        if (this.type != ResourceType.CLIENT_RESOURCES)
            return;

        System.out.println("Inject generated resource packs.");
        this.addPack(new CTResourcePack());
    }
}