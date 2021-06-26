package lesson7;

public class TestClassTest {

    @BeforeSuite
    void testBeforeSuite (){
        System.out.println("testBeforeSuite - done");
    }

    @Test (priority = 1)
    void test1 (){
        System.out.println("test1 - done");
    }

    @Test (priority = 2)
    void test2 (){
        System.out.println("test2 - done");
    }

    @Test (priority = 3)
    void test3 (){
        System.out.println("test3 - done");
    }

    @Test (priority = 4)
    void test4 (){
        System.out.println("test4 - done");
    }

    @AfterSuite
    void testAfterSuite (){
        System.out.println("testAfterSuite - done");
    }
}
