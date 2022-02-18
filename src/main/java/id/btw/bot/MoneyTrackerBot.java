package id.btw.bot;

import id.btw.entity.Expense;
import id.btw.repository.ExpenseRepository;
import id.btw.util.ResponseFormatter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.ZoneId;

@Slf4j
public class MoneyTrackerBot extends TelegramLongPollingBot {

    private ExpenseRepository expenseRepository;

    public MoneyTrackerBot(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
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
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                log.info("Received Message from {}", update.getMessage().getFrom().getUserName());

                Message message = update.getMessage();
                handleIncomingMessage(message);

            } else if (update.hasCallbackQuery()) {
                log.info("Received Callback from {}", update.getCallbackQuery().getFrom().getUserName());

                // TODO: Handle Callback
            }
        } catch (TelegramApiException e) {
            log.error("Error handling incoming message", e);
        }
    }

    private void handleIncomingMessage(Message message) throws TelegramApiException {
        if (!message.isUserMessage())
            return;

        if (!message.isCommand())
            onTextMessage(message.getChatId(), message.getText());
    }

    private void onTextMessage(Long chatId, String message) throws TelegramApiException {
        // TODO: Parsing one by one
        MutableList<String> msgListMutable = Lists.mutable.of(message.split("\n"));

        if (msgListMutable.size() != 3) {
            log.error("Invalid input");
            return;
        }

        Expense expense = Expense.builder()
                .amount(Integer.parseInt(msgListMutable.get(0)))
                .name(msgListMutable.get(1))
                .description(msgListMutable.get(2))
                .date(LocalDate.now(ZoneId.of("Asia/Jakarta")))
                .build();

        expenseRepository.insert(expense);

        execute(responseMessage(chatId, expense));

    }

    private SendMessage responseMessage(Long chatId, Expense expense) {
        log.info("Sending validation success message");

        InlineKeyboardButton updateButton = InlineKeyboardButton.builder()
                .text("Update")
                .callbackData("update")
                .build();

        InlineKeyboardButton deleteButton = InlineKeyboardButton.builder()
                .text("Delete")
                .callbackData("delete")
                .build();

        MutableList<InlineKeyboardButton> keyboardButtons = Lists.mutable.of(updateButton, deleteButton);

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboardRow(keyboardButtons)
                .build();

        return SendMessage.builder()
                .chatId(chatId.toString())
                .text(ResponseFormatter.onSucceedExpensePayload(expense))
                .replyMarkup(markup)
                .build();
    }
}
