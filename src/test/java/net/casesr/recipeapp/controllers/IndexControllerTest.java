package net.casesr.recipeapp.controllers;

import net.casesr.recipeapp.domain.Recipe;
import net.casesr.recipeapp.services.RecipeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import reactor.core.publisher.Flux;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class IndexControllerTest {
	
	IndexController indexController;
	
	@Mock
    RecipeService recipeService;
	
	@Mock
    Model model;
	
	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		indexController = new IndexController(recipeService);
	}
	
	@Test
	void testMockMVC() throws Exception {
		MockMvc mockMvc = MockMvcBuilders.standaloneSetup(indexController).build();

		when(recipeService.getRecipes()).thenReturn(Flux.empty());
		
		mockMvc.perform(get("/"))
			.andExpect(status().isOk())
			.andExpect(view().name("index"));
	}

	@Test
	void testGetIndexPage() {
		// given
		Set<Recipe> recipesData = new HashSet<>();
		recipesData.add(new Recipe());
		
		Recipe recipe = new Recipe();
		recipe.setId("1");
		recipesData.add(recipe);
		
		when(recipeService.getRecipes()).thenReturn(Flux.fromIterable(recipesData));
		
		ArgumentCaptor<List<Recipe>> argCaptor = ArgumentCaptor.forClass(List.class);
		
		String expectedView = "index";
		
		assertEquals(expectedView, indexController.getIndexPage(model));
		
		verify(recipeService, times(1)).getRecipes();
		verify(model, times(1)).addAttribute(ArgumentMatchers.eq("recipes"), argCaptor.capture());
		List<Recipe> listInController = argCaptor.getValue();
		assertEquals(2, listInController.size());
	}

}
