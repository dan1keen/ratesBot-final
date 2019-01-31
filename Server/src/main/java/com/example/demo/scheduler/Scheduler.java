package com.example.demo.scheduler;

import com.example.demo.DemoApplication;
import com.example.demo.model.Item;
import com.example.demo.service.ItemServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;


@Component
public class Scheduler {
    private final ItemServiceImpl itemService;

    @Autowired
    public Scheduler(ItemServiceImpl itemService) {
        this.itemService = itemService;
    }
    @PostConstruct
    public void onStart() {

        readFromXmlFile();

    }
    //Метод запускается каждые 10 минут
    @Scheduled(cron = "0 0/10 * * * *")
    public void loadEveryTenMinutes() {

        readFromXmlFile();

    }

    @SuppressWarnings("Duplicates")
    private void readFromXmlFile(){
        SSLContext ctx = null;
        try {
            ctx = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            assert ctx != null;
            ctx.init(new KeyManager[0], new TrustManager[] {new DemoApplication.DefaultTrustManager()}, new SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        SSLContext.setDefault(ctx);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        Document doc = null;
        try {
            assert db != null;
            doc = db.parse(new URL("https://nationalbank.kz/rss/rates.xml").openStream());

        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }
        Item item = new Item();

        assert doc != null;
        NodeList nodes = doc.getElementsByTagName("channel");
        Element element = (Element) nodes.item(0);
        NodeList nodeList = element.getElementsByTagName("item");
        Element nodeElement = (Element) nodeList.item(2);
        NodeList description = nodeElement.getElementsByTagName("description");
        NodeList name = nodeElement.getElementsByTagName("title");
        NodeList change = nodeElement.getElementsByTagName("change");
        NodeList pubDate = nodeElement.getElementsByTagName("pubDate");
        Date calendar = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String published = simpleDateFormat.format(calendar);

        double descr_double = Double.parseDouble(description.item(0).getFirstChild().getTextContent());
        double change_double = Double.parseDouble(change.item(0).getFirstChild().getTextContent());
        item.setDescr(descr_double);
        item.setName(name.item(0).getFirstChild().getTextContent());
        item.setChange(change_double);
        item.setPubDate(pubDate.item(0).getFirstChild().getTextContent());
        item.setCreateDate(published);
        Item last = itemService.getLastByName(item.getName());


        //----------------------------------------------------------------------------------------------------------
        Item itemRUR = new Item();
        nodeElement = (Element) nodeList.item(0);
        description = nodeElement.getElementsByTagName("description");
        name = nodeElement.getElementsByTagName("title");
        change = nodeElement.getElementsByTagName("change");
        pubDate = nodeElement.getElementsByTagName("pubDate");
        calendar = new Date();
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        published = simpleDateFormat.format(calendar);

        double descrRUR_double = Double.parseDouble(description.item(0).getFirstChild().getTextContent());
        double changeRUR_double = Double.parseDouble(change.item(0).getFirstChild().getTextContent());
        itemRUR.setDescr(descrRUR_double);
        itemRUR.setName(name.item(0).getFirstChild().getTextContent());
        itemRUR.setChange(changeRUR_double);
        itemRUR.setPubDate(pubDate.item(0).getFirstChild().getTextContent());
        itemRUR.setCreateDate(published);
        Item lastRUR = itemService.getLastByName(itemRUR.getName());

        //----------------------------------------------------------------------------------------------------------
        Item itemEUR = new Item();
        nodeElement = (Element) nodeList.item(1);
        description = nodeElement.getElementsByTagName("description");
        name = nodeElement.getElementsByTagName("title");
        change = nodeElement.getElementsByTagName("change");
        pubDate = nodeElement.getElementsByTagName("pubDate");
        calendar = new Date();
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        published = simpleDateFormat.format(calendar);

        double descrEUR_double = Double.parseDouble(description.item(0).getFirstChild().getTextContent());
        double changeEUR_double = Double.parseDouble(change.item(0).getFirstChild().getTextContent());
        itemEUR.setDescr(descrEUR_double);
        itemEUR.setName(name.item(0).getFirstChild().getTextContent());
        itemEUR.setChange(changeEUR_double);
        itemEUR.setPubDate(pubDate.item(0).getFirstChild().getTextContent());
        itemEUR.setCreateDate(published);
        Item lastEUR = itemService.getLastByName(itemEUR.getName());



        if (itemService.exists() > 0) {
            if (itemEUR.getDescr() != lastEUR.getDescr() || !itemEUR.getPubDate().equals(lastEUR.getPubDate())) {
                itemService.save(itemEUR);
                System.out.println("EURO saved Value is: " + itemEUR.getDescr());
                System.out.println("Published date is: " + itemEUR.getCreateDate());
                //Иначе просто говорим что последнее изменение такое же что мы вытащили из json файла
            } else {
                System.out.println("EURO value is the same! Value is: " + itemEUR.getDescr());
            }
            if (itemRUR.getDescr()!=lastRUR.getDescr() || !itemRUR.getPubDate().equals(lastRUR.getPubDate())) {
                itemService.save(itemRUR);
                System.out.println("RUBLE saved Value is: " + itemRUR.getDescr());
                System.out.println("Published date is: " + itemRUR.getCreateDate());
                //Иначе просто говорим что последнее изменение такое же что мы вытащили из json файла
            } else {
                System.out.println("RUBLE value is the same! Value is: " + itemRUR.getDescr());
            }
            if (item.getDescr()!=last.getDescr() || !item.getPubDate().equals(last.getPubDate())) {
                itemService.save(item);
                System.out.println("DOLLAR saved Value is: " + item.getDescr());
                System.out.println("Published date is: " + item.getCreateDate());
                //Иначе просто говорим что последнее изменение такое же что мы вытащили из json файла
            } else {
                System.out.println("DOLLAR value is the same! Value is: " + item.getDescr());
            }
        }  else {
            itemService.save(itemEUR);
            itemService.save(itemRUR);
            itemService.save(item);
            System.out.println("EURO saved Value is: " + itemEUR.getDescr());
            System.out.println("Published date is: " + itemEUR.getCreateDate());
            System.out.println("RUBLE saved Value is: " + itemRUR.getDescr());
            System.out.println("Published date is: " + itemRUR.getCreateDate());
            System.out.println("DOLLAR saved Value is: " + item.getDescr());
            System.out.println("Published date is: " + item.getCreateDate());
        }



    }

}