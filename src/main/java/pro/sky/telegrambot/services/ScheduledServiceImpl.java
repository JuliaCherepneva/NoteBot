package pro.sky.telegrambot.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import java.util.Collection;

@Service
public class ScheduledServiceImpl implements ScheduledService {

    private final NotificationTaskService notificationTaskService;
    private final NotificationSenderService notificationSenderService;

    public ScheduledServiceImpl(NotificationTaskService notificationTaskService, NotificationSenderService notificationSenderService) {
        this.notificationTaskService = notificationTaskService;
        this.notificationSenderService = notificationSenderService;
    }

    Logger logger = LoggerFactory.getLogger(ScheduledServiceImpl.class);

    @Override
    @Scheduled(cron = "0 0/1 * * * *")
    public void run() {
        Collection<NotificationTask> tasks = notificationTaskService.tasksNow();
        for (NotificationTask task : tasks) {
            notificationSenderService.sendNotification(task.getChatId(), "✅Напоминаю: " + task.getText());
            logger.info("Sent a notification");
        }
    }
}
