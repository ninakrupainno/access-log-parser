import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Введите первое число:");
        int number = new Scanner(System.in).nextInt();
        System.out.println("Введите второе число:");
        int number2 = new Scanner(System.in).nextInt();
        System.out.println("сумма: " + (number + number2));
        System.out.println("разность: " + (number - number2));
        System.out.println("произведение: " + (number * number2));
        double quotient = (double) number / number2;
        System.out.println("частное: " + quotient);
    }
}
