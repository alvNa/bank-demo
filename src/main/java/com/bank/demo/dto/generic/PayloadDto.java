package com.bank.demo.dto.generic;

import lombok.Data;

import java.util.List;

@Data
public class PayloadDto<T> {
    List<T> list;
}