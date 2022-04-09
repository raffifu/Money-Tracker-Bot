package id.my.btw;

import id.my.btw.bot.MoneyTrackerBot;
import id.my.btw.repository.ExpenseRepository;
import id.my.btw.repository.ExpenseRepositoryImpl;
import id.my.btw.service.CallbackService;
import id.my.btw.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.TimeZone;

import static org.quartz.CronScheduleBuilder.dailyAtHourAndMinute;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

@Slf4j
public class Application {
    public static void main(String[] args) {
        try {
            log.info("Bot started");
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

            MoneyTrackerBot moneyTrackerBot = registerBot(botsApi);
            runReminderJob(moneyTrackerBot);
        } catch (TelegramApiException | SchedulerException e) {
            e.printStackTrace();
        }
    }

    private static MoneyTrackerBot registerBot(TelegramBotsApi botsApi) throws TelegramApiException, SchedulerException {
        ExpenseRepository expenseRepository = new ExpenseRepositoryImpl();

        MessageService messageService = new MessageService(expenseRepository);
        CallbackService callbackService = new CallbackService(expenseRepository);
        MoneyTrackerBot moneyTrackerBot = new MoneyTrackerBot(messageService, callbackService);

        botsApi.registerBot(moneyTrackerBot);
        return moneyTrackerBot;
    }

    private static void runReminderJob(MoneyTrackerBot moneyTrackerBot) throws SchedulerException {
        Scheduler defaultScheduler = StdSchedulerFactory.getDefaultScheduler();

        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("bot", moneyTrackerBot);
        jobDataMap.put("creator", System.getenv("CREATOR_ID"));

        JobDetail reminderJob = newJob(ReminderJob.class)
                .withIdentity("reminderJob", "defaultGroup")
                .setJobData(jobDataMap)
                .build();

        Trigger cronJob = newTrigger()
                .withIdentity("cronTrigger", "defaultGroup")
                .withSchedule(dailyAtHourAndMinute(21, 00)
                        .inTimeZone(TimeZone.getTimeZone("Asia/Jakarta")))
                .build();

        defaultScheduler.scheduleJob(reminderJob, cronJob);
        defaultScheduler.start();
    }
}
