package id.my.btw.service;

import id.my.btw.entity.Account;
import id.my.btw.entity.Button;
import id.my.btw.entity.Category;
import id.my.btw.entity.Expense;
import id.my.btw.repository.ExpenseRepository;
import id.my.btw.util.CommonUtil;
import id.my.btw.util.KeyboardUtil;
import id.my.btw.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.function.Consumer;


@Slf4j
public class CallbackService {
    private final ExpenseRepository expenseRepository;

    public CallbackService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public void handleIncomingCallback(CallbackQuery callbackQuery, Consumer<? super BotApiMethod<? extends Serializable>> responseHandler) throws TelegramApiException {
        Message message = callbackQuery.getMessage();

        log.info("[{}] - Handling Callback", callbackQuery.getData());

        String data = callbackQuery.getData();

        if (!isInButton(data)) {
            responseHandler.accept(onGeneralCallback(message, callbackQuery.getData()));
            return;
        }

        Button buttonCalled = Button.valueOf(data);

        switch (buttonCalled) {
            case DELETE:
                responseHandler.accept(onDelete(message));
                break;
            case YES:
                responseHandler.accept(onConfirmedDelete(message));
                break;
            case CANCEL:
                responseHandler.accept(onCancel(message));
                break;
            case EDIT:
                responseHandler.accept(onEdit(message));
                break;
            case CATEGORY:
                responseHandler.accept(onEditCategory(message));
                break;
            case DATE:
                responseHandler.accept(onEditDate(message));
                break;
            case ACCOUNT:
                responseHandler.accept(onEditAccount(message));
                break;
        }
    }

    private EditMessageText onEditAccount(Message message) {
        log.info("Show account pad");

        EditMessageText editMessage = ResponseUtil.genEditMessage(message);
        editMessage.setReplyMarkup(KeyboardUtil.accountPad());
        return editMessage;
    }

    private EditMessageText onEdit(Message message) {
        log.info("Show edit pad");

        EditMessageText editMessage = ResponseUtil.genEditMessage(message);
        editMessage.setReplyMarkup(KeyboardUtil.editPad());
        return editMessage;

    }

    private EditMessageText onDelete(Message message) {
        log.info("Show delete confirmation");

        EditMessageText editMessage = ResponseUtil.genEditMessage(message);
        editMessage.setReplyMarkup(KeyboardUtil.confirmDeletePad());
        return editMessage;
    }

    private EditMessageText onConfirmedDelete(Message message) throws TelegramApiException {
        log.info("Deleting expense: {}", message.getReplyToMessage().getMessageId());

        Integer id = Optional
                .ofNullable(CommonUtil.getId(message.getText()))
                .orElseThrow(() -> new TelegramApiException("Invalid Message"));

        expenseRepository.delete(id);

        return ResponseUtil.genEditMessage(message, "Deleted from database");
    }

    private EditMessageText onCancel(Message message) {
        log.info("Show default pad");

        EditMessageText editMessage = ResponseUtil.genEditMessage(message);
        editMessage.setReplyMarkup(KeyboardUtil.defaultPad());
        return editMessage;
    }

    private EditMessageText onEditCategory(Message message) {
        log.info("Show category pad");

        EditMessageText editMessage = ResponseUtil.genEditMessage(message);
        editMessage.setReplyMarkup(KeyboardUtil.categoryPad());
        return editMessage;
    }

    private EditMessageText onEditDate(Message message) throws TelegramApiException {
        log.info("Show date pad");

        Integer id = Optional
                .ofNullable(CommonUtil.getId(message.getText()))
                .orElseThrow(() -> new TelegramApiException("Invalid Message"));

        Expense expense = Optional
                .ofNullable(expenseRepository.getById(id))
                .orElseThrow(() -> new TelegramApiException("Invalid Message"));

        EditMessageText editMessage = ResponseUtil.genEditMessage(message);
        editMessage.setReplyMarkup(KeyboardUtil.datePad(expense.getDate()));
        return editMessage;
    }

    private EditMessageText onGeneralCallback(Message message, String callbackData) throws TelegramApiException {
        log.info("Callback Data not found, handling as general callback");

        EditMessageText editMessage = null;

        if (isInCategory(callbackData)) {
            editMessage = onInputCategory(message, callbackData);
        } else if (isDate(callbackData)) {
            editMessage = onInputDate(message, callbackData);
        } else if (isInAccount(callbackData)) {
            editMessage = onInputAccount(message, callbackData);
        }

        return Optional
                .ofNullable(editMessage)
                .orElseThrow(() -> new TelegramApiException("Invalid Callback"));
    }

    private EditMessageText onInputDate(Message message, String callbackData) throws TelegramApiException {
        log.info("Input date: {}", callbackData);

        Integer id = Optional
                .ofNullable(CommonUtil.getId(message.getText()))
                .orElseThrow(() -> new TelegramApiException("Invalid Message"));

        Expense expense = Optional
                .ofNullable(expenseRepository.getById(id))
                .orElseThrow(() -> new TelegramApiException("Invalid Message"));

        LocalDate newDate = LocalDate.parse(callbackData);
        if (newDate.isAfter(LocalDate.now())) {
            log.error("Date is in the future");
            throw new TelegramApiException("Invalid Date");
        }

        expense.setDate(newDate);
        expenseRepository.update(expense);

        EditMessageText editMessage = ResponseUtil.genEditMessage(message, ResponseUtil.genResultMessage(expense));
        editMessage.setReplyMarkup(KeyboardUtil.defaultPad());
        return editMessage;
    }

    private EditMessageText onInputCategory(Message message, String callbackData) throws TelegramApiException {
        log.info("Input category: {}", callbackData);

        Category category = Category.valueOf(callbackData);
        Integer id = Optional
                .ofNullable(CommonUtil.getId(message.getText()))
                .orElseThrow(() -> new TelegramApiException("Invalid Message"));

        Expense expense = Optional
                .ofNullable(expenseRepository.getById(id))
                .orElseThrow(() -> new TelegramApiException("Invalid Message"));

        expense.setCategory(category.name());
        expenseRepository.update(expense);

        EditMessageText editMessage = ResponseUtil.genEditMessage(message, ResponseUtil.genResultMessage(expense));
        editMessage.setReplyMarkup(KeyboardUtil.defaultPad());
        return editMessage;
    }

    private EditMessageText onInputAccount(Message message, String callbackData) throws TelegramApiException {
        log.info("Input account: {}", callbackData);

        Account account = Account.valueOf(callbackData);

        Integer id = Optional
                .ofNullable(CommonUtil.getId(message.getText()))
                .orElseThrow(() -> new TelegramApiException("Invalid Message"));

        Expense expense = Optional
                .ofNullable(expenseRepository.getById(id))
                .orElseThrow(() -> new TelegramApiException("Invalid Message"));

        expense.setAccount(account.name());
        expenseRepository.update(expense);

        EditMessageText editMessage = ResponseUtil.genEditMessage(message, ResponseUtil.genResultMessage(expense));
        editMessage.setReplyMarkup(KeyboardUtil.defaultPad());
        return editMessage;
    }

    private Boolean isDate(String date) {
        try {
            LocalDate.parse(date);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private Boolean isInCategory(String data) {
        for (Category category : Category.values()) {
            if (category.name().equals(data)) {
                return true;
            }
        }
        return false;
    }

    private Boolean isInAccount(String data) {
        for (Account account : Account.values()) {
            if (account.name().equals(data)) {
                return true;
            }
        }
        return false;
    }

    private Boolean isInButton(String data) {
        for (Button account : Button.values()) {
            if (account.name().equals(data)) {
                return true;
            }
        }
        return false;
    }
}
