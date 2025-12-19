package ru.yandex.practicum.catsgram.exception;

import lombok.Getter;

@Getter
public class ParameterNotValidException extends IllegalArgumentException {

    private String parametr;
    private String reason;

    public ParameterNotValidException(String parametr, String reason) {
        this.parametr = parametr;
        this.reason = reason;
    }

}
