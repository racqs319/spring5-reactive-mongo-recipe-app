package net.casesr.recipeapp.services.impl;

import lombok.extern.slf4j.Slf4j;
import net.casesr.recipeapp.commands.UnitOfMeasureCommand;
import net.casesr.recipeapp.converters.UnitOfMeasureToUnitOfMeasureCommand;
import net.casesr.recipeapp.repositories.UnitOfMeasureRepository;
import net.casesr.recipeapp.services.UnitOfMeasureService;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class UnitOfMeasureServiceImpl implements UnitOfMeasureService {

    private final UnitOfMeasureRepository unitOfMeasureRepository;
    private final UnitOfMeasureToUnitOfMeasureCommand command;

    public UnitOfMeasureServiceImpl(UnitOfMeasureRepository unitOfMeasureRepository,
                                    UnitOfMeasureToUnitOfMeasureCommand command) {
        this.unitOfMeasureRepository = unitOfMeasureRepository;
        this.command = command;
    }

    @Override
    public Set<UnitOfMeasureCommand> listAllUoms() {
        return StreamSupport.stream(unitOfMeasureRepository.findAll()
                    .spliterator(), false)
                .map(command::convert)
                .collect(Collectors.toSet());
    }
}
