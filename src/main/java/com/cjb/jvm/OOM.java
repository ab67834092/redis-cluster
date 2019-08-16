package com.cjb.jvm;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by chenjiabao on 2019/8/16.
 */
public class OOM {

    public static void main(String[] args) {
        List<Object> list = new LinkedList<>();
        int i=0;
        while (true){
            i++;
            if(i%10000==0){
                System.out.println("i="+i);
            }
            list.add(new Object());
        }
    }
}
