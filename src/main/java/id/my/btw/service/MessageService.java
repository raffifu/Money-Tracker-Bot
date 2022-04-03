package id.my.btw.service;

import id.my.btw.entity.Expense;
import id.my.btw.repository.ExpenseRepository;
import id.my.btw.util.CommonUtil;
import id.my.btw.util.KeyboardUtil;
import id.my.btw.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
public class MessageService {
    private final ExpenseRepository expenseRepository;

    public MessageService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public void handleIncomingMessage(Message message, Consumer<? super BotApiMethod<? extends Serializable>> responseHandler) throws TelegramApiException {
        if (!message.isUserMessage())
            return;

        if (message.isReply()) {
            log.info("Received reply, editing expense");

            responseHandler.accept(onReply(message));
            responseHandler.accept(onReplyConfirmation(message));
        } else if (message.isCommand()) {
            // TODO: Handle command
            log.info("Received command");
        } else {
            log.info("Received general message, add new expense");

            responseHandler.accept(onNewExpense(message));
        }
    }

    private EditMessageText onReply(Message message) throws TelegramApiException {
        Integer id = Optional
                .ofNullable(CommonUtil.getId(message.getReplyToMessage().getText()))
                .orElseThrow(() -> new TelegramApiException("Invalid Message"));

        Expense newExpense = Optional
                .ofNullable(CommonUtil.getExpense(message))
                .orElseThrow(() -> new TelegramApiException("Cannot parse message"));

        Expense oldExpense = Optional
                .ofNullable(expenseRepository.getById(id))
                .orElseThrow(() -> new TelegramApiException("Invalid Message"));

        oldExpense.setAmount(newExpense.getAmount());
        oldExpense.setNote(newExpense.getNote());

        expenseRepository.update(oldExpense);

        EditMessageText editMessage = ResponseUtil.genEditMessage(message, ResponseUtil.genResultMessage(oldExpense));
        editMessage.setMessageId(message.getReplyToMessage().getMessageId());
        editMessage.setReplyMarkup(KeyboardUtil.defaultPad());
        return editMessage;
    }

    private SendMessage onReplyConfirmation(Message message) {
        SendMessage sendMessage = ResponseUtil.genSendMessage(message, "Success Update expense");
        sendMessage.setReplyToMessageId(message.getReplyToMessage().getMessageId());
        return sendMessage;
    }

    private SendMessage onNewExpense(Message message) throws TelegramApiException {
        Expense expense = Optional
                .ofNullable(CommonUtil.getExpense(message))
                .orElseThrow(() -> new TelegramApiException("Cannot parse message"));

        expenseRepository.insert(expense);

        SendMessage sendMessage = ResponseUtil.genSendMessage(message, ResponseUtil.genResultMessage(expense));
        sendMessage.setReplyMarkup(KeyboardUtil.categoryPad());
        return sendMessage;
    }
}
