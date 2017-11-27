package com.tarena.app;

/**
 * Created by tarena on 2017/9/5.
 */

public class MyClass {
    private void show() {
        MyClass mc = new MyClass();
        mc.func1();

        MyClass mc2 = new MyClass().func1();
        mc2.func2();

        new MyClass().func1().func2().func3();

        MyClass m1 = new MyClass(); //0x10
        MyClass m2 = m1.func1(); //0x10
        MyClass m3 = m2.func2(); //0x10
        m3.func3();
    }
    private  MyClass func1(){
        return this; //0x10
    }
    private  MyClass func2(){
        return this; //0x10
    }
    private  void func3(){

    }
    private  void func4(){

    }

}
