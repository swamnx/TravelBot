package com.project.bot;

import com.project.model.City;
import com.project.repository.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@PropertySource("classpath:telegram.properties")
public class TravelBot extends TelegramLongPollingBot {

    @Autowired
    private CityRepository cityRepository;

    @Value("${bot.name}")
    private String botUsername;

    @Value("${bot.token}")
    private String botToken;

    @Value("${bot.messageStart}")
    private String messageStart;

    @Value("${bot.messageHelp}")
    private String messageHelp;

    @Value("${bot.messageInfoEmpty}")
    private String messageInfoEmpty;

    @Value("${bot.messageNotFoundCity}")
    private String messageNotFoundCity;

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String sendMessageText = createSendMessageText(update.getMessage().getText());
            if(sendMessageText != null) {
                SendMessage message = new SendMessage()
                        .setChatId(update.getMessage().getChatId())
                        .setReplyToMessageId(update.getMessage().getMessageId())
                        .setText(sendMessageText);
                try {
                    sendApiMethod(message);
                }
                catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String createSendMessageText(String updateMessageText){
        switch (updateMessageText){
            case "/start":
                return messageStart ;
            case "/help":
                return messageHelp;
            case "/info":
                return messageInfoEmpty;
            default:
                String cityName = parseUpdateMessageTextFromChats(updateMessageText);
                if(cityName == null) return null;
                City city = cityRepository.findCityByName(cityName);
                return city == null ? messageNotFoundCity : city.getDescription();
        }
    }

    private String parseUpdateMessageTextFromChats(String updateMessageText){
        String messageRegex = "^/info(?:@"+getBotUsername()+")*(?:\\s*)(.*)$";
        Matcher matcher = Pattern.compile(messageRegex).matcher(updateMessageText);
        return matcher.matches() ? matcher.group(1) : null;
    }
}
