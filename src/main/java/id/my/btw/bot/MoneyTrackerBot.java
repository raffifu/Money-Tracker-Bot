package id.my.btw.bot;

import id.my.btw.service.CallbackService;
import id.my.btw.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;
import java.util.function.Consumer;

@Slf4j
public class MoneyTrackerBot extends TelegramLongPollingBot {
    private final MessageService messageService;
    private final CallbackService callbackService;

    public MoneyTrackerBot(MessageService messageService, CallbackService callbackService) {
        this.messageService = messageService;
        this.callbackService = callbackService;
    }

    @Override
    public String getBotUsername() {
        return System.getenv("BOT_USERNAME");
    }

    @Override
    public String getBotToken() {
        return System.getenv("BOT_TOKEN");
    }

    @Override
    public void onUpdateReceived(Update update) {
        Consumer<? super BotApiMethod<? extends Serializable>> responseHandler = response -> {
            try {
                execute(response);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        };

        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                Message message = update.getMessage();
                log.info("Received Message from {}", message.getFrom().getUserName());

                messageService.handleIncomingMessage(message, responseHandler);

            } else if (update.hasCallbackQuery()) {
                CallbackQuery callbackQuery = update.getCallbackQuery();
                log.info("Received Callback from {}", callbackQuery.getFrom().getUserName());

                callbackService.handleIncomingCallback(callbackQuery, responseHandler);
            }
        } catch (TelegramApiException e) {
            log.error("Error handling incoming message", e);
        }
    }
}
