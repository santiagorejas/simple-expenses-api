package com.simpleexpenses.demo.controller;

import com.simpleexpenses.demo.dto.ExpenseDto;
import com.simpleexpenses.demo.model.request.ExpenseRequest;
import com.simpleexpenses.demo.model.response.ExpenseResponse;
import com.simpleexpenses.demo.model.response.MessageResponse;
import com.simpleexpenses.demo.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseResponse> createExpense(@RequestBody ExpenseRequest expense) {

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
        ExpenseDto expenseDto = modelMapper.map(expense, ExpenseDto.class);

        ExpenseDto createdExpenseDto = this.expenseService.createExpense(expenseDto);

        ExpenseResponse expenseResponse = modelMapper.map(createdExpenseDto, ExpenseResponse.class);

        return ResponseEntity.ok(expenseResponse);
    }

    @PutMapping("/{expenseId}")
    public ResponseEntity<ExpenseResponse> updateExpense(@PathVariable String expenseId,
                                                         @RequestBody ExpenseRequest expense) {

        ModelMapper modelMapper = new ModelMapper();
        ExpenseDto expenseDto = modelMapper.map(expense, ExpenseDto.class);

        ExpenseDto updatedExpenseDto = this.expenseService.updateExpense(expenseId, expenseDto);

        ExpenseResponse expenseResponse = modelMapper.map(updatedExpenseDto, ExpenseResponse.class);

        return ResponseEntity.ok(expenseResponse);
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<MessageResponse> deleteExpense(@PathVariable String expenseId) {

        this.expenseService.deleteExpense(expenseId);

        return ResponseEntity.ok(new MessageResponse("Expense deleted successfully."));
    }

    @PostMapping("/{expenseId}/categories/{categoryId}")
    public ResponseEntity<MessageResponse> addCategory(
            @PathVariable String categoryId,
            @PathVariable String expenseId) {

        this.expenseService.addCategory(categoryId, expenseId);

        return ResponseEntity.ok(new MessageResponse("Category added successfully."));
    }

    @DeleteMapping("/{expenseId}/categories/{categoryId}")
    public ResponseEntity<MessageResponse> removeCategory(
            @PathVariable String categoryId,
            @PathVariable String expenseId) {

        this.expenseService.removeCategory(categoryId, expenseId);

        return ResponseEntity.ok(new MessageResponse("Category removed successfully."));
    }

}
