package com.atguigu.gmall.all.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class TestIoJs {

    public static void main(String[] args) throws IOException {

        // 查询销售属性数据
        String spuJson = "{\"3|5|7\":\"8\",\"3|4|7\":\"7\",\"2|4|7\":\"6\",\"1|6|7\":\"3\",\"1|5|7\":\"1\",\"2|6|7\":\"4\",\"1|4|7\":\"2\",\"2|5|7\":\"5\",\"3|6|7\":\"9\"}";

        // 将销售属性生成为静态的json文件
        File file = new File("d:/spu_3.json");

        FileOutputStream fos = new FileOutputStream(file);

        fos.write(spuJson.getBytes());

    }
}
