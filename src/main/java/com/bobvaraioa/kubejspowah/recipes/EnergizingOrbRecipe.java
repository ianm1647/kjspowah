package com.bobvaraioa.kubejspowah.recipes;

import com.google.gson.JsonArray;
import dev.latvian.mods.kubejs.recipe.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class EnergizingOrbRecipe extends RecipeJS {
    public List<Ingredient> inputs = null;
    public ItemStack output = null;
    public long energy = 0;

    @Override
    public void create(RecipeArguments args) {
        // .create([items, ...], output, energy)
        inputs = parseItemInputList(args.get(0));
        output = parseItemOutput(args.get(1));
        energy = args.getLong(2, 0);
    }

    @Override
    public void deserialize() {
        inputs = parseItemInputList(json.get("ingredients"));
        output = parseItemOutput(json.get("result"));
        energy = json.get("energy").getAsLong();
    }

    @Override
    public void serialize() {
        var list = new JsonArray();
        for (var ele : inputs) {
            list.add(ele.toJson());
        }
        json.add("ingredients", list);
        json.add("result", itemToJson(output));
        json.addProperty("energy", energy);
    }

    @Override
    public boolean hasInput(IngredientMatch match) {
        for (var ele : inputs) {
            if (match.contains(ele)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean replaceInput(IngredientMatch match, Ingredient with, ItemInputTransformer transformer) {
        var changed = false;
        for (int i = 0; i < inputs.size(); i++) {
            var ele = inputs.get(i);
            if (match.contains(ele)) {
                changed = true;
                inputs.set(i, transformer.transform(this, match, ele, with));
            }
        }
        return changed;
    }

    @Override
    public boolean hasOutput(IngredientMatch match) {
        return match.contains(output);
    }

    @Override
    public boolean replaceOutput(IngredientMatch match, ItemStack with, ItemOutputTransformer transformer) {
        if (match.contains(output)) {
            output = transformer.transform(this, match, output, with);
            return true;
        }
        return false;
    }
}
