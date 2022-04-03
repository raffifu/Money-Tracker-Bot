package id.my.btw.service;

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
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
public class CallbackService {
    public static final String DELETE = "DELETE";
    public static final String CONFIRM_DELETE = "CONFIRM_DELETE";
    public static final String CANCEL_DELETE = "CANCEL_DELETE";
    public static final String EDIT_CATEGORY = "EDIT_CATEGORY";

    private final ExpenseRepository expenseRepository;

    public CallbackService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public void handleIncomingCallback(CallbackQuery callbackQuery, Consumer<? super BotApiMethod<? extends Serializable>> responseHandler) throws TelegramApiException {
        Message message = callbackQuery.getMessage();

        switch (callbackQuery.getData()) {
            case DELETE:
                responseHandler.accept(onDelete(message));
                break;
            case CONFIRM_DELETE:
                responseHandler.accept(onConfirmDelete(message));
                break;
            case CANCEL_DELETE:
                responseHandler.accept(onCancelDelete(message));
                break;
            case EDIT_CATEGORY:
                responseHandler.accept(onEditCategory(message));
                break;
            default:
                responseHandler.accept(onInputCategory(message, callbackQuery.getData()));
        }
    }

    private EditMessageText onDelete(Message message) {
        EditMessageText editMessage = ResponseUtil.genEditMessage(message);
        editMessage.setReplyMarkup(KeyboardUtil.confirmDeletePad());
        return editMessage;
    }

    private EditMessageText onConfirmDelete(Message message) throws TelegramApiException {
        Integer id = Optional
                .ofNullable(CommonUtil.getId(message.getText()))
                .orElseThrow(() -> new TelegramApiException("Invalid Message"));

        expenseRepository.delete(id);

        return ResponseUtil.genEditMessage(message, "Deleted from database");
    }

    private EditMessageText onCancelDelete(Message message) {
        EditMessageText editMessage = ResponseUtil.genEditMessage(message);
        editMessage.setReplyMarkup(KeyboardUtil.defaultPad());
        return editMessage;
    }

    private EditMessageText onEditCategory(Message message) {
        EditMessageText editMessage = ResponseUtil.genEditMessage(message);
        editMessage.setReplyMarkup(KeyboardUtil.categoryPad());
        return editMessage;
    }

    private EditMessageText onInputCategory(Message message, String callbackData) throws TelegramApiException {
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
}
