package lesson6;

import java.util.LinkedList;

public class TestList {
    public static void main(String[] args) {

        LinkedList<String> messagesList = new LinkedList<>();
        messagesList.add("1");
        messagesList.add("2");
        messagesList.add("3");
        messagesList.add("4");
        messagesList.add("5");

        System.out.println(messagesList);

        LinkedList<String> msgList = new LinkedList<>();
        msgList.addFirst("1");
        msgList.addFirst("2");
        msgList.addFirst("3");
        msgList.addFirst("4");
        msgList.addFirst("5");

        System.out.println(msgList);



       /* readFile("1");
        readFile("2");
        readFile("3");
        readFile("4");
        readFile("5");
        System.out.println(msgList);*/

     //   readFile();


    }

   /* private static void readFile(){
        LinkedList<String> msgList = new LinkedList<>();
        List<String> list = new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5"));
        int count = 0;
       *//* while (count < 3) {
            msgList.addFirst(msg);
            count++;
        } msgList.removeFirst();*//*
        while (true) {
            for (int i = 0; i < list.size(); i++) {
                if (count < 4) {
                    count++;
                    msgList.addLast(list.get(i));
                } else {
                    System.out.println(msgList + " - до обновления");
                    msgList.removeFirst();
                    count--;
                    System.out.println(msgList + " - после");
                }
            }

        }

            //msgList.removeFirst();
    }*/


}
