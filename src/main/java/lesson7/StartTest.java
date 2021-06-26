package lesson7;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class StartTest {

    public static void main(String[] args) {
        start(TestClassTest.class);
}


    public static void start(Class testClass) {
        Method[] methods = testClass.getDeclaredMethods();

        List<Method> sortedPriority = new ArrayList<>();
        Method beforeSuit = null;
        Method afterSuit = null;
        int countBeforeSuit = 0;
        int countAfterSuit = 0;

        for (Method m : methods) {
            if(m.getAnnotation(Test.class) != null) {
                sortedPriority.add(m);
            }
            if(m.getAnnotation(BeforeSuite.class) != null) {
                beforeSuit = m;
                countBeforeSuit++;
            }
            if(m.getAnnotation(AfterSuite.class) != null) {
                afterSuit = m;
                countAfterSuit++;
            }
        }

        if(countAfterSuit > 1 & countBeforeSuit > 1){
            throw new RuntimeException("Methods with annotations @BeforeSuite and @AfterSuite are not unique");
        }
        sortedPriority.sort(Comparator.comparingInt(m -> m.getAnnotation(Test.class).priority()));
        sortedPriority.add(0, beforeSuit);
        sortedPriority.add(afterSuit);


        try {
            Object object = testClass.getDeclaredConstructor().newInstance();

            for(Method m : sortedPriority){
                m.invoke(object);
            }
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
