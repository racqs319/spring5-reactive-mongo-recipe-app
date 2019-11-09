package net.casesr.recipeapp.services.impl;

import lombok.extern.slf4j.Slf4j;
import net.casesr.recipeapp.commands.IngredientCommand;
import net.casesr.recipeapp.converters.IngredientCommandToIngredient;
import net.casesr.recipeapp.converters.IngredientToIngredientCommand;
import net.casesr.recipeapp.domain.Ingredient;
import net.casesr.recipeapp.domain.Recipe;
import net.casesr.recipeapp.repositories.reactive.RecipeReactiveRepository;
import net.casesr.recipeapp.repositories.reactive.UnitOfMeasureReactiveRepository;
import net.casesr.recipeapp.services.IngredientService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@Service
public class IngredientServiceImpl implements IngredientService {

    private final IngredientToIngredientCommand ingredientToIngredientCommand;
    private final IngredientCommandToIngredient ingredientCommandToIngredient;
    private final RecipeReactiveRepository recipeReactiveRepository;
    private final UnitOfMeasureReactiveRepository unitOfMeasureRepository;

    public IngredientServiceImpl(IngredientToIngredientCommand ingredientToIngredientCommand,
                                 IngredientCommandToIngredient ingredientCommandToIngredient,
                                 RecipeReactiveRepository recipeReactiveRepository,
                                 UnitOfMeasureReactiveRepository unitOfMeasureRepository) {
        this.ingredientToIngredientCommand = ingredientToIngredientCommand;
        this.ingredientCommandToIngredient = ingredientCommandToIngredient;
        this.recipeReactiveRepository = recipeReactiveRepository;
        this.unitOfMeasureRepository = unitOfMeasureRepository;
    }

    @Override
    public Mono<IngredientCommand> findByRecipeIdAndIngredientId(String recipeId, String ingredientId) {
        return recipeReactiveRepository
                .findById(recipeId)
                .flatMapIterable(Recipe::getIngredients)
                .filter(ingredient -> ingredient.getId().equalsIgnoreCase(ingredientId))
                .single()
                .map(ingredient -> {
                   IngredientCommand command = ingredientToIngredientCommand.convert(ingredient);
                   command.setRecipeId(recipeId);
                   return command;
                });
//        Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);
//
//        if (!recipeOptional.isPresent()) {
//            log.error("recipe id not found: " + recipeId);
//        }
//
//        Recipe recipe = recipeOptional.get();
//
//        Optional<IngredientCommand> ingredientCommandOptional = recipe.getIngredients().stream()
//                .filter(ingredient -> ingredient.getId().equals(ingredientId))
//                .map(ingredient -> ingredientToIngredientCommand.convert(ingredient)).findFirst();
//
//        if (!ingredientCommandOptional.isPresent()) {
//            log.error("Ingredient id not found: " + ingredientId);
//        }
//
//        // enhance command object with recipe id
//        IngredientCommand ingredientCommand = ingredientCommandOptional.get();
//        ingredientCommand.setRecipeId(recipe.getId());
//
//        return Mono.just(ingredientCommand);
    }

    @Override
    public Mono<IngredientCommand> saveIngredientCommand(IngredientCommand ingredientCommand) {
        Recipe recipe = recipeReactiveRepository.findById(ingredientCommand.getRecipeId()).block();

        if (recipe == null) {
            log.error("Recipe not found for id: " + ingredientCommand.getRecipeId());
            return Mono.just(new IngredientCommand());
        } else {
            Optional<Ingredient> ingredientOptional = recipe
                    .getIngredients()
                    .stream()
                    .filter(ingredient -> ingredient.getId().equals(ingredientCommand.getId()))
                    .findFirst();

            if (ingredientOptional.isPresent()) {
                Ingredient ingredientFound = ingredientOptional.get();
                ingredientFound.setDescription(ingredientCommand.getDescription());
                ingredientFound.setAmount(ingredientCommand.getAmount());
                ingredientFound.setUom(unitOfMeasureRepository
                        .findById(ingredientCommand.getUom().getId()).block());
                if (ingredientFound.getUom() == null) {
                    new RuntimeException("UOM NOT FOUND");
                }
            } else {
                //add new ingredient
                Ingredient ingredient = ingredientCommandToIngredient.convert(ingredientCommand);
                recipe.addIngredient(ingredient);
            }

            Recipe savedRecipe = recipeReactiveRepository.save(recipe).block();

            Optional<Ingredient> savedIngredientOptional = savedRecipe.getIngredients().stream()
                    .filter(recipeIngredients -> recipeIngredients.getId().equals(ingredientCommand.getId()))
                    .findFirst();

            //check by description
            if (!savedIngredientOptional.isPresent()) {
                //not totally save... but bet guess
                savedIngredientOptional = savedRecipe.getIngredients().stream()
                        .filter(recipeIngredients -> recipeIngredients.getDescription().equals(ingredientCommand.getDescription()))
                        .filter(recipeIngredients -> recipeIngredients.getAmount().equals(ingredientCommand.getAmount()))
                        .filter(recipeIngredients -> recipeIngredients.getUom().getId().equals(ingredientCommand.getUom().getId()))
                        .findFirst();
            }

            //todo check for fail

            // enhance with id value
            IngredientCommand ingredientCommandSaved = ingredientToIngredientCommand.convert(savedIngredientOptional.get());
            ingredientCommandSaved.setRecipeId(recipe.getId());

            return Mono.just(ingredientCommandSaved);
        }
    }

    @Override
    public Mono<Void> deleteById(String recipeId, String idToDelete) {
        log.debug("Deleting ingredient: " + recipeId + ":" + idToDelete);

        Recipe recipe = recipeReactiveRepository.findById(recipeId).block();

        if (recipe != null) {
            log.debug("found recipe");

            Optional<Ingredient> ingredientOptional = recipe
                    .getIngredients()
                    .stream()
                    .filter(recipeIngredients -> recipeIngredients.getId().equals(idToDelete))
                    .findFirst();

            if (ingredientOptional.isPresent()) {
                log.debug("found ingredient");

                recipe.getIngredients().remove(ingredientOptional.get());
                recipeReactiveRepository.save(recipe).block();

            }
        } else {
            log.debug("Recipe not found: " + recipeId);
        }

        return Mono.empty();
    }
}
