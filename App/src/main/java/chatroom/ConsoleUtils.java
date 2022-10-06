package chatroom;

import java.util.Scanner;
import java.util.function.Predicate;

public class ConsoleUtils {
    static Scanner sc = new Scanner(System.in);
    public static boolean checkInt(String tmp){
        if(tmp == null) return false;
        try{
            Integer.parseInt(tmp);
        }catch (NumberFormatException nfe){
            return false;
        }
        return true;
    }
    public static int getCorrectInt(){
        String tmp = sc.next();
        if(checkInt(tmp)){
            sc.nextLine();
            return Integer.parseInt(tmp);
        }
        System.out.print("Please enter a valid number: ");
        return getCorrectInt();
    }
    public static int getCorrectInt(Predicate<Integer> predicate, String errorMessage){
        String tmp = sc.next();
        if(checkInt(tmp)){
            int parsed = Integer.parseInt(tmp);
            if(predicate.test(parsed)) {
                sc.nextLine();
                return Integer.parseInt(tmp);
            }
            else{
                System.out.println(errorMessage);
            }
        }
        System.out.print("Please enter a valid number: ");
        return getCorrectInt(predicate, errorMessage);
    }
    public static int askSingleInt(){
        System.out.print("Please specify value: ");
        return getCorrectInt();
    }
}
