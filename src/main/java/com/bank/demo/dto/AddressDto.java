package com.bank.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class AddressDto implements Serializable {
    String address;
    String city;
    String countryCode;
}