package id.my.btw.entity;

import lombok.Getter;

@Getter
public class CommandMessage {
    private final String command;
    private final String message;

    public CommandMessage(String command, String message) {
        this.command = command;
        this.message = message;
    }
}