package id.my.btw;

import id.my.btw.bot.MoneyTrackerBot;
import id.my.btw.repository.ExpenseRepository;
import id.my.btw.repository.ExpenseRepositoryImpl;
import id.my.btw.service.CallbackService;
import id.my.btw.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
public class Application {
    public static void main(String[] args) {
        try {
            log.info("Bot started");
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

            registerBot(botsApi);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private static void registerBot(TelegramBotsApi botsApi) throws TelegramApiException {
        ExpenseRepository expenseRepository = new ExpenseRepositoryImpl();

        MessageService messageService = new MessageService(expenseRepository);
        CallbackService callbackService = new CallbackService(expenseRepository);
        MoneyTrackerBot moneyTrackerBot = new MoneyTrackerBot(messageService, callbackService);

        botsApi.registerBot(moneyTrackerBot);
    }
}
