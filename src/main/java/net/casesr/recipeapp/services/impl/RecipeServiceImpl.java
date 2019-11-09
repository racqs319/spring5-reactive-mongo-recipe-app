package net.casesr.recipeapp.services.impl;

import lombok.extern.slf4j.Slf4j;
import net.casesr.recipeapp.commands.RecipeCommand;
import net.casesr.recipeapp.converters.RecipeCommandToRecipe;
import net.casesr.recipeapp.converters.RecipeToRecipeCommand;
import net.casesr.recipeapp.domain.Recipe;
import net.casesr.recipeapp.exceptions.NotFoundException;
import net.casesr.recipeapp.repositories.RecipeRepository;
import net.casesr.recipeapp.services.RecipeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class RecipeServiceImpl implements RecipeService {
	private final RecipeRepository recipeRepository;
	private final RecipeCommandToRecipe recipeCommandToRecipe;
	private final RecipeToRecipeCommand recipeToRecipeCommand;

	public RecipeServiceImpl(RecipeRepository recipeRepository,
							 RecipeCommandToRecipe recipeCommandToRecipe,
							 RecipeToRecipeCommand recipeToRecipeCommand) {
		this.recipeRepository = recipeRepository;
		this.recipeCommandToRecipe = recipeCommandToRecipe;
		this.recipeToRecipeCommand = recipeToRecipeCommand;
	}

	@Override
	public Set<Recipe> getRecipes() {
		log.debug("In RecipeServiceImpl.getRecipes(");
		
		Set<Recipe> recipeSet = new HashSet<>();
		recipeRepository.findAll().iterator().forEachRemaining(recipeSet::add);
		
		return recipeSet;
	}

    @Override
    public void deleteById(String id) {
        recipeRepository.deleteById(id);
    }

    @Override
	public Recipe findById(String id) {
		Optional<Recipe> recipeOptional = recipeRepository.findById(id);

		if (!recipeOptional.isPresent()) {
			throw new NotFoundException("Recipe not found for ID value: " + id);
		}

		return recipeOptional.get();
	}

	@Transactional
	@Override
	public RecipeCommand findCommandById(String id) {
		RecipeCommand recipeCommand = recipeToRecipeCommand.convert(findById(id));

		// enhance command object with id value
		if (recipeCommand.getIngredients() != null && recipeCommand.getIngredients().size() > 0) {
			recipeCommand.getIngredients().forEach(rc -> {
				rc.setRecipeId(recipeCommand.getId());
			});
		}

		return recipeCommand;
	}

	@Transactional
	@Override
	public RecipeCommand saveRecipeCommand(RecipeCommand command) {
		Recipe detachedRecipe = recipeCommandToRecipe.convert(command);

		Recipe savedRecipe = recipeRepository.save(detachedRecipe);
		log.debug("Saved Recipe Id: " + savedRecipe.getId());
		return recipeToRecipeCommand.convert(savedRecipe);
	}
}
