package ru.practicum.shareit.exception;

public class ItemRequestValidationException extends RuntimeException {
    public ItemRequestValidationException(String message) {
        super(message);
    }
}
