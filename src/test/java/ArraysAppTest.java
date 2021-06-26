import lesson6.ArraysApp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class ArraysAppTest {
    private ArraysApp arraysApp;

    @BeforeEach
    public void init(){
        ArraysApp arraysApp = new ArraysApp();
    }

    @Test
    @DisplayName("searchElementsAfterFour InputDataV1")
    void testInputDataV1(){

        int[] expected = new int[]{6, 5, 6, 7, 8};
        int[] actual = arraysApp.searchElementsAfterFour(new int[]{0, 5, 3, 4, 6, 5, 6, 7, 8});
        Assertions.assertArrayEquals(expected, actual);
    }

    @Test
    @DisplayName("searchElementsAfterFour InputDataV2")
    void testInputDataV2(){

        int[] expected = new int[]{7, 8};
        int[] actual = arraysApp.searchElementsAfterFour(new int[]{0, 5, 3, 4, 6, 5, 4, 7, 8});
        Assertions.assertArrayEquals(expected, actual);
    }

    @Test
    @DisplayName("searchElementsAfterFour InputDataV3")
    void testInputDataV3(){

        int[] source = new int[]{0, 1, 2, 7, 6, 5, 7, 8};
        Assertions.assertThrows(RuntimeException.class, () -> arraysApp.searchElementsAfterFour(source));
    }

    @Test
    @DisplayName("searchElementsAfterFour InputDataV4")
    void testInputDataV4(){

        int[] expected = new int[]{};
        int[] actual = arraysApp.searchElementsAfterFour(new int[]{0, 5, 8, 3, 4, 5, 6, 7, 4});
        Assertions.assertArrayEquals(expected, actual, "Array length is bigger than 0");
    }

    @Test
    @DisplayName("isArrayContainsFourOrOne InputDataV1")
    void testV1(){
        Assertions.assertTrue(arraysApp.isArrayContainsFourOrOne(new int[]{1, 1, 4, 4, 1, 4, 1}));
    }

    @Test
    @DisplayName("isArrayContainsFourOrOne InputDataV2")
    void testV2(){
        Assertions.assertFalse(arraysApp.isArrayContainsFourOrOne(new int[]{1, 1, 1, 1, 1}));
    }

    @Test
    @DisplayName("isArrayContainsFourOrOne InputDataV3")
    void testV3(){
        Assertions.assertFalse(arraysApp.isArrayContainsFourOrOne(new int[]{4, 4, 4, 4, 4, 4}));
    }

    @Test
    @DisplayName("isArrayContainsFourOrOne InputDataV4")
    void testV4(){
        Assertions.assertFalse(arraysApp.isArrayContainsFourOrOne(new int[]{1, 1, 3, 1, 1, 4, 1}));
    }

}
