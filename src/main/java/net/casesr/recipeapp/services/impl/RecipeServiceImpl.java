package net.casesr.recipeapp.services.impl;

import lombok.extern.slf4j.Slf4j;
import net.casesr.recipeapp.commands.RecipeCommand;
import net.casesr.recipeapp.converters.RecipeCommandToRecipe;
import net.casesr.recipeapp.converters.RecipeToRecipeCommand;
import net.casesr.recipeapp.domain.Recipe;
import net.casesr.recipeapp.repositories.reactive.RecipeReactiveRepository;
import net.casesr.recipeapp.services.RecipeService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class RecipeServiceImpl implements RecipeService {
	private final RecipeReactiveRepository recipeReactiveRepository;
	private final RecipeCommandToRecipe recipeCommandToRecipe;
	private final RecipeToRecipeCommand recipeToRecipeCommand;

	public RecipeServiceImpl(RecipeReactiveRepository recipeRepository,
							 RecipeCommandToRecipe recipeCommandToRecipe,
							 RecipeToRecipeCommand recipeToRecipeCommand) {
		this.recipeReactiveRepository = recipeRepository;
		this.recipeCommandToRecipe = recipeCommandToRecipe;
		this.recipeToRecipeCommand = recipeToRecipeCommand;
	}

	@Override
	public Flux<Recipe> getRecipes() {
		log.debug("In RecipeServiceImpl.getRecipes(");

		return recipeReactiveRepository.findAll();
	}

    @Override
    public Mono<Void> deleteById(String id) {
        recipeReactiveRepository.deleteById(id).block();

        return Mono.empty();
    }

    @Override
	public Mono<Recipe> findById(String id) {
		return recipeReactiveRepository.findById(id);
	}

	@Override
	public Mono<RecipeCommand> findCommandById(String id) {
		return recipeReactiveRepository.findById(id)
				.map(recipe -> {
					RecipeCommand recipeCommand = recipeToRecipeCommand.convert(recipe);

					recipeCommand.getIngredients().forEach(rc -> {
						rc.setRecipeId(recipeCommand.getId());
					});

					return recipeCommand;
				});
	}

	@Override
	public Mono<RecipeCommand> saveRecipeCommand(RecipeCommand command) {
		return recipeReactiveRepository.save(recipeCommandToRecipe.convert(command))
				.map(recipeToRecipeCommand::convert);
	}
}
