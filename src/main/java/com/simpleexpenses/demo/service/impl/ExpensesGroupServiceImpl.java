package com.simpleexpenses.demo.service.impl;

import com.simpleexpenses.demo.dto.ExpensesGroupDto;
import com.simpleexpenses.demo.model.entity.ExpensesGroupEntity;
import com.simpleexpenses.demo.repository.ExpensesGroupRepository;
import com.simpleexpenses.demo.service.ExpensesGroupService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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

        List<ExpensesGroupEntity> expensesGroupEntities = this.expensesGroupRepository.findAllByUserId(userId);

        ModelMapper modelMapper = new ModelMapper();

        List<ExpensesGroupDto> expensesGroupDtoList = expensesGroupEntities.stream().map(userEntity ->
            modelMapper.map(userEntity, ExpensesGroupDto.class)
        ).collect(Collectors.toList());

        return expensesGroupDtoList;
    }
}
