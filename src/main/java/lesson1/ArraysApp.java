package lesson1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArraysApp {
    public static void main(String[] args) {
        Integer[] arrayInteger = {1, 2, 3};
        System.out.println(Arrays.toString(arrayInteger));
        System.out.println(Arrays.toString(swapArrayElements(arrayInteger, 0, 1)));

        String[] arrayString = {"a", "b", "c"};
        System.out.println(Arrays.toString(arrayString));
        System.out.println(Arrays.toString(swapArrayElements(arrayString, 0, 1)));

        System.out.println(arrayToList(arrayInteger));
        System.out.println(arrayToList(arrayString));
    }

    public static Object[] swapArrayElements(Object[] array, int a, int b){
        Object z = 0;
        z = array[a];
        array[a] = array[b];
        array[b] = z;
        return array;
    }

    public static List<?> arrayToList(Object[] array){
        return new ArrayList<>(Arrays.asList(array));
    }
}