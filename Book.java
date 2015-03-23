/* We created a book class to ease managing the objects
and make editing the stock easier. The book class holds
attributes such as price, quantity, etc. */

public class Book {
    private String title;
    private String topic;
    private int itemNum;
    private int quantity;
    private double price;

    public Book(String titlePassed, String topicPassed, int itemNumPassed, double pricePassed) {
        title = titlePassed;
        topic = topicPassed;
        itemNum = itemNumPassed;
        quantity = 1;
        price = pricePassed;
    }

    public int getItemNumber() {
        return itemNum;
    }

    public String getTopic() {
        return topic;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getTitleAndItemNum() {
        String deets = title + ", " + itemNum;
        return deets;
    }

    public void setCost(double newCost) {
        price = newCost;
    }

    public void updateQuantity(int changeQuantity) {
        int newQuantity = quantity + changeQuantity;
        if (newQuantity < 0) {
            quantity = 0;
        }
        else {
            quantity = newQuantity;
        }
    }

    public String getBookDetails() {
        String deets = title + "\n" + quantity + " in stock" + "\n$" + price;
        return deets;
    }
}