package id.my.btw;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class ReminderJob implements Job {
    public static AbsSender bot;
    public static String creator;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap dataMap = jobExecutionContext.getMergedJobDataMap();

        SendMessage reminder = SendMessage.builder()
                .chatId(creator)
                .text("Don't forget to add your expenses!")
                .build();

        try {
            bot.execute(reminder);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setBot(AbsSender bot) {
        this.bot = bot;
    }
}
