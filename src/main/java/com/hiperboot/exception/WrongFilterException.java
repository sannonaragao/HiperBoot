package com.hiperboot.exception;

import java.io.Serial;
import java.util.List;

import lombok.Getter;

@Getter
public class WrongFilterException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -688632812694259105L;

    public WrongFilterException(Class<?> clazz, List<String> filterList) {
        super(clazz.getSimpleName() + " don't accept those fields as filter: " + String.join(", ", filterList));
    }
}
