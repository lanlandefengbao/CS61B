import java.util.Arrays;
import java.util.Objects;

public class Dog {
    private String[] dogs;

    public Dog() {
        dogs = new String[]{"a", "b"};
    }

//    @Override
//    public boolean equals(Object o) {
//        if (o instanceof Dog dog) {
//
//        }
//    }

    public static void main(String[] arg) {
        Dog d = new Dog();
        Dog d2 = new Dog();
        System.out.println(d.equals(d2));
    }
}