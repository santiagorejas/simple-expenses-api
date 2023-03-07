package com.simpleexpenses.demo.service.impl;

import com.simpleexpenses.demo.dto.ExpensesGroupDto;
import com.simpleexpenses.demo.exceptions.AccessDeniedException;
import com.simpleexpenses.demo.exceptions.EntityNotFoundException;
import com.simpleexpenses.demo.model.entity.ExpensesGroupEntity;
import com.simpleexpenses.demo.repository.ExpensesGroupRepository;
import com.simpleexpenses.demo.service.ExpensesGroupService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpensesGroupServiceImpl implements ExpensesGroupService {

    private final ExpensesGroupRepository expensesGroupRepository;

    @Override
    public ExpensesGroupDto createExpensesGroup(ExpensesGroupDto expensesGroup) {

        ModelMapper modelMapper = new ModelMapper();

        String userId = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getSubject();

        ExpensesGroupEntity expensesGroupEntity = modelMapper.map(expensesGroup, ExpensesGroupEntity.class);
        expensesGroupEntity.setExpensesGroupId(UUID.randomUUID().toString());
        expensesGroupEntity.setCreatedAt(new Date());
        expensesGroupEntity.setUserId(userId);

        ExpensesGroupEntity createdExpensesGroupEntity = this.expensesGroupRepository.save(expensesGroupEntity);

        ExpensesGroupDto createdExpensesGroupDto = modelMapper.map(createdExpensesGroupEntity, ExpensesGroupDto.class);

        return createdExpensesGroupDto;
    }

    @Override
    public List<ExpensesGroupDto> getExpensesGroups() {

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        List<ExpensesGroupEntity> expensesGroupEntities = this.expensesGroupRepository
                .findAllByUserId(userId)
                .orElseThrow();

        ModelMapper modelMapper = new ModelMapper();

        List<ExpensesGroupDto> expensesGroupDtoList = expensesGroupEntities.stream().map(userEntity ->
            modelMapper.map(userEntity, ExpensesGroupDto.class)
        ).collect(Collectors.toList());

        return expensesGroupDtoList;
    }

    @Override
    public ExpensesGroupDto updateExpensesGroup(String groupId, ExpensesGroupDto expensesGroupDto) {

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        ExpensesGroupEntity expensesGroupEntity = this.expensesGroupRepository
                .findByExpensesGroupId(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Expenses Group not found."));


        if (!userId.equals(expensesGroupEntity.getUserId())) {
            throw new AccessDeniedException("You don't have permission to update this expenses group.");
        }

        expensesGroupEntity.setTitle(expensesGroupDto.getTitle());
        expensesGroupEntity.setDescription(expensesGroupDto.getDescription());

        ExpensesGroupEntity storedExpensesGroupEntity = this.expensesGroupRepository.save(expensesGroupEntity);

        ModelMapper modelMapper = new ModelMapper();

        ExpensesGroupDto storedExpensesGroupDto = modelMapper.map(storedExpensesGroupEntity, ExpensesGroupDto.class);

        return storedExpensesGroupDto;
    }

    @Override
    public void deleteExpensesGroup(String groupId) {

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        ExpensesGroupEntity expensesGroupEntity = this.expensesGroupRepository
                .findByExpensesGroupId(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Expenses group not found."));

        if (!userId.equals(expensesGroupEntity.getUserId())) {
            throw new AccessDeniedException("You don't have permission to delete this expenses group.");
        }

        this.expensesGroupRepository.delete(expensesGroupEntity);

    }

    @Override
    public ExpensesGroupDto getExpensesGroup(String groupId) {

        ExpensesGroupEntity expensesGroup = this.expensesGroupRepository
                .findByExpensesGroupIdPopulated(groupId)
                .orElseThrow();

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
        ExpensesGroupDto expensesGroupDto = modelMapper.map(expensesGroup, ExpensesGroupDto.class);

        return expensesGroupDto;
    }
}
