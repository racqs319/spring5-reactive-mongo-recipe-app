package net.casesr.recipeapp.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CategoryTest {
	
	Category category;

	@BeforeEach
	void setUp() throws Exception {
		category = new Category();
	}

	@Test
	void testGetId() {
		String idValue = "4";
		
		category.setId(idValue);
		
		assertEquals(idValue, category.getId());
	}

	@Test
	void testGetDescription() {
		//fail("Not yet implemented");
	}

	@Test
	void testGetRecipes() {
		//fail("Not yet implemented");
	}

}
