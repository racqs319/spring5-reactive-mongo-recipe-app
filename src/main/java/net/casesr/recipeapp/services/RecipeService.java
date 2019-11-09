package net.casesr.recipeapp.services;

import net.casesr.recipeapp.commands.RecipeCommand;
import net.casesr.recipeapp.domain.Recipe;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RecipeService {
	
	Flux<Recipe> getRecipes();

	Mono<Recipe> findById(String id);

	Mono<Void> deleteById(String id);

	Mono<RecipeCommand> findCommandById(String id);

	Mono<RecipeCommand> saveRecipeCommand(RecipeCommand command);

}
