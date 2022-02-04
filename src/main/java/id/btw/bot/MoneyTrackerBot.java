package id.btw.bot;

import id.btw.payload.input.Expense;
import id.btw.utils.ResponseFormatter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.factory.Sets;
import org.eclipse.collections.impl.map.mutable.UnifiedMap;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Date;

@Slf4j
public class MoneyTrackerBot extends TelegramLongPollingBot {
    private final UnifiedMap<UnifiedMap<Long, Integer>, Expense> expenseData = UnifiedMap.newMap();

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

                CallbackQuery callback = update.getCallbackQuery();
                handleCallbackQuery(callback);
            }
        } catch (TelegramApiException e) {
            log.error("Error handling incoming message", e);
        }
    }

    private void handleCallbackQuery(CallbackQuery callback) throws TelegramApiException {
        String data = callback.getData();

        if (data.equals("save"))
            onSaveCallback(callback);
    }

    private void onSaveCallback(CallbackQuery callback) throws TelegramApiException {
        Long chatId = callback.getMessage().getChatId();
        Integer messageId = callback.getMessage().getMessageId();

        UnifiedMap<Long, Integer> key = UnifiedMap.newWithKeysValues(
                chatId,
                messageId
        );

        Expense messageData = expenseData.get(key);

        log.info("Data want to save {}", messageData);
        // TODO: Save to DB (Call From Service)

        expenseData.remove(key);
        EditMessageText editMessageText = EditMessageText.builder()
                .chatId(chatId.toString())
                .messageId(messageId)
                .text(callback.getMessage().getText() + "\n_Saved to DB_")
                .build();

        execute(editMessageText);
    }

    private void handleIncomingMessage(Message message) throws TelegramApiException {
        if (!message.isUserMessage())
            return;

        if (!message.isCommand())
            onTextMessage(message.getChatId(), message.getText());
    }

    private void onTextMessage(Long chatId, String message) throws TelegramApiException {
        MutableList<String> msgListMutable = Lists.mutable.of(message.split("\n"));

        if (msgListMutable.size() != 3) {
            log.error("Invalid input");
            return;
        }

        Expense expense = Expense.builder()
                .amount(Double.parseDouble(msgListMutable.get(0)))
                .name(msgListMutable.get(1))
                .description(msgListMutable.get(2))
                .date(new Date())
                .build();

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        MutableSet<ConstraintViolation<Expense>> violations = Sets.adapt(validator.validate(expense));

        if (violations.size() != 0) {
            violations.collect(ConstraintViolation::getMessage).forEach(log::error);
            return;
        }

        sendValidationSuccessMessage(chatId, expense);
    }

    private void sendValidationSuccessMessage(Long chatId, Expense expense) throws TelegramApiException {
        log.info("Sending validation success message");

        SendMessage payload = SendMessage.builder()
                .chatId(chatId.toString())
                .text(ResponseFormatter.onSucceedExpensePayload(expense))
                .build();

        InlineKeyboardButton saveButton = InlineKeyboardButton.builder()
                .text("Save")
                .callbackData("save")
                .build();

        InlineKeyboardButton cancelButton = InlineKeyboardButton.builder()
                .text("Cancel")
                .callbackData("cancel")
                .build();

        MutableList<InlineKeyboardButton> keyboardButtons = Lists.mutable.of(saveButton, cancelButton);

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboardRow(keyboardButtons)
                .build();

        payload.setReplyMarkup(markup);

        Message msgSend = execute(payload);
        log.info("Message id {} with Chat Id {} sent", msgSend.getMessageId(), msgSend.getChatId());

        expenseData.put(
                UnifiedMap.newWithKeysValues(
                        msgSend.getChatId(),
                        msgSend.getMessageId()
                ),
                expense
        );
    }
}
