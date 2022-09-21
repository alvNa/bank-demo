package com.bank.demo.services;

import com.bank.demo.dto.TransactionDto;
import com.bank.demo.model.Transaction;
import com.bank.demo.model.TransactionType;
import com.bank.demo.repositories.TransactionRepository;
import lombok.val;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ModelMapper modelMapper;

    public void saveAll(List<TransactionDto> transactionDtos){
        val transactions = transactionDtos.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
        transactionRepository.saveAll(transactions);
    }

    private Transaction toModel(TransactionDto dto){
        Transaction t = modelMapper.map(dto, Transaction.class);
        val type = TransactionType.builder()
                .id(dto.getType().getEnumeration())
                .description(dto.getType().getValue())
                .build();
        t.setTransactionType(type);
        return t;
    }
}
