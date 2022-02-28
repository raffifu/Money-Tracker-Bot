package id.my.btw.bot;

import com.vdurmont.emoji.EmojiManager;
import id.my.btw.entity.Expense;
import id.my.btw.repository.ExpenseRepository;
import id.my.btw.util.MessageUtil;
import id.my.btw.util.ResponseFormatter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.list.MutableList;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.ZoneId;

@Slf4j
public class MoneyTrackerBot extends TelegramLongPollingBot {

    private final String CALLBACK_DELETE_DATA = "DELETE";

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

        if (!message.isCommand()) {
            MutableList<String> msgListMutable = Lists.mutable.of(message.getText().split("\n"));

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

            execute(responseMessage(message, expense));
        }
    }

    private void handleIncomingCallback(CallbackQuery callbackQuery) throws TelegramApiException {
        Message message = callbackQuery.getMessage();

        if (CALLBACK_DELETE_DATA.equals(callbackQuery.getData())) {
            Integer id = MessageUtil.getId(message.getText());
            log.info("Deleting message with messageId {} and id {}", message.getMessageId(), id);

            if (id != null) {
                expenseRepository.delete(id);
                execute(responseDeleteCallback(callbackQuery.getMessage()));
            }
        }
    }

    private SendMessage responseMessage(Message message, Expense expense) {
        log.info("Sending validation success message");

        InlineKeyboardButton deleteButton = InlineKeyboardButton.builder()
                .text(EmojiManager.getForAlias("basket").getUnicode() + " Delete")
                .callbackData(CALLBACK_DELETE_DATA)
                .build();

        MutableList<InlineKeyboardButton> keyboardButtons = Lists.mutable.of(deleteButton);

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboardRow(keyboardButtons)
                .build();

        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text(ResponseFormatter.onSucceedExpensePayload(expense))
                .replyMarkup(markup)
                .replyToMessageId(message.getMessageId())
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
