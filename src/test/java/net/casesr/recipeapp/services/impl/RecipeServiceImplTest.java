package net.casesr.recipeapp.services.impl;

import net.casesr.recipeapp.commands.RecipeCommand;
import net.casesr.recipeapp.converters.RecipeCommandToRecipe;
import net.casesr.recipeapp.converters.RecipeToRecipeCommand;
import net.casesr.recipeapp.domain.Recipe;
import net.casesr.recipeapp.repositories.reactive.RecipeReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class RecipeServiceImplTest {
	
	RecipeServiceImpl recipeService;
	
	@Mock
	RecipeReactiveRepository recipeRepository;

	@Mock
    RecipeCommandToRecipe recipeCommandToRecipe;

	@Mock
    RecipeToRecipeCommand recipeToRecipeCommand;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		recipeService = new RecipeServiceImpl(recipeRepository, recipeCommandToRecipe, recipeToRecipeCommand);
		
	}

	@SuppressWarnings("rawtypes")
	@Test
	void testGetRecipes() {
		Recipe recipe = new Recipe();
		HashSet recipesData = new HashSet<>();
		recipesData.add(recipe);
		
		when(recipeRepository.findAll()).thenReturn(Flux.just(recipe));
		
		List<Recipe> recipes = recipeService.getRecipes().collectList().block();
		assertEquals(recipesData.size(), recipes.size());
		
		verify(recipeRepository, times(1)).findAll();
	}

	@Test
	void testGetRecipeById() throws Exception {
		Recipe recipe = new Recipe();
		recipe.setId("1");

		when(recipeRepository.findById(anyString())).thenReturn(Mono.just(recipe));

		Recipe recipeReturned = recipeService.findById("1").block();

		assertNotNull(recipeReturned,"Null recipe returned");
		verify(recipeRepository, times(1)).findById(anyString());
		verify(recipeRepository, never()).findAll();
	}

	@Test
	public void testGetRecipeCommandById() throws Exception {
		Recipe recipe = new Recipe();
		recipe.setId("1");

		when(recipeRepository.findById(anyString())).thenReturn(Mono.just(recipe));

		RecipeCommand recipeCommand = new RecipeCommand();
		recipeCommand.setId("1");

		when(recipeToRecipeCommand.convert(any())).thenReturn(recipeCommand);

		RecipeCommand commandById = recipeService.findCommandById("1").block();

		assertNotNull(commandById, "Null recipe returned");
		verify(recipeRepository, times(1)).findById(anyString());
		verify(recipeRepository, never()).findAll();
	}

	@Test
	public void testDeleteById() throws Exception {
		when(recipeRepository.deleteById(anyString())).thenReturn(Mono.empty());

		String idToDelete = "2";
		recipeService.deleteById(idToDelete);

		verify(recipeRepository, times(1)).deleteById(anyString());
	}

}
