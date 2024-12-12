package pro.sky.telegrambot.services;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationSenderServiceImpl implements NotificationSenderService {

    @Autowired
    private TelegramBot telegramBot;


    public void sendNotification(Long updateChatId, String note) {
        SendMessage request = new SendMessage(updateChatId, note);
        telegramBot.execute(request);
    }
}
