package aradite;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Test {

    public static void main(String[] args) {
        List<People> peopleList = new ArrayList<>();
        peopleList.add(new People("phong"));
        peopleList.add(new People("tez"));
        peopleList.add(new People("khanh"));

        System.out.println(peopleList);

        System.out.println(peopleList.stream().map(People::getName).collect(Collectors.toList()));
    }

    static class People {
        String name;

        People(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "@{People=" + name + "}";
        }
    }

}
