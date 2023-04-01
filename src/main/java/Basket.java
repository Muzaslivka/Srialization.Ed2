import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class Basket {
    private String[] products;
    private int[] prices;
    private int[] countProduct;

    public Basket(String[] products, int[] prices) {
        this.products = products;
        this.prices = prices;
        this.countProduct = new int[products.length];
    }

    public void addToCart(int productNum, int amount) {
        countProduct[productNum - 1] += amount;
    }

    private int sumTotal() {
        int sum = 0;
        for (int j = 0; j < products.length; j++) {
            if (countProduct[j] != 0) {
                int sumProduct = prices[j] * countProduct[j];
                sum += sumProduct;
            }
        }
        return sum;
    }

    public void printCart() {
        System.out.println("Ваша корзина:");
        for (int i = 0; i < products.length; i++) {
            if (countProduct[i] != 0) {
                int sum = prices[i] * countProduct[i];
                System.out.println(products[i] + " (" + prices[i] + " руб/шт) - " + countProduct[i] + " шт (Итого: " + sum + " руб)");
            }
        }
        System.out.println("Итоговая сумма: " + sumTotal() + " руб");
    }

    public void printChoice() {
        System.out.println("Список продуктов для выбора:");
        for (int i = 0; i < products.length; i++) {
            System.out.println((i + 1) + ". " + products[i] + " " + prices[i] + " руб/шт");
        }
    }

    public void saveTxt(File textFile) {
        try (BufferedWriter outBuffer = new BufferedWriter(new FileWriter(textFile))) {
            for (int i = 0; i < products.length; i++) {
                outBuffer.write(products[i] + ";" + prices[i] + ";" + countProduct[i]);
                outBuffer.newLine();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void saveJson(File jsonFile) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        try (BufferedWriter wr = new BufferedWriter(new FileWriter(jsonFile))) {
            wr.write(gson.toJson(this));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Basket loadFromTxtFile(File textFile) {
        Basket basket = null;
        try (BufferedReader inBuffer = new BufferedReader(new FileReader(textFile))) {
            List<String> fileInfo = inBuffer.lines().collect(Collectors.toList());
            String[] fileProducts = new String[fileInfo.size()];
            int[] filePrices = new int[fileInfo.size()];
            int[] fileCount = new int[fileInfo.size()];
            for (int i = 0; i < fileInfo.size(); i++) {
                String[] fileLine = fileInfo.get(i).split(";");
                fileProducts[i] = fileLine[0];
                filePrices[i] = Integer.parseInt(fileLine[1]);
                fileCount[i] = Integer.parseInt(fileLine[2]);
            }
            basket = new Basket(fileProducts, filePrices);
            for (int j = 0; j < fileCount.length; j++) {
                basket.addToCart(j + 1, fileCount[j]);
            }
            System.out.println("У вас уже есть список покупок");
            basket.printCart();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return basket;
    }

    public static Basket loadFromJson(File jsonFile) {
        Basket basket = null;
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        try (BufferedReader rd = new BufferedReader(new FileReader(jsonFile))) {
            basket = gson.fromJson(rd, Basket.class);
            System.out.println("У вас уже есть список покупок");
            basket.printCart();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return basket;
    }
}
