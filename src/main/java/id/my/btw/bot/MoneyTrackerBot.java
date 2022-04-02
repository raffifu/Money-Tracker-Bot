package id.my.btw.bot;

import id.my.btw.CallbackData;
import id.my.btw.entity.Expense;
import id.my.btw.repository.ExpenseRepository;
import id.my.btw.util.CommonUtil;
import id.my.btw.util.KeyboardUtil;
import id.my.btw.util.ResponseFormatter;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
public class MoneyTrackerBot extends TelegramLongPollingBot {
    private final ExpenseRepository expenseRepository;

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
                Message message = update.getMessage();
                log.info("Received Message from {}", message.getFrom().getUserName());

                handleIncomingMessage(message);

            } else if (update.hasCallbackQuery()) {
                CallbackQuery callbackQuery = update.getCallbackQuery();
                log.info("Received Callback from {}", callbackQuery.getFrom().getUserName());

                handleIncomingCallback(callbackQuery);
            }
        } catch (TelegramApiException e) {
            log.error("Error handling incoming message", e);
        }
    }

    private void handleIncomingMessage(Message message) throws TelegramApiException {
        if (!message.isUserMessage())
            return;

        if (message.isReply()) {
            log.info("Edit existing expense");

            Integer id = CommonUtil.getId(message.getReplyToMessage().getText());
            if (id == null)
                throw new TelegramApiException("Invalid Message");

            Expense expense = CommonUtil.getExpense(message);
            expense.setId(id);
            expenseRepository.update(expense);

            execute(responseRefreshMessage(message, expense));
            execute(responseUpdateMessage(message));
        } else if (message.isCommand()) {
            // TODO: Handle command
            log.info("Received command: {}", message.getText());
        } else {
            Expense expense = CommonUtil.getExpense(message);
            log.info("Add new expense");

            expenseRepository.insert(expense);
            execute(responseMessage(message, expense));
        }

    }

    private void handleIncomingCallback(CallbackQuery callbackQuery) throws TelegramApiException {
        Message message = callbackQuery.getMessage();

        if (CallbackData.DELETE.equals(callbackQuery.getData())) {
            execute(responseConfirmDeleteCallback(message));
        } else if (CallbackData.CONFIRM_DELETE.equals(callbackQuery.getData())) {
            Integer id = CommonUtil.getId(message.getText());
            log.info("Deleting message with messageId {} and id {}", message.getMessageId(), id);

            if (id == null)
                throw new TelegramApiException("Invalid Message");

            expenseRepository.delete(id);
            execute(responseDeleteCallback(callbackQuery.getMessage()));

        } else if (CallbackData.CANCEL_DELETE.equals(callbackQuery.getData())) {
            execute(responseCancelDeleteCallback(callbackQuery.getMessage()));
        }
    }

    private SendMessage responseMessage(Message message, Expense expense) {
        log.info("Sending validation success message");

        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text(ResponseFormatter.onSucceedExpensePayload(expense))
                .replyMarkup(KeyboardUtil.deletePad())
                .replyToMessageId(message.getMessageId())
                .parseMode(ParseMode.MARKDOWN)
                .build();
    }

    private SendMessage responseUpdateMessage(Message message) {
        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .replyToMessageId(message.getMessageId())
                .text("Success, update in database")
                .build();
    }

    private EditMessageText responseRefreshMessage(Message message, Expense expense) {
        return EditMessageText.builder()
                .chatId(message.getChatId().toString())
                .messageId(message.getReplyToMessage().getMessageId())
                .text(ResponseFormatter.onSucceedExpensePayload(expense))
                .replyMarkup(KeyboardUtil.deletePad())
                .parseMode(ParseMode.MARKDOWN)
                .build();
    }

    private EditMessageText responseConfirmDeleteCallback(Message message) {
        return EditMessageText.builder()
                .chatId(message.getChatId().toString())
                .messageId(message.getMessageId())
                .replyMarkup(KeyboardUtil.confirmDeletePad())
                .text(message.getText())
                .parseMode(ParseMode.MARKDOWN)
                .build();
    }

    private EditMessageText responseCancelDeleteCallback(Message message) {
        return EditMessageText.builder()
                .chatId(message.getChatId().toString())
                .messageId(message.getMessageId())
                .replyMarkup(KeyboardUtil.deletePad())
                .text(message.getText())
                .parseMode(ParseMode.MARKDOWN)
                .build();
    }

    private EditMessageText responseDeleteCallback(Message message) {
        return EditMessageText.builder()
                .chatId(message.getChatId().toString())
                .messageId(message.getMessageId())
                .text("Deleted from database")
                .build();
    }
}
