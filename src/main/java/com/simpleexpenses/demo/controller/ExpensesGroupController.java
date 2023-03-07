package com.simpleexpenses.demo.controller;

import com.simpleexpenses.demo.dto.ExpenseDto;
import com.simpleexpenses.demo.dto.ExpensesGroupDto;
import com.simpleexpenses.demo.model.request.ExpensesGroupRequest;
import com.simpleexpenses.demo.model.response.ExpensesGroupListItemResponse;
import com.simpleexpenses.demo.model.response.ExpensesGroupResponse;
import com.simpleexpenses.demo.model.response.MessageResponse;
import com.simpleexpenses.demo.service.ExpensesGroupService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/expenses-groups")
@RequiredArgsConstructor
public class ExpensesGroupController {

    private final ExpensesGroupService expensesGroupService;

    @PostMapping
    public ResponseEntity<ExpensesGroupResponse> createExpensesGroup(@RequestBody ExpensesGroupRequest expensesGroup) {

        ModelMapper modelMapper = new ModelMapper();
        ExpensesGroupDto expensesGroupDto = modelMapper.map(expensesGroup, ExpensesGroupDto.class);

        ExpensesGroupDto createdExpensesGroup = this.expensesGroupService.createExpensesGroup(expensesGroupDto);

        ExpensesGroupResponse response = modelMapper.map(createdExpensesGroup, ExpensesGroupResponse.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ExpensesGroupListItemResponse>> getExpensesGroups() {

        List<ExpensesGroupDto> expensesGroups = this.expensesGroupService.getExpensesGroups();

        ModelMapper modelMapper = new ModelMapper();
        List<ExpensesGroupListItemResponse> response = expensesGroups.stream().map(expensesGroupDto ->
                    modelMapper.map(expensesGroupDto, ExpensesGroupListItemResponse.class)
                ).collect(Collectors.toList());


        return ResponseEntity.ok(response);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<ExpensesGroupResponse> getExpensesGroup(@PathVariable String groupId) {

        ExpensesGroupDto expensesGroupDto = this.expensesGroupService.getExpensesGroup(groupId);

        ModelMapper modelMapper = new ModelMapper();
        ExpensesGroupResponse expensesGroupResponse = modelMapper.map(expensesGroupDto, ExpensesGroupResponse.class);

        return ResponseEntity.ok(expensesGroupResponse);
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<ExpensesGroupResponse> updateExpensesGroup(@PathVariable String groupId,
                                                               @RequestBody ExpensesGroupRequest expensesGroup) {

        ModelMapper modelMapper = new ModelMapper();
        ExpensesGroupDto expensesGroupDto = modelMapper.map(expensesGroup, ExpensesGroupDto.class);

        ExpensesGroupDto updatedExpensesGroupDto = this.expensesGroupService.updateExpensesGroup(groupId, expensesGroupDto);

        ExpensesGroupResponse expensesGroupResponse = modelMapper.map(updatedExpensesGroupDto, ExpensesGroupResponse.class);

        return ResponseEntity.ok(expensesGroupResponse);
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<MessageResponse> deleteExpensesGroup(@PathVariable String groupId) {

        this.expensesGroupService.deleteExpensesGroup(groupId);

        return ResponseEntity.ok(new MessageResponse("Expenses group deleted successfully."));
    }



}
