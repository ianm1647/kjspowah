package com.bobvaraioa.kubejspowah;

import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(KubeJSPowah.MODID)
public class KubeJSPowah {
    public static final String MODID = "kubejspowah";

    public KubeJSPowah() {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, KubeJSPowah::serverReload);
    }

    public static void serverReload(TagsUpdatedEvent event) {
        KubeJSPowahPlugin.COOLANTS.post(ScriptType.SERVER, KubeJSPowahPlugin.CoolantsEvent.INSTANCE);
        KubeJSPowahPlugin.HEAT_SOURCE.post(ScriptType.SERVER, KubeJSPowahPlugin.HeatSourceEvent.INSTANCE);
        KubeJSPowahPlugin.MAGMATIC_FLUID.post(ScriptType.SERVER, KubeJSPowahPlugin.MagmaticFluidEvent.INSTANCE);
    }
}
