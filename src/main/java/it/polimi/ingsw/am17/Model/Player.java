package it.polimi.ingsw.am17.Model;

import it.polimi.ingsw.am17.Model.GameCard.GameCard;

import java.util.List;

enum Color{
    RED,
    BLUE,
    GREEN,
    PURPLE,
    YELLOW,
    PINK,
}
public class Player implements Subject{
    private String nickname;
    private int pp;
    private int food;
    private Color color;
    private PlayerRow row;
    private OfferingCard offeringCard;

    public void addPp(int quantity){}
    public void addFood(int quantity) {}


    public OfferingCard getOfferingCard() {
        return offeringCard;
    }
    public int getPp()
    {
        return pp;
    }
    public int getFood()
    {
        return food;
    }
    public Color getColor()
    {
        return color;
    }
    public void setColor(Color color) {
        this.color = color;
    }
    public void setNickname(String nickname)
    {
        this.nickname = nickname;
    }
    public String getNickname()
    {
        return nickname;
    }


    public void setOfferingCard(OfferingCard offeringCard) {
        //controllare che l'input sia valido
    }
    public void freeOfferingCard(){}
    public void playTurn(List<GameCard> cards){}

    @Override
    public void attach(Observer observer) {

    }

    @Override
    public void detach(Observer observer) {

    }

    @Override
    public void notifyObserver() {

    }
}
