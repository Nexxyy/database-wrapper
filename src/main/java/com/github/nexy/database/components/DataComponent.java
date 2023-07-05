package com.github.nexy.database.components;

import com.github.nexy.database.constants.ComponentType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DataComponent {

    private final String columnName;
    private final ComponentType componentType;

    public String adjuster(int index) {
        return String.format(index >= 1 ? ", " : "" + "`%s` %s", this.columnName, this.componentType.name());
    }

}
