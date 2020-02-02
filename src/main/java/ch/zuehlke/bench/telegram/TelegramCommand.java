package ch.zuehlke.bench.telegram;

public interface TelegramCommand {
    String execute(String... parameter);
}
