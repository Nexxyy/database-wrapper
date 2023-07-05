package com.github.nexy.database.example.component;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PlayerAccount {

    private final String name;
    private final int coins;

}
