package net.casesr.recipeapp.repositories.reactive;

import net.casesr.recipeapp.domain.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataMongoTest
public class CategoryReactiveRepositoryTest {

    @Autowired
    CategoryReactiveRepository categoryReactiveRepository;

    @BeforeEach
    public void setUp() throws Exception {
        categoryReactiveRepository.deleteAll().block();
    }

    @Test
    public void testSaveCategory() throws Exception {
        Category category = new Category();
        category.setDescription("Foo");

        categoryReactiveRepository.save(category).block();

        Long count = categoryReactiveRepository.count().block();

        assertEquals(Long.valueOf(1L), count);
    }

    @Test
    public void testFindByDescription() throws Exception {
        Category category = new Category();
        category.setDescription("Foo");

        categoryReactiveRepository.save(category).block();

        Category fetchedCat = categoryReactiveRepository.findByDescription("Foo").block();

        assertNotNull(fetchedCat.getId());
    }

}
