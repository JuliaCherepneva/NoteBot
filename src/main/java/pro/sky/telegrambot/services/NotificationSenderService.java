package pro.sky.telegrambot.services;

public interface NotificationSenderService {
   void sendNotification(Long updateChatId, String note);
}


