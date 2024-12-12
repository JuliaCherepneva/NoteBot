package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.services.NotificationTaskService;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final NotificationTaskService notificationTaskService;

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    public TelegramBotUpdatesListener(NotificationTaskService notificationTaskService) {
        this.notificationTaskService = notificationTaskService;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            String text = update.message().text();
            Long chatId = update.message().chat().id();

            if (text.equals("/start")) {
                String start = "Добро пожаловать\uD83D\uDC4B Я могу напомнить выполнить любую задачу! \nВы можете написать дату, время и текст напоминания в формате: ДД.ММ.ГГГГ ЧЧ:ММ в одном сообщении \uD83D\uDCDD \nНапример: \n\n06.12.2024 15:30 Купить хлеб \n\nА далее дело за мной \uD83D\uDE09 \nВ указанную дату и время вы получите от меня уведомление\uD83D\uDC4D";
                SendResponse response = telegramBot.execute(new SendMessage(chatId, start));
            } else {
                notificationTaskService.add(update);
                String receivedMessage = "Ваше сообщение принято, ожидайте напоминания \uD83D\uDE0A";
                SendResponse response = telegramBot.execute(new SendMessage(chatId, receivedMessage));
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}
