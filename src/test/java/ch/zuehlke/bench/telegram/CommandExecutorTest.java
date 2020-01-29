package ch.zuehlke.bench.telegram;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandExecutorTest {

    @Test
    void parseCommand_EmptyString() {
        Optional<CommandExecutor.COMMAND> command = CommandExecutor.parseCommand("");
        assertNull(command);
    }

    @Test
    void parseCommand_NULL() {
        Optional<CommandExecutor.COMMAND> command = CommandExecutor.parseCommand(null);
        assertNull(command);
    }

    @Test
    void parseCommand_NoCommand() {
        Optional<CommandExecutor.COMMAND> command = CommandExecutor.parseCommand("heinz");
        assertNull(command);
    }

    @Test
    void parseCommand_SinglePrefix() {
        Optional<CommandExecutor.COMMAND> command = CommandExecutor.parseCommand(CommandExecutor.PREFIX);
        assertFalse(command.isPresent());
    }


    @ParameterizedTest
    @EnumSource(CommandExecutor.COMMAND.class)
    void parseCommand_All(CommandExecutor.COMMAND cmd) {
        Optional<CommandExecutor.COMMAND> command = CommandExecutor.parseCommand(CommandExecutor.PREFIX + cmd.name());
        assertTrue(command.isPresent());
    }


}