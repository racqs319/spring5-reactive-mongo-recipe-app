package net.casesr.recipeapp.services.impl;

import net.casesr.recipeapp.commands.RecipeCommand;
import net.casesr.recipeapp.converters.RecipeCommandToRecipe;
import net.casesr.recipeapp.converters.RecipeToRecipeCommand;
import net.casesr.recipeapp.domain.Recipe;
import net.casesr.recipeapp.exceptions.NotFoundException;
import net.casesr.recipeapp.repositories.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class RecipeServiceImplTest {
	
	RecipeServiceImpl recipeService;
	
	@Mock
    RecipeRepository recipeRepository;

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
		
		when(recipeRepository.findAll()).thenReturn(recipesData);
		
		Set<Recipe> recipes = recipeService.getRecipes();
		assertEquals(recipesData.size(), recipes.size());
		
		verify(recipeRepository, times(1)).findAll();
	}

	@Test
	void testGetRecipeById() throws Exception {
		Recipe recipe = new Recipe();
		recipe.setId("1");
		Optional<Recipe> recipeOptional = Optional.of(recipe);

		when(recipeRepository.findById(anyString())).thenReturn(recipeOptional);

		Recipe recipeReturned = recipeService.findById("1");

		assertNotNull(recipeReturned,"Null recipe returned");
		verify(recipeRepository, times(1)).findById(anyString());
		verify(recipeRepository, never()).findAll();
	}

	@Test
	public void testGetRecipeByIdNotFound() throws Exception {
		Optional<Recipe> recipeOptional = Optional.empty();

		when(recipeRepository.findById(anyString())).thenReturn(recipeOptional);

		Throwable exception = assertThrows(NotFoundException.class, () -> {
			recipeService.findById("1");
		});

		assertEquals("Recipe not found for ID value: 1", exception.getMessage());

		//should go boom
	}

	@Test
	public void testGetRecipeCommandById() throws Exception {
		Recipe recipe = new Recipe();
		recipe.setId("1");
		Optional<Recipe> recipeOptional = Optional.of(recipe);

		when(recipeRepository.findById(anyString())).thenReturn(recipeOptional);

		RecipeCommand recipeCommand = new RecipeCommand();
		recipeCommand.setId("1");

		when(recipeToRecipeCommand.convert(any())).thenReturn(recipeCommand);

		RecipeCommand commandById = recipeService.findCommandById("1");

		assertNotNull(commandById, "Null recipe returned");
		verify(recipeRepository, times(1)).findById(anyString());
		verify(recipeRepository, never()).findAll();
	}

	@Test
	public void testDeleteById() throws Exception {
		String idToDelete = "2";
		recipeService.deleteById(idToDelete);

		verify(recipeRepository, times(1)).deleteById(anyString());
	}

}
