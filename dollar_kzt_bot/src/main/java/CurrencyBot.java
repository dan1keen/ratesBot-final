import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CurrencyBot extends TelegramLongPollingBot {
    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    private String rate_title = "";
    private String description ="";
    private String title ="";
    private String change ="";
    private String pubDate ="";

    public void onUpdateReceived(Update update) {
        Properties props = new Properties();
        if (update.hasMessage() && update.getMessage().hasText()) {
            try {
                props.load(this.getClass().getResourceAsStream("application.properties"));
                String driver = props.getProperty("jdbc.driver");
                if (driver != null) { Class.forName(driver) ; }
                String url = props.getProperty("jdbc.url");
                String username = props.getProperty("jdbc.username");
                String password = props.getProperty("jdbc.password");
                conn = DriverManager.getConnection(url,username,password);
                stmt = conn.createStatement();
                String message_text = update.getMessage().getText();
                long chat_id = update.getMessage().getChatId();

                if(message_text.equals("hide")){
                    SendMessage message = new SendMessage().setChatId(chat_id).setText("Меню Закрылось");
                    ReplyKeyboardRemove keyboardMarkup = new ReplyKeyboardRemove();
                    message.setReplyMarkup(keyboardMarkup);
                    try { execute(message); } catch (TelegramApiException e) { e.printStackTrace(); }
                }else if(message_text.equals("USD") || message_text.equals("RUR") || message_text.equals("EUR")) {
                    switch(message_text){
                        case "USD": rate_title = "USD"; break;
                        case "RUR": rate_title = "RUR"; break;
                        case "EUR": rate_title = "EUR"; break;
                        default: System.out.println("No such rate"); break;
                    }
                    query(rate_title);
                    SendMessage message = new SendMessage().setChatId(chat_id).setText(getTemplate());
                    try { execute(message); }
                    catch (TelegramApiException e) { e.printStackTrace(); }
                }else {
                    SendMessage message = new SendMessage() // Create a message object object
                            .setChatId(chat_id)
                            .setText("Выберите курс валют");
                    ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
                    List<KeyboardRow> keyboard = new ArrayList<>();
                    KeyboardRow row = new KeyboardRow();
                    row.add("USD");
                    row.add("EUR");
                    row.add("RUR");
                    keyboard.add(row);
                    row = new KeyboardRow();
                    row.add("hide");
                    keyboard.add(row);
                    keyboardMarkup.setKeyboard(keyboard);
                    message.setReplyMarkup(keyboardMarkup);
                    try { execute(message); } catch (TelegramApiException e) { e.printStackTrace(); }
                }
            } catch (SQLException | ClassNotFoundException | IOException e) { e.printStackTrace(); }
            finally {
                try {
                    if (rs != null) { rs.close(); }
                    if (stmt != null) { stmt.close(); }
                    if (conn != null) { conn.close(); }
                }catch (Exception e) { e.printStackTrace(); }
            }
        }
    }

    private void query(String rate_name) throws SQLException {
        final String sql = "SELECT * FROM item WHERE item_name = ? ORDER BY item_id DESC LIMIT 1";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1,rate_name);
        rs = preparedStatement.executeQuery();
        while (rs.next()) {
            title = rs.getString("item_name");
            description = rs.getString("item_descr");
            change = rs.getString("item_change");
            pubDate = rs.getString("item_date");
        }
    }

    private String getTemplate(){
        return "1 (" +
                title + ") равeн " +
                description + " (KZT) \n" +
                "Изменения: " + change + "\n" +
                "Национальный Банк Казахстана " + "\n" +
                pubDate;
    }


    public String getBotUsername() { return "currency_bot"; }

    public String getBotToken() { return "773777009:AAGMOpAw93kRr1IgwPM0hFdWDZOAItxBtj8"; }


}
