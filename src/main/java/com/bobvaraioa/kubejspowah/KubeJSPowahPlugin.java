package com.bobvaraioa.kubejspowah;

import com.bobvaraioa.kubejspowah.recipes.EnergizingOrbRecipe;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.RegisterRecipeTypesEvent;
import net.minecraft.resources.ResourceLocation;

public class KubeJSPowahPlugin extends KubeJSPlugin {
    @Override
    public void registerRecipeTypes(RegisterRecipeTypesEvent event) {
        event.register(new ResourceLocation("powah:energizing"), EnergizingOrbRecipe::new);
    }
}
