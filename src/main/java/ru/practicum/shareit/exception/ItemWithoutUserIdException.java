package ru.practicum.shareit.exception;

public class ItemWithoutUserIdException extends RuntimeException {
    public ItemWithoutUserIdException(String message) {
        super(message);
    }
}
