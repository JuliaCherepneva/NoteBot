package pro.sky.telegrambot.services;

import com.pengrad.telegrambot.model.Update;
import pro.sky.telegrambot.model.NotificationTask;

import java.util.Collection;

public interface NotificationTaskService {
    NotificationTask add(Update update);
    Collection<NotificationTask> tasksNow();

}
