package com.simpleexpenses.demo.controller;

import com.simpleexpenses.demo.dto.ExpensesGroupDto;
import com.simpleexpenses.demo.model.request.ExpensesGroupModelRequest;
import com.simpleexpenses.demo.model.response.ExpensesGroupResponse;
import com.simpleexpenses.demo.service.ExpensesGroupService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/expenses-groups")
@RequiredArgsConstructor
public class ExpensesGroupController {

    private final ExpensesGroupService expensesGroupService;

    @PostMapping
    public ResponseEntity<ExpensesGroupResponse> createExpensesGroup(@RequestBody ExpensesGroupModelRequest expensesGroup) {

        ModelMapper modelMapper = new ModelMapper();
        ExpensesGroupDto expensesGroupDto = modelMapper.map(expensesGroup, ExpensesGroupDto.class);

        ExpensesGroupDto createdExpensesGroup = this.expensesGroupService.createExpensesGroup(expensesGroupDto);

        ExpensesGroupResponse response = modelMapper.map(createdExpensesGroup, ExpensesGroupResponse.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<?> getExpensesGroups() {
        return null;
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<?> getExpensesGroup(@PathVariable String groupId) {
        return null;
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<?> updateExpensesGroup(@PathVariable String groupId) {
        return null;
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<?> deleteExpensesGroup(@PathVariable String group) {
        return null;
    }



}
