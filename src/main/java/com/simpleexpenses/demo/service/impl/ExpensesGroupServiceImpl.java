package com.simpleexpenses.demo.service.impl;

import com.simpleexpenses.demo.dto.ExpenseDto;
import com.simpleexpenses.demo.dto.ExpensesGroupDto;
import com.simpleexpenses.demo.dto.PagedDto;
import com.simpleexpenses.demo.exceptions.AccessDeniedException;
import com.simpleexpenses.demo.exceptions.EntityNotFoundException;
import com.simpleexpenses.demo.model.entity.ExpenseEntity;
import com.simpleexpenses.demo.model.entity.ExpensesGroupEntity;
import com.simpleexpenses.demo.repository.ExpenseRepository;
import com.simpleexpenses.demo.repository.ExpensesGroupRepository;
import com.simpleexpenses.demo.service.ExpensesGroupService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    private final ExpenseRepository expenseRepository;

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
    public PagedDto<ExpenseDto> getExpenses(String groupId, int page, int size) {

        ExpensesGroupEntity expensesGroup = this.expensesGroupRepository
                .findByExpensesGroupId(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Expenses group doesn't exist."));

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!expensesGroup.getUserId().equals(userId)) {
            throw new AccessDeniedException("You can't access this expenses group.");
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "date");
        Pageable paging = PageRequest.of(page, size, sort);

        Page<ExpenseEntity> expensesEntities = this.expenseRepository
                .findAllByExpensesGroup_Id(expensesGroup.getId(), paging)
                .orElseThrow(() -> new EntityNotFoundException("Expenses not found."));

        ModelMapper modelMapper = new ModelMapper();
        List<ExpenseDto> expenseDtoList = expensesEntities
                .stream()
                .map(expenseEntity -> modelMapper.map(expenseEntity, ExpenseDto.class))
                .toList();

        PagedDto<ExpenseDto> pagedExpenseDto = PagedDto
                .<ExpenseDto>builder()
                .content(expenseDtoList)
                .page(expensesEntities.getNumber())
                .size(expensesEntities.getTotalPages())
                .totalPages(expensesEntities.getTotalPages())
                .totalElements(expensesEntities.getTotalElements())
                .build();

        return pagedExpenseDto;
    }

    @Override
    public ExpensesGroupDto getExpensesGroup(String groupId) {

        ExpensesGroupEntity expensesGroupEntity = this.expensesGroupRepository
                .findByExpensesGroupId(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Expenses group doesn't exist."));

        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!expensesGroupEntity.getUserId().equals(userId)) {
            throw new AccessDeniedException("You don't have access to this expenses group.");
        }

        ModelMapper modelMapper = new ModelMapper();
        ExpensesGroupDto expensesGroupDto = modelMapper.map(expensesGroupEntity, ExpensesGroupDto.class);

        return expensesGroupDto;
    }
}
