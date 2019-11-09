package net.casesr.recipeapp.controllers;

import lombok.extern.slf4j.Slf4j;
import net.casesr.recipeapp.services.RecipeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class IndexController {
	
	private final RecipeService recipeService;
	
	public IndexController(RecipeService recipeService) {
		super();
		this.recipeService = recipeService;
	}

	@GetMapping({"", "/", "/index", "/index.html"})
	public String getIndexPage(Model model) {
		log.debug("Add list of recipes in index page");
		model.addAttribute("recipes", recipeService.getRecipes());
		
		return "index";
	}

}
