package com.bobvaraioa.kubejspowah;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;
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

    interface EnergizingOrbRecipe {
        RecipeKey<InputItem[]> INPUTS = ItemComponents.UNWRAPPED_INPUT_ARRAY.key("ingredients");
        RecipeKey<OutputItem> OUTPUT = ItemComponents.OUTPUT.key("result");
        RecipeKey<Long> ENERGY = NumberComponent.LONG.key("energy");

        RecipeSchema SCHEMA = new RecipeSchema(INPUTS, OUTPUT, ENERGY);
    }

    @Override
    public void registerRecipeSchemas(RegisterRecipeSchemasEvent event) {
        event.register(new ResourceLocation("powah:energizing"), EnergizingOrbRecipe.SCHEMA);
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
