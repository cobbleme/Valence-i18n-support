package dev.valence.i18n.mixin;

import dev.valence.i18n.pack.VirtualI18nResourcePack;
import net.minecraft.resource.*;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;
import java.util.function.Consumer;

@Mixin(ResourcePackManager.class)
public class ResourcePackManagerMixin {
    @Shadow @Final private Set<ResourcePackProvider> providers;
    @Shadow private Map<String, ResourcePackProfile> profiles;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void valence$injectVirtualPack(CallbackInfo ci) {
        this.providers.add(new ResourcePackProvider() {
            @Override
            public void register(Consumer<ResourcePackProfile> profileAdder) {
                ResourcePackInfo info = new ResourcePackInfo(
                        "valence_i18n_virtual",
                        Text.literal("Valence i18n & Font Pack"),
                        ResourcePackSource.BUILTIN,
                        Optional.empty()
                );

                ResourcePackProfile.PackFactory factory = new ResourcePackProfile.PackFactory() {
                    @Override
                    public ResourcePack open(ResourcePackInfo info) {
                        return new VirtualI18nResourcePack(info);
                    }

                    @Override
                    public ResourcePack openWithOverlays(ResourcePackInfo info, ResourcePackProfile.Metadata metadata) {
                        return new VirtualI18nResourcePack(info);
                    }
                };

                ResourcePackPosition position = new ResourcePackPosition(true, ResourcePackProfile.InsertionPosition.TOP, false);
                ResourcePackProfile profile = ResourcePackProfile.create(
                        info,
                        factory,
                        ResourceType.CLIENT_RESOURCES,
                        position
                );

                if (profile != null) {
                    profileAdder.accept(profile);
                }
            }
        });
    }

    @Inject(method = "buildEnabledProfiles", at = @At("RETURN"), cancellable = true)
    private void valence$forceEnableVirtualPack(Collection<String> enabledNames, CallbackInfoReturnable<List<ResourcePackProfile>> cir) {
        if (this.profiles != null) {
            ResourcePackProfile ourProfile = this.profiles.get("valence_i18n_virtual");
            if (ourProfile != null) {
                List<ResourcePackProfile> list = new ArrayList<>(cir.getReturnValue());
                if (!list.contains(ourProfile)) {
                    list.add(ourProfile); // 位于最高优先级覆盖
                    cir.setReturnValue(list);
                }
            }
        }
    }
}
