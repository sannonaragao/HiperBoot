package com.hiperboot.exception;

import java.io.Serial;

import lombok.Getter;

@Getter
public class HiperBootException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 4664240996541494157L;

    public HiperBootException(String msg) {
        super(msg);
    }
}
