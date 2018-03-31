package com.example.jdk8.demo.sevice;

import com.alibaba.fastjson.JSON;
import com.example.jdk8.demo.entity.Student;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SteamApiTest {

    public static void main(String[] args) {
        SteamApiTest steamApiTest = new SteamApiTest();

        //流的创建方式
        //第一种 通过Stream接口的of静态方法创建一个流
        Stream<String> stream = Stream.of("hello", "world", "helloworld");
        //第二种 通过Arrays类的stream方法，实际上第一种of方法底层也是调用的Arrays.stream(values);
        String[] array = new String[]{"hello","world","helloworld"};

        Stream<String> stream3 = Arrays.stream(array);
        //第三种 通过集合的stream方法，该方法是Collection接口的默认方法，所有集合都继承了该方法
        Stream<String> stream2 = Arrays.asList("hello","world","helloworld").stream();

        steamApiTest.test0();
        stream.peek(System.out::println).peek(s->System.out.println(s.toUpperCase())).peek(s->System.out.println(s.concat("qqq")));
        System.out.println("-------------------");
        stream2.sequential().forEach(System.out::println);
        System.exit(-1);

//        stream2.parallel().forEach(System.out::println);
        stream2.parallel().forEachOrdered(System.out::println);


        Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9).parallelStream().forEach(System.out::println);
        System.out.println("----------------------------------------------------------------");
        Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9).parallelStream().forEachOrdered(System.out::println);


        steamApiTest.test1();
        steamApiTest.test2();
        steamApiTest.test3();
        steamApiTest.test4();
    }

    public void test0() {
        List<Double> list = new ArrayList<Double>();
        for(int i=0;i<10000000;i++){
            double d = Math.random() * 1000;
            list.add(d);
        }
        long start = System.nanoTime();
        list = list.stream().sequential().sorted().collect(Collectors.toList());
        long end = System.nanoTime();
        list = list.stream().parallel().sorted().collect(Collectors.toList());
        long parTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime()-end);//得到并行排序所用的时间
        long seqTime = TimeUnit.NANOSECONDS.toMillis(end-start);//得到串行排序所用的时间
        System.out.println("并行时间:"+parTime+";串行时间:"+seqTime);
    }

    public void test1() {
        List<String> list = Arrays.asList("hello", "world", "helloworld");
        List<String> stringList = list.stream().map(s -> s.toUpperCase()).collect(Collectors.toList());
        Set<String> stringSet = list.stream().map(s -> s.toUpperCase()).collect(Collectors.toSet());
        list.stream().map(s -> s.toUpperCase()).collect(Collectors.toCollection(TreeSet::new));

    }

    public void test2() {
        Stream<List<Integer>> listStream = Stream.of(Arrays.asList(1), Arrays.asList(2, 3), Arrays.asList(4, 5, 6));
        listStream.flatMap(list->list.stream()).map(i->i*i).forEach(System.out::println);
    }

    public void test3() {
        //同时获取最大 最小 平均值等信息
        List<Integer> list = Arrays.asList(1, 3, 5, 7, 9, 11);
        list.stream().filter(integer -> integer>2).mapToInt(i->i*2).skip(2).limit(2).forEach(System.out::println);
        IntSummaryStatistics statistics = list.stream().filter(integer -> integer > 2).mapToInt(i -> i * 2).skip(2).limit(2).summaryStatistics();
        System.out.println(statistics.getAverage()+","+ statistics.getCount()+","+ statistics.getMax()+","+ statistics.getMin()+","+ statistics.getSum());

    }

    public void test4() {
        String s = Stream.generate(UUID.randomUUID()::toString).findFirst().orElseGet(() -> "nihaoa");
        System.out.println(s);

        //从1开始，每个元素比前一个元素大2，最多生成10个元素
        Stream.iterate(1,i->i+2).limit(10).forEach(System.out::println);
        IntStream.iterate(0, i -> (i + 1) % 2).limit(6).distinct().forEach(System.out::println);
    }

    /**
     * 按用户名分组
     *
     * select * from student group by name;
     */
    public void test5() {
        List<Student> students = new ArrayList<>();
        //分组
        Map<String, List<Student>> listMap = students.stream().collect(Collectors.groupingBy(Student::getName));
        listMap.forEach((a, b)->{
            System.out.println(a);
            b.stream().forEach(student -> System.out.println(JSON.toJSONString(student)));
            System.out.println("---------------------------------------");
        });
        //分组后继续操作,求总和，求平均值
        Map<String, Long> longMap = students.stream().collect(Collectors.groupingBy(Student::getName, Collectors.counting()));
        longMap.forEach((a, b)-> System.out.println(a+":"+b));

        Map<String, Double> doubleMap = students.stream().collect(Collectors.groupingBy(Student::getName, Collectors.averagingDouble(Student::getScore)));
        doubleMap.forEach((a, b)-> System.out.println(a+":"+b));

        //分区
        Map<Boolean, List<Student>> booleanListMap = students.stream().collect(Collectors.partitioningBy(student -> student.getScore() > 60));
        System.out.println(JSON.toJSONString(booleanListMap.get(true)));
        System.out.println(JSON.toJSONString(booleanListMap.get(false)));
    }

    public void test6() {
        String s = Stream.of("a", "b","c").reduce("", String::concat);
        System.out.println(s);
        Integer integer = Stream.of(1, 2, 3).reduce(5, Integer::sum);
        System.out.println(integer);
        Double aDouble = Stream.of(-1.0, -2.0, 3.0, 5.0).reduce(Double.MAX_VALUE, Double::min);
        System.out.println(aDouble);
        Integer ss = Stream.of(1, 2, 3).reduce(Integer::sum).orElseGet(() -> 10);
        System.out.println(ss);
    }

}
