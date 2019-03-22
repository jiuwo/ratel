package org.jiuwo.ratel.contract.enums;

import lombok.Getter;

/**
 * @author Steven Han
 */
@Getter
public enum EnableEnum {

    /**
     * UNABLE
     */
    UNABLE(0, "UNABLE"),
    /**
     * ENABLE
     */
    ENABLE(1, "ENABLE");

    private int key;

    private String name;

    EnableEnum(int key, String name) {
        this.key = key;
        this.name = name;
    }
}
