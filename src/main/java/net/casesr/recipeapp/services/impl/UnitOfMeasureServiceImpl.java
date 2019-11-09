package net.casesr.recipeapp.services.impl;

import lombok.extern.slf4j.Slf4j;
import net.casesr.recipeapp.commands.UnitOfMeasureCommand;
import net.casesr.recipeapp.converters.UnitOfMeasureToUnitOfMeasureCommand;
import net.casesr.recipeapp.repositories.UnitOfMeasureRepository;
import net.casesr.recipeapp.repositories.reactive.UnitOfMeasureReactiveRepository;
import net.casesr.recipeapp.services.UnitOfMeasureService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class UnitOfMeasureServiceImpl implements UnitOfMeasureService {

    private final UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository;
    private final UnitOfMeasureToUnitOfMeasureCommand command;

    public UnitOfMeasureServiceImpl(UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository,
                                    UnitOfMeasureToUnitOfMeasureCommand command) {
        this.unitOfMeasureReactiveRepository = unitOfMeasureReactiveRepository;
        this.command = command;
    }

    @Override
    public Flux<UnitOfMeasureCommand> listAllUoms() {
        return unitOfMeasureReactiveRepository
                .findAll()
                .map(command::convert);
    }
}
