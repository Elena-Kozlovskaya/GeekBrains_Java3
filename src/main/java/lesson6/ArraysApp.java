package lesson6;

import java.util.Arrays;

public class ArraysApp {
    /*public static void main(String[] args) {
       int [] array = new int []{4, 4, 4, 4, 4};
       try {
           System.out.println(Arrays.toString(searchElementsAfterFour(array)));
       } catch (RuntimeException e){
           e.printStackTrace();
       }

      System.out.println(isArrayContainsFourOrOne(array));
    }*/

    /**
     * Метод должен вернуть новый массив, который получен путем вытаскивания из исходного массива элементов,
     * идущих после последней четверки. Входной массив должен содержать хотя бы одну четверку,
     * иначе в методе необходимо выбросить RuntimeException.
     * @param array
     * @return new int[] newArray
     */
    public static int[] searchElementsAfterFour(int[] array) {
        int[] newArray;
        int lastIndex = 0;
        int element = 4;
        for(int i = array.length - 1; i >= 0; i--){
            if(array[i] == element){
                lastIndex = i;
                break;
            }
        }
        if(lastIndex == 0) {
            throw new RuntimeException("Array does not contains element " + element);
        }
        newArray = new int[array.length - lastIndex - 1];
        System.arraycopy(array, lastIndex + 1, newArray, 0, newArray.length);
        return newArray;
    }

    /**
     * Проверяет состав массива из чисел 1 и 4. Если хоть один элемент не равен четверке или единице, то метод вернет false.
     * Если массив состоит только из 1 или только из 4, то метод вернет false.
     * @param array
     * @return true
     */

    public static boolean isArrayContainsFourOrOne(int[] array) {
        boolean firstElement = false;
        boolean secondElement = false;
        for (int i : array) {

            if (i == 1) {
                firstElement = true;
            } else if (i == 4) {
                secondElement = true;
            } else {
                return false;
            }
        }
        return (firstElement & secondElement);
    }
}
