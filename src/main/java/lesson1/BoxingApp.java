package lesson1;

public class BoxingApp {

    public static void main(String[] args) {
        Apple apple = new Apple(1);
        Orange orange = new Orange(1);
        Apple apple1 = new Apple(2);
        Orange orange1 = new Orange(2);
        Apple apple2 = new Apple(3);
        Orange orange2 = new Orange(3);
        Apple apple3 = new Apple(4);


        Box<Apple> box1 = new Box<>();
        box1.putFruitInABox(apple);
        box1.putFruitInABox(apple1);
        box1.putFruitInABox(apple2);
        box1.putFruitInABox(apple3);

        Box<Orange> box2 = new Box<>();
        box2.putFruitInABox(orange);
        box2.putFruitInABox(orange1);
        box2.putFruitInABox(orange2);


        System.out.println(box1.getWeight());
        System.out.println(box2.getWeight());

        System.out.println(box1.compareBoxes(box2));

        Box<Apple> box3 = new Box<>();
        Box<Orange> box4 = new Box<>();

        box1.shiftTheFruitInAnotherBox(box3);
        System.out.println(box3.getWeight());
        System.out.println(box1.getWeight());

        box2.shiftTheFruitInAnotherBox(box4);
        System.out.println(box4.getWeight());
        System.out.println(box2.getWeight());



    }
}
