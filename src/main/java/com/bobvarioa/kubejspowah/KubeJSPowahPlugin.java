package com.bobvarioa.kubejspowah;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.generator.KubeDataGenerator;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchemaRegistry;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.data.GeneratedData;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;

public class KubeJSPowahPlugin implements KubeJSPlugin {
    public static EventGroup GROUP = EventGroup.of("PowahEvents");
    public static EventHandler COOLANTS = GROUP.server("registerCoolants", () -> CoolantsEvent.class);
    public static EventHandler HEAT_SOURCE = GROUP.server("registerHeatSource", () -> HeatSourceEvent.class);
    public static EventHandler MAGMATIC_FLUID = GROUP.server("registerMagmaticFluid", () -> MagmaticFluidEvent.class);
    public static EventHandler REACTOR_FUEL = GROUP.server("registerReactorFuel", () -> ReactorFuelEvent.class);

    @Override
    public void registerRecipeSchemas(RecipeSchemaRegistry registry) {
        registry.register(ResourceLocation.fromNamespaceAndPath("powah", "energizing"), EnergizingOrbRecipe.SCHEMA);
    }

    @Override
    public void registerEvents(EventGroupRegistry registry) {
        registry.register(GROUP);
    }

    @Override
    public void init() {
    }


    private JsonObject toObj(Map<ResourceLocation, Integer> map, String key) {
        var res = new JsonObject();

        var values = new JsonObject();
        for (var entries : map.entrySet()) {
            var obj = new JsonObject();
            obj.addProperty(key, entries.getValue());
            values.add(entries.getKey().toString(), obj);
        }

        res.add("values", values);

        return res;
    }

    private JsonObject toObj(Map<ResourceLocation, Pair<Integer, Integer>> map, String key, String key2) {
        var res = new JsonObject();

        var values = new JsonObject();
        for (var entries : map.entrySet()) {
            var obj = new JsonObject();
            obj.addProperty(key, entries.getValue().left());
            obj.addProperty(key2, entries.getValue().right());
            values.add(entries.getKey().toString(), obj);
        }

        res.add("values", values);

        return res;
    }


    @Override
    public void generateData(KubeDataGenerator generator) {
        KubeJSPowahPlugin.clear();
        KubeJSPowahPlugin.COOLANTS.post(ScriptType.SERVER, KubeJSPowahPlugin.CoolantsEvent.INSTANCE);
        KubeJSPowahPlugin.HEAT_SOURCE.post(ScriptType.SERVER, KubeJSPowahPlugin.HeatSourceEvent.INSTANCE);
        KubeJSPowahPlugin.MAGMATIC_FLUID.post(ScriptType.SERVER, KubeJSPowahPlugin.MagmaticFluidEvent.INSTANCE);
        KubeJSPowahPlugin.REACTOR_FUEL.post(ScriptType.SERVER, KubeJSPowahPlugin.ReactorFuelEvent.INSTANCE);
        generator.add(GeneratedData.json(ResourceLocation.fromNamespaceAndPath("powah", "data_maps/block/heat_source"), () -> toObj(HeatSourceEvent.dataBlocks, "temperature")));
        generator.add(GeneratedData.json(ResourceLocation.fromNamespaceAndPath("powah", "data_maps/fluid/heat_source"), () -> toObj(HeatSourceEvent.dataFluids, "temperature")));
        generator.add(GeneratedData.json(ResourceLocation.fromNamespaceAndPath("powah", "data_maps/fluid/fluid_coolant"), () -> toObj(CoolantsEvent.dataFluids, "temperature")));
        generator.add(GeneratedData.json(ResourceLocation.fromNamespaceAndPath("powah", "data_maps/fluid/magmator_fuel"), () -> toObj(MagmaticFluidEvent.dataFluids, "energy_produced")));
        generator.add(GeneratedData.json(ResourceLocation.fromNamespaceAndPath("powah", "data_maps/item/solid_coolant"), () -> toObj(CoolantsEvent.dataItems, "amount", "temperature")));
        generator.add(GeneratedData.json(ResourceLocation.fromNamespaceAndPath("powah", "data_maps/item/reactor_fuel"), () -> toObj(ReactorFuelEvent.dataItems, "fuelAmount", "temperature")));
    }

    public static void clear() {
        CoolantsEvent.dataFluids.clear();
        CoolantsEvent.dataItems.clear();
        HeatSourceEvent.dataBlocks.clear();
        HeatSourceEvent.dataFluids.clear();
        MagmaticFluidEvent.dataFluids.clear();
        ReactorFuelEvent.dataItems.clear();
    }

    public static class CoolantsEvent implements KubeEvent {
        public static CoolantsEvent INSTANCE = new CoolantsEvent();

        private static Map<ResourceLocation, Integer> dataFluids = new LinkedHashMap<>();
        private static Map<ResourceLocation, Pair<Integer, Integer>> dataItems = new LinkedHashMap<>();

        public void addFluid(ResourceLocation res, int cool) {
            dataFluids.put(res, cool);
        }

        public void addSolid(ItemStack res, int cool) {
            dataItems.put(BuiltInRegistries.ITEM.getKey(res.getItem()), Pair.of(res.getCount(), cool));
        }
    }

    public static class HeatSourceEvent implements KubeEvent {
        public static HeatSourceEvent INSTANCE = new HeatSourceEvent();

        private static Map<ResourceLocation, Integer> dataBlocks = new LinkedHashMap<>();
        private static Map<ResourceLocation, Integer> dataFluids = new LinkedHashMap<>();

        public void addBlock(ResourceLocation res, int heat) {
            dataBlocks.put(res, heat);
        }

        public void addFluid(ResourceLocation res, int heat) {
            dataFluids.put(res, heat);
        }
    }

    public static class MagmaticFluidEvent implements KubeEvent {
        public static MagmaticFluidEvent INSTANCE = new MagmaticFluidEvent();

        private static Map<ResourceLocation, Integer> dataFluids = new LinkedHashMap<>();

        public void add(ResourceLocation res, int heat) {
            dataFluids.put(res, heat);
        }
    }

    public static class ReactorFuelEvent implements KubeEvent {
        public static ReactorFuelEvent INSTANCE = new ReactorFuelEvent();

        private static Map<ResourceLocation, Pair<Integer, Integer>> dataItems = new LinkedHashMap<>();

        public void add(ItemStack res, int heat) {
            dataItems.put(BuiltInRegistries.ITEM.getKey(res.getItem()), Pair.of(res.getCount(), heat));
        }
    }
}
