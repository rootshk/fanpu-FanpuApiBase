package tech.fanpu.common.util;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;

/**
 * @author porridge
 * @version V1.0
 * @Title: FileUtil.java
 * @Description: TODO 文件操作工具
 * @date 2016年1月11日 下午5:09:12
 */
public class FileUtil {
    public final static String CHARSET = "UTF-8";
    static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 下载网络文件
     *
     * @param networkUrl
     * @param file
     * @throws MalformedURLException
     */
    @SuppressWarnings("all")
    public static Boolean downloadNetworkFile(String networkUrl, String file) {
        int bytesum = 0;
        int byteread = 0;
        Boolean result = false;
        try {
            URL url = new URL(networkUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36");//假装自己是浏览器.哈哈.
            conn.setReadTimeout(5000);//毫秒计
            if (conn.getResponseCode() == 200) {
                InputStream inStream = conn.getInputStream();
                FileOutputStream fs = new FileOutputStream(file);
                byte[] buffer = new byte[1204];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread;
                    fs.write(buffer, 0, byteread);
                }
                fs.close();
                inStream.close();
                result = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("下载网络文件失败：" + networkUrl + " file:" + file);
            logger.error("e", e);
        } finally {
            return result;
        }
    }

    public static void ensureDirExists(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("all")
    public static String writeText(String url, String content) {
        String fileName = null;
        FileOutputStream fos;
        boolean ret = false;
        try {
            File file = new File(url);
            File parentFile = new File(file.getParent());
            if (!parentFile.exists() && !parentFile.isDirectory()) {
                parentFile.mkdirs();
            }
            if (!file.exists())
                file.createNewFile();

            fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos, CHARSET);
            BufferedWriter bw = new BufferedWriter(osw);
            bw.write(content);
            bw.close();
            osw.close();
            fos.close();
            fileName = file.getName();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return fileName;
        }
    }

    /**
     * 写入文件为csv格式的
     *
     * @param url
     * @param lists
     * @return
     */
    @SuppressWarnings("all")
    public static String writeCSV(String url, List<List<String>> lists, String charset) {
        String fileName = null;
        FileOutputStream fos;
        boolean ret = false;
        try {
            File file = new File(url);
            File parentFile = new File(file.getParent());
            if (!parentFile.exists() && !parentFile.isDirectory()) {
                parentFile.mkdirs();
            }
            if (!file.exists())
                file.createNewFile();

            fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos, charset);
            BufferedWriter bw = new BufferedWriter(osw);
            for (List<String> ls : lists) {
                Integer len = ls.size();
                for (Integer i = 0; i < len; i++) {
                    bw.write("\"" + ls.get(i) + "\"");
                    if (!i.equals(len - 1))
                        bw.write(",");
                }
                bw.write("\n");
            }
            bw.close();
            osw.close();
            fos.close();
            fileName = file.getName();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return fileName;
        }
    }


    @SuppressWarnings("all")
    public static String writeByteArrayOutputStream(String url, File file) {
        String fileName = null;
        FileOutputStream fos;
        boolean ret = false;
        try {
            File parentFile = new File(file.getParent());
            if (!parentFile.exists() && !parentFile.isDirectory()) {
                parentFile.mkdirs();
            }
            if (!file.exists())
                file.createNewFile();

            fos = new FileOutputStream(file);
            fos.close();
            fileName = file.getName();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return fileName;
        }
    }

    public static byte[] getFileByte(String path) {
        byte[] buffer = null;
        try {
            File file = new File(path);
            if (!file.isFile()) {
                return null;
            }
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return buffer;
        }
    }


    @SuppressWarnings("all")
    public static String readText(String url) {
        if (url == null)
            return "";
        File file = new File(url);
        if (!file.exists())
            return null;

        StringBuffer stb = new StringBuffer();
        BufferedReader buff = null;
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), CHARSET);
            buff = new BufferedReader(isr);
            while (buff.ready()) {
                stb.append(buff.readLine());
            }
            buff.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return stb.toString();
        }
    }

    /**
     * 读取 流里边的数据
     *
     * @param inStream
     * @return
     */
    public static String readInputStream(InputStream inStream) {
        try {
            int count = 0;
            while (count == 0) {
                count = inStream.available();
            }
            byte[] b = new byte[count];
            int len = 0;
            int temp = 0;
            while ((temp = inStream.read()) != -1) { // 当没有读取完时，继续读取
                b[len] = (byte) temp;
                len++;
            }
            inStream.close();
            return new String(b, 0, len);
        } catch (IOException e) {
            return "";
        }
    }

    /**
     * 保存流数据
     *
     * @param url
     * @param is
     * @return
     */
    public static boolean saveInputStream(String url, InputStream is) {
        try {
            File file = new File(url);
            File parentFile = new File(file.getParent());
            if (!parentFile.exists() && !parentFile.isDirectory()) {
                parentFile.mkdirs();
            }
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(url)));
            FileCopyUtils.copy(is, stream);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 取文件后缀名
     *
     * @return
     */
    public static String getFileSuffix(String name) {
        if (name.indexOf(".") != -1) {
            name = name.substring(name.lastIndexOf("."));
        }
        return name;
    }

    /**
     * 删除文件
     *
     * @param url
     */
    public static void deleteFile(String url) {
        if (StringUtils.isNotBlank(url)) {
            File file = new File(url);
            if (file.isFile()) {
                file.delete();
            }
        }
    }

    public static void main(String[] a) {
        System.out.println("====" + FileUtil.getFileSuffix("/Users/porridge/Music/aa.mp3"));
    }

    public static String getFileSize(long fileS) {
        if (fileS == 0)
            return "0BT";
        DecimalFormat df = new DecimalFormat("#.00");
        if (fileS < 1024) {
            return df.format((double) fileS) + "BT";
        } else if (fileS < 1048576) {
            return df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            return df.format((double) fileS / 1048576) + "MB";
        } else {
            return df.format((double) fileS / 1073741824) + "GB";
        }
    }
}
