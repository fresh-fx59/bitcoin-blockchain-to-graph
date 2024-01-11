package com.example.bitcoinblockchaintograph.entity.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CreatedByEnum {

    UNKNOWN("unknown");

    private final String inDb;
}
