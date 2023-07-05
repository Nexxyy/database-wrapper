package com.github.nexy.database.annotations;

import java.lang.annotation.*;
//import java.lang.annotation.

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface StoreVoid {
}
