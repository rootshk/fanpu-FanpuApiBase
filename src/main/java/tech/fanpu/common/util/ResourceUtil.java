package tech.fanpu.common.util;

import java.io.InputStream;

/**
 * @author porridge
 * @version V1.0
 * @Title: ResourceUtil.java
 * @Package tech.fanpu.common.util
 * @Description: TODO 读取resource里边的文件
 * @date 2016年10月27日 下午10:12:33
 */
public class ResourceUtil {
    private InputStream inStream;

    public ResourceUtil(String url) {
        inStream = getClass().getClassLoader().getResourceAsStream(url);
    }

    public static void main(String[] a) {
        String content = new ResourceUtil("chinese_dictionary.txt").getContent();
        System.out.println(content.split("\n").length);
    }

    public String getContent() {
        return FileUtil.readInputStream(inStream);
    }
}
