package com.bobvarioa.kubejspowah;

import com.google.gson.JsonArray;
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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


    private JsonObject toObj(Map<ResourceLocation, Integer> map, String key, List<ResourceLocation> removals) {
        var res = new JsonObject();

        var values = new JsonObject();
        for (var entries : map.entrySet()) {
            var sub = new JsonObject();
            sub.addProperty("replace", true);

            var obj = new JsonObject();
            obj.addProperty(key, entries.getValue());
            sub.add("value", obj);
            values.add(entries.getKey().toString(), sub);
        }

        res.add("values", values);

        var removed = new JsonArray();
        for (var item : removals) {
            removed.add(item.toString());
        }
        res.add("remove", removed);

        return res;
    }

    private JsonObject toObj(Map<ResourceLocation, Pair<Integer, Integer>> map, String key, String key2, List<ResourceLocation> removals) {
        var res = new JsonObject();


        var values = new JsonObject();
        for (var entries : map.entrySet()) {
            var sub = new JsonObject();
            sub.addProperty("replace", true);
            var obj = new JsonObject();
            obj.addProperty(key, entries.getValue().left());
            obj.addProperty(key2, entries.getValue().right());
            sub.add("value", obj);
            values.add(entries.getKey().toString(), sub);
        }

        res.add("values", values);

        var removed = new JsonArray();
        for (var item : removals) {
            removed.add(item.toString());
        }
        res.add("remove", removed);

        return res;
    }


    @Override
    public void generateData(KubeDataGenerator generator) {
        KubeJSPowahPlugin.clear();
        KubeJSPowahPlugin.COOLANTS.post(ScriptType.SERVER, KubeJSPowahPlugin.CoolantsEvent.INSTANCE);
        KubeJSPowahPlugin.HEAT_SOURCE.post(ScriptType.SERVER, KubeJSPowahPlugin.HeatSourceEvent.INSTANCE);
        KubeJSPowahPlugin.MAGMATIC_FLUID.post(ScriptType.SERVER, KubeJSPowahPlugin.MagmaticFluidEvent.INSTANCE);
        KubeJSPowahPlugin.REACTOR_FUEL.post(ScriptType.SERVER, KubeJSPowahPlugin.ReactorFuelEvent.INSTANCE);
        generator.add(GeneratedData.json(ResourceLocation.fromNamespaceAndPath("powah", "data_maps/block/heat_source"), () -> toObj(HeatSourceEvent.addedBlocks, "temperature", HeatSourceEvent.removedBlocks)));
        generator.add(GeneratedData.json(ResourceLocation.fromNamespaceAndPath("powah", "data_maps/fluid/heat_source"), () -> toObj(HeatSourceEvent.addedFluids, "temperature", HeatSourceEvent.removedFluids)));
        generator.add(GeneratedData.json(ResourceLocation.fromNamespaceAndPath("powah", "data_maps/fluid/fluid_coolant"), () -> toObj(CoolantsEvent.addedFluids, "temperature", CoolantsEvent.removedFluids)));
        generator.add(GeneratedData.json(ResourceLocation.fromNamespaceAndPath("powah", "data_maps/fluid/magmator_fuel"), () -> toObj(MagmaticFluidEvent.addedFluids, "energy_produced", MagmaticFluidEvent.removedFluids)));
        generator.add(GeneratedData.json(ResourceLocation.fromNamespaceAndPath("powah", "data_maps/item/solid_coolant"), () -> toObj(CoolantsEvent.addedItems, "amount", "temperature", CoolantsEvent.removedItems)));
        generator.add(GeneratedData.json(ResourceLocation.fromNamespaceAndPath("powah", "data_maps/item/reactor_fuel"), () -> toObj(ReactorFuelEvent.addedItems, "fuelAmount", "temperature", ReactorFuelEvent.removedItems)));
    }

    public static void clear() {
        CoolantsEvent.addedFluids.clear();
        CoolantsEvent.addedItems.clear();
        HeatSourceEvent.addedBlocks.clear();
        HeatSourceEvent.addedFluids.clear();
        MagmaticFluidEvent.addedFluids.clear();
        ReactorFuelEvent.addedItems.clear();
    }

    public static class CoolantsEvent implements KubeEvent {
        public static CoolantsEvent INSTANCE = new CoolantsEvent();

        private static Map<ResourceLocation, Integer> addedFluids = new LinkedHashMap<>();
        private static Map<ResourceLocation, Pair<Integer, Integer>> addedItems = new LinkedHashMap<>();
        private static List<ResourceLocation> removedFluids = new ArrayList();
        private static List<ResourceLocation> removedItems = new ArrayList();

        public void addFluid(ResourceLocation res, int cool) {
            addedFluids.put(res, cool);
        }

        public void addSolid(ItemStack res, int cool) {
            addedItems.put(BuiltInRegistries.ITEM.getKey(res.getItem()), Pair.of(res.getCount(), cool));
        }

        public void removeFluid(ResourceLocation fluid) {
            removedFluids.add(fluid);
        }

        public void removeSolid(ResourceLocation solid) {
            removedItems.add(solid);
        }
    }

    public static class HeatSourceEvent implements KubeEvent {
        public static HeatSourceEvent INSTANCE = new HeatSourceEvent();

        private static Map<ResourceLocation, Integer> addedBlocks = new LinkedHashMap<>();
        private static Map<ResourceLocation, Integer> addedFluids = new LinkedHashMap<>();
        private static List<ResourceLocation> removedBlocks = new ArrayList();
        private static List<ResourceLocation> removedFluids = new ArrayList();

        public void addBlock(ResourceLocation res, int heat) {
            addedBlocks.put(res, heat);
        }

        public void addFluid(ResourceLocation res, int heat) {
            addedFluids.put(res, heat);
        }

        public void removeBlock(ResourceLocation block) {
            removedBlocks.add(block);
        }

        public void removeFluid(ResourceLocation solid) {
            removedFluids.add(solid);
        }
    }

    public static class MagmaticFluidEvent implements KubeEvent {
        public static MagmaticFluidEvent INSTANCE = new MagmaticFluidEvent();

        private static Map<ResourceLocation, Integer> addedFluids = new LinkedHashMap<>();
        private static List<ResourceLocation> removedFluids = new ArrayList();

        public void add(ResourceLocation res, int heat) {
            addedFluids.put(res, heat);
        }

        public void remove(ResourceLocation fluid) {
            removedFluids.add(fluid);
        }
    }

    public static class ReactorFuelEvent implements KubeEvent {
        public static ReactorFuelEvent INSTANCE = new ReactorFuelEvent();

        private static Map<ResourceLocation, Pair<Integer, Integer>> addedItems = new LinkedHashMap<>();
        private static List<ResourceLocation> removedItems = new ArrayList();

        public void add(ItemStack res, int heat) {
            addedItems.put(BuiltInRegistries.ITEM.getKey(res.getItem()), Pair.of(res.getCount(), heat));
        }

        public void remove(ResourceLocation item) {
            removedItems.add(item);
        }
    }
}
