package ru.practicum.shareit.exception;

public class ItemOwnerValidationException extends RuntimeException {
    public ItemOwnerValidationException(String message) {
        super(message);
    }
}
