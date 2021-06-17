package lesson1;

import java.util.ArrayList;
import java.util.List;

public class Box <T extends Fruit> {
    private final List <T> fruits = new ArrayList<>();


    public void putFruitInABox(T fruit){
        fruits.add(fruit);
    }

    public void removeFruitFromBox(){
        fruits.clear();
    }

    public int getWeight(){
        return fruits.stream().mapToInt(Fruit::getWeight).sum();
    }

    public boolean compareBoxes (Box <?> box){
        return this.getWeight() == box.getWeight();
    }

    public void shiftTheFruitInAnotherBox(Box <T> box){
        fruits.forEach(box::putFruitInABox);
        this.removeFruitFromBox();
    }


}