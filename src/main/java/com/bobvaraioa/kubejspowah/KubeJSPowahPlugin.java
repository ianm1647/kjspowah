package com.bobvaraioa.kubejspowah;

import com.bobvaraioa.kubejspowah.recipes.EnergizingOrbRecipe;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.recipe.RegisterRecipeTypesEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.checkerframework.checker.units.qual.C;
import owmii.powah.api.PowahAPI;

public class KubeJSPowahPlugin extends KubeJSPlugin {
    public static EventGroup GROUP = EventGroup.of("PowahEvents");
    public static EventHandler COOLANTS = GROUP.server("registerCoolants", () -> CoolantsEvent.class);
    public static EventHandler HEAT_SOURCE = GROUP.server("registerHeatSource", () -> HeatSourceEvent.class);
    public static EventHandler MAGMATIC_FLUID = GROUP.server("registerMagmaticFluid", () -> MagmaticFluidEvent.class);

    @Override
    public void registerRecipeTypes(RegisterRecipeTypesEvent event) {
        event.register(new ResourceLocation("powah:energizing"), EnergizingOrbRecipe::new);
    }
    @Override
    public void registerEvents() {
        GROUP.register();
    }

    static class CoolantsEvent extends EventJS {
        public static CoolantsEvent INSTANCE = new CoolantsEvent();
        public void addFluid(ResourceLocation res, int cool) {
            PowahAPI.registerCoolant(res, cool);
        }

        public void addSolid(ItemStack res, int cool) {
            PowahAPI.registerSolidCoolant(res.getItem(), res.getCount(), cool);
        }
    }

    static class HeatSourceEvent extends EventJS {
        public static HeatSourceEvent INSTANCE = new HeatSourceEvent();

        public void add(ResourceLocation res, int heat) {
            PowahAPI.registerHeatSource(res, heat);
        }
    }

    static class MagmaticFluidEvent extends EventJS {
        public static MagmaticFluidEvent INSTANCE = new MagmaticFluidEvent();

        public void add(ResourceLocation res, int heat) {
            PowahAPI.registerMagmaticFluid(res, heat);
        }
    }
}
