package ru.yandex.practicum.filmorate.exceptions;

import lombok.Data;

@Data
public class ErrorResponse {
    private final String error;
}