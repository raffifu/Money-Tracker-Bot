package id.my.btw.bot;

import id.my.btw.CallbackData;
import id.my.btw.entity.Category;
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

import java.util.Optional;

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

            Integer id = Optional
                    .ofNullable(CommonUtil.getId(message.getText()))
                    .orElseThrow(() -> new TelegramApiException("Invalid Message"));

            Expense expense = Optional
                    .ofNullable(CommonUtil.getExpense(message))
                    .orElseThrow(() -> new TelegramApiException("Cannot parse message"));
            expense.setId(id);

            expenseRepository.update(expense);

            EditMessageText editMessage = baseEditMessage(message, ResponseFormatter.onSucceedExpensePayload(expense));
            editMessage.setMessageId(message.getReplyToMessage().getMessageId());
            execute(editMessage);

            SendMessage sendMessage = baseSendMessage(message, "Success, update in database");
            execute(sendMessage);
        } else if (message.isCommand()) {
            // TODO: Handle command
            log.info("Received command: {}", message.getText());
        } else {
            Expense expense = CommonUtil.getExpense(message);
            log.info("Add new expense");

            expenseRepository.insert(expense);

            SendMessage sendMessage = baseSendMessage(message, ResponseFormatter.onSucceedExpensePayload(expense));
            sendMessage.setReplyMarkup(KeyboardUtil.categoryPad());
            execute(sendMessage);
        }

    }

    private void handleIncomingCallback(CallbackQuery callbackQuery) throws TelegramApiException {
        Message message = callbackQuery.getMessage();

        if (CallbackData.DELETE.equals(callbackQuery.getData())) {
            EditMessageText editMessage = baseEditMessage(message);
            editMessage.setReplyMarkup(KeyboardUtil.confirmDeletePad());
            execute(editMessage);
        } else if (CallbackData.CONFIRM_DELETE.equals(callbackQuery.getData())) {
            Integer id = Optional
                    .ofNullable(CommonUtil.getId(message.getText()))
                    .orElseThrow(() -> new TelegramApiException("Invalid Message"));

            log.info("Deleting message with messageId {} and id {}", message.getMessageId(), id);

            expenseRepository.delete(id);

            EditMessageText editMessage = baseEditMessage(message, "Deleted from database");
            execute(editMessage);

        } else if (CallbackData.CANCEL_DELETE.equals(callbackQuery.getData())) {
            EditMessageText editMessage = baseEditMessage(message);
            editMessage.setReplyMarkup(KeyboardUtil.defaultPad());
            execute(editMessage);
        } else if (CallbackData.EDIT_CATEGORY.equals(callbackQuery.getData())) {
            EditMessageText editMessage = baseEditMessage(message);
            editMessage.setReplyMarkup(KeyboardUtil.categoryPad());
            execute(editMessage);
        } else {
            Category category = Category.valueOf(callbackQuery.getData());
            Integer id = Optional
                    .ofNullable(CommonUtil.getId(message.getText()))
                    .orElseThrow(() -> new TelegramApiException("Invalid Message"));

            log.info("Set Category to {} for message with id {}", category, id);

            Expense expense = Optional
                    .ofNullable(expenseRepository.getById(id))
                    .orElseThrow(() -> new TelegramApiException("Invalid Message"));

            expense.setCategory(category.name());
            expenseRepository.update(expense);

            EditMessageText editMessage = baseEditMessage(message, ResponseFormatter.onSucceedExpensePayload(expense));
            editMessage.setReplyMarkup(KeyboardUtil.defaultPad());
            execute(editMessage);
        }
    }

    private SendMessage baseSendMessage(Message message, String text) {
        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .replyToMessageId(message.getMessageId())
                .text(text)
                .parseMode(ParseMode.MARKDOWN)
                .build();
    }

    private EditMessageText baseEditMessage(Message message) {
        return baseEditMessage(message, message.getText());
    }

    private EditMessageText baseEditMessage(Message message, String text) {
        return EditMessageText.builder()
                .chatId(message.getChatId().toString())
                .messageId(message.getMessageId())
                .text(text)
                .parseMode(ParseMode.MARKDOWN)
                .build();
    }
}
