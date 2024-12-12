package pro.sky.telegrambot.services;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.exceptions.BadMessageException;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repositories.NotificationTaskRepository;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
public class NotificationTaskServiceImpl implements NotificationTaskService {
    private final NotificationTaskRepository notificationTaskRepository;

    public NotificationTaskServiceImpl(NotificationTaskRepository notificationTaskRepository) {
        this.notificationTaskRepository = notificationTaskRepository;
    }

    @Autowired
    private TelegramBot telegramBot;

    Logger logger = LoggerFactory.getLogger(NotificationTaskServiceImpl.class);


    @Override
    public NotificationTask add(Update update) throws BadMessageException {
        NotificationTask task = new NotificationTask();
        try {
            if (update.message() != null && update.message().text() != null) {
                String text = update.message().text();
                logger.debug("The message is not empty");

                Pattern pattern = Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{4}\\s\\d{2}:\\d{2})(\\s+)(.+)");
                Matcher matcher = pattern.matcher(text);

                if (matcher.matches()) {
                    logger.debug("String matches expected pattern");

                    // Извлечение даты и времени
                    String dateTimeString = matcher.group(1);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.ENGLISH);
                    LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);
                    task.setDateTime(dateTime);
                    logger.debug("Parsed date and time: " + dateTime);

                    //Извлечение уведомления
                    String note = matcher.group(3); //Получаем текст после даты и времени
                    task.setText(note);
                    logger.debug("Parsed note: " + note);

                    //Извлечение chatId
                    Long updateChatId = update.message().chat().id();
                    task.setChatId(updateChatId);
                    logger.debug("ChatId: " + updateChatId);
                }
            }
            return notificationTaskRepository.save(task);
        } catch (BadMessageException e) {
            logger.warn("Message does not match expected pattern");
            throw new BadMessageException("Не правильно составлено сообщение");
        }
    }

    @Override
    public Collection<NotificationTask> tasksNow () {
        LocalDateTime currentDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        return notificationTaskRepository.findByDateTime(currentDateTime);
    }
}
