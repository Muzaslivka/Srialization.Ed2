import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.Scanner;

public class Main {

    static boolean loadFileEnabled;
    static String loadFileName;
    static String loadFileFormat;
    static boolean saveFileEnabled;
    static String saveFileName;
    static String saveFileFormat;
    static boolean logFileEnabled;
    static String logFileName;

    public static void main(String[] args) {

        final String[] products = {"Молоко", "Хлеб", "Сливочное масло", "Макароны", "Творог", "Мука"};

        final int[] prices = {80, 45, 120, 62, 88, 74};

        Scanner sc = new Scanner(System.in);
        final File fileXml = new File("shop.xml");
        Basket basket;
        ClientLog clientLog = new ClientLog();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document docSettings = builder.parse(fileXml);
            loadFileEnabled = valueSetting(docSettings, "load", "enabled").equals("true");
            loadFileName = valueSetting(docSettings, "load", "fileName");
            loadFileFormat = valueSetting(docSettings, "load", "format");
            saveFileEnabled = valueSetting(docSettings, "save", "enabled").equals("true");
            saveFileName = valueSetting(docSettings, "save", "fileName");
            saveFileFormat = valueSetting(docSettings, "save", "format");
            logFileEnabled = valueSetting(docSettings, "log", "enabled").equals("true");
            logFileName = valueSetting(docSettings, "log", "fileName");
        } catch (ParserConfigurationException | IOException | SAXException e) {
            System.out.println("Файл с настройками прочитать не удалось, взяты настройки по умолчанию");
        }

        if (loadFileEnabled) {
            File fileBasket = new File(loadFileName);
            if (fileBasket.canRead() && !isEmptyFile(fileBasket)) {
                try {
                    basket = loadFileFormat.equals("txt") ? Basket.loadFromTxtFile(fileBasket) : Basket.loadFromJson(fileBasket);
                } catch (Exception e) {
                    System.out.println("Неверный формат файла. Будет создана пустая корзина");
                    basket = new Basket(products, prices);
                }
            } else {
                System.out.println("У вас пустая корзина");
                basket = new Basket(products, prices);
            }
        } else {
            System.out.println("У вас пустая корзина");
            basket = new Basket(products, prices);
        }

        basket.printChoice();

        while (true) {
            System.out.println("Введите номер продукта и его количество через пробел или end для выхода:");
            String input = sc.nextLine();
            if (input.equals("end")) {
                basket.printCart();
                if (logFileEnabled) {
                    File fiLeLog = new File(logFileName);
                    clientLog.exportAsCSV(fiLeLog);
                }
                break;
            }
            String[] foodBasket = input.split(" ");
            if (foodBasket.length != 2) {
                System.out.println("Неверный ввод: введено не 2 значения. Попробуйте еще раз");
            } else {
                int numberProduct;
                try {
                    numberProduct = Integer.parseInt(foodBasket[0]);
                } catch (NumberFormatException exception) {
                    System.out.println("Вы ввели не число. Попробуйте еще раз");
                    continue;
                }
                if (numberProduct > products.length || numberProduct <= 0) {
                    System.out.println("Неверный номер продукта. Попробуйте еще раз");
                } else {
                    int count;
                    try {
                        count = Integer.parseInt(foodBasket[1]);
                    } catch (NumberFormatException exception) {
                        System.out.println("Вы ввели не число. Попробуйте еще раз");
                        continue;
                    }
                    if (count < 0) {
                        System.out.println("Введено отрицательное количество. Попробуйте еще раз");
                    } else {
                        basket.addToCart(numberProduct, count);
                        if (saveFileEnabled) {
                            File fileBasket = new File(saveFileName);
                            if (saveFileFormat.equals("txt")) {
                                basket.saveTxt(fileBasket);
                            } else {
                                basket.saveJson(fileBasket);
                            }
                        }

                        clientLog.log(numberProduct, count);
                    }
                }
            }
        }
    }

    private static boolean isEmptyFile(File textFile) {
        try (BufferedReader inBuffer = new BufferedReader(new FileReader(textFile))) {
            return inBuffer.readLine() == null;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    private static String valueSetting(Document doc, String nameRootSetting, String nameSetting) {
        String name = "";
        NodeList loadSettingList = doc.getElementsByTagName(nameRootSetting);
        Node loadSettingNode = loadSettingList.item(0);
        if (loadSettingNode.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) loadSettingNode;
            NodeList nd = element.getElementsByTagName(nameSetting).item(0).getChildNodes();
            Node node = nd.item(0);
            name = node.getNodeValue();
        }
        return name;
    }
}