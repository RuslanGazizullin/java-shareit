package ru.practicum.shareit.exception;

public class ItemWithoutUserIdException extends Exception {
    public ItemWithoutUserIdException(String message) {
        super(message);
    }
}
