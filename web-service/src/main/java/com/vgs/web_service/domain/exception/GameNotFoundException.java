package com.vgs.web_service.domain.exception;

public class GameNotFoundException extends DomainException {
    public GameNotFoundException(Long id) {
        super(String.format("Game with id %d not found", id));
    }
}