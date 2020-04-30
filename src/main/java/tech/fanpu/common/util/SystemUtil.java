package tech.fanpu.common.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import tech.fanpu.common.exception.SystemException;
import tech.fanpu.common.util.response.HttpStatusCode;
import tech.fanpu.common.util.response.ServiceCode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 系统工具类
 *
 * @author porridge
 * @version V1.0
 * @Title: SystemUtil.java
 * @Description: TODO
 * @date 2015年11月27日 上午10:25:44
 */
@SuppressWarnings("all")
public class SystemUtil {
    static Logger logger = LoggerFactory.getLogger(SystemUtil.class.getClass());

    /**
     * 获得请求地址ip
     *
     * @param httpservletrequest
     * @return
     */
    public static String getClientIP(HttpServletRequest httpservletrequest) {
        if (httpservletrequest == null)
            return null;
        String s = httpservletrequest.getHeader("X-Forwarded-For");
        if (s == null || s.length() == 0 || "unknown".equalsIgnoreCase(s))
            s = httpservletrequest.getHeader("Proxy-Client-IP");
        if (s == null || s.length() == 0 || "unknown".equalsIgnoreCase(s))
            s = httpservletrequest.getHeader("WL-Proxy-Client-IP");
        if (s == null || s.length() == 0 || "unknown".equalsIgnoreCase(s))
            s = httpservletrequest.getHeader("HTTP_CLIENT_IP");
        if (s == null || s.length() == 0 || "unknown".equalsIgnoreCase(s))
            s = httpservletrequest.getHeader("HTTP_X_FORWARDED_FOR");
        if (s == null || s.length() == 0 || "unknown".equalsIgnoreCase(s))
            s = httpservletrequest.getRemoteAddr();
        return s;
    }

    /**
     * 合并二个对象,将 obj2的值合并到 obj1里边去
     *
     * @param obj1
     * @param obj2
     * @return
     */
    public static void mergeBean(Object obj1, Object obj2) {
        if (obj1.getClass() != obj2.getClass()) {
            throw new SystemException(HttpStatusCode.INTERNAL_SERVER_ERROR, ServiceCode.SERVER_ERROR, "不是同一个对象.无法合并");
        }
        try {
            Field[] f1 = obj1.getClass().getDeclaredFields();
            Field[] f2 = obj2.getClass().getDeclaredFields();
            for (int i = 0; i < f2.length; i++) {
                f2[i].setAccessible(true);
                f1[i].setAccessible(true);
                if (f2[i].get(obj2) != null && f1[i].get(obj1) == null) {
                    f1[i].set(obj1, f2[i].get(obj2));
                }
            }
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String getOrderNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date()) + getRandomStr(4);
    }

    /**
     * 复制bean 允许不同对象复制,　只要他们之间有相同类型的属性名即可
     *
     * @param obj1 待复制的对象
     * @param cls  需要复制的对象 class
     */
    @SuppressWarnings("unchecked")
    public static <T> T copyBean(Object obj1, Class<?> cls) {
        try {
            Object obj2 = cls.newInstance();
            Field[] f1s = obj1.getClass().getDeclaredFields();
            Field[] f2s = obj2.getClass().getDeclaredFields();
            for (int i = 0; i < f1s.length; i++) {
                Field f1 = f1s[i];
                for (int j = 0; j < f2s.length; j++) {
                    Field f2 = f2s[j];
                    if (f1.getName().equals(f2.getName()) && f1.getType() == f2.getType()) {
                        f1.setAccessible(true);
                        f2.setAccessible(true);
                        f2.set(obj2, f1.get(obj1));
                    }
                }
            }
            return (T) obj2;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("finally")
    public static String MD5(String password) {
        MessageDigest md5;
        StringBuffer result = new StringBuffer();
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(password.getBytes());
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    result.append("0" + temp);
                } else {
                    result.append(temp);
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } finally {
            return result.toString();
        }
    }

    /**
     * 获得用户体验更好的时间返回方式
     * 2分钟以内        显示 刚刚
     * 一个钟以内      显示 x分钟前
     * 一天以内          显示  xx:xx
     * 一天以上          显示 xxxx-xx-xx
     *
     * @param createdAt
     * @return
     */
    public static String getDateInfo(Date createdAt) {
        if (createdAt == null)
            return "";
        long minute = (new Date().getTime() - createdAt.getTime()) / (60 * 1000);//分钟数
        if (minute <= 2) {
            return "刚刚";
        } else if (minute < 60) {
            return minute + "分钟前";
        } else if (minute < 1440) {
            return new SimpleDateFormat("HH : mm").format(createdAt);
        }
        return new SimpleDateFormat("yyyy-MM-dd").format(createdAt);
    }

    /**
     * 获得随机数
     *
     * @param digi
     * @return
     */
    public static String getRandomStr(int digi) {
        StringBuffer buf = new StringBuffer();
        Random r = new Random();
        for (int i = 0; i < digi; i++) {
            buf.append(r.nextInt(10));
        }
        return buf.toString();
    }

    /**
     * 将骆峰写法转换为全小写. 比如 userName to user_name
     *
     * @param name
     * @return
     */
    public static String convertLower(String name) {
        if (StringUtils.isBlank(name))
            return "";
        StringBuffer stb = new StringBuffer();
        for (char c : name.toCharArray()) {
            if (Character.isLowerCase(c))
                stb.append(c);
            else
                stb.append("_" + Character.toLowerCase(c));
        }
        return stb.toString();
    }

    public static Map<String, Object> objectCovertToMap(Object obj, String prefix) {
        Map<String, Object> result = new HashMap<>();
        Field[] f1s = obj.getClass().getDeclaredFields();
        for (int i = 0; i < f1s.length; i++) {
            Field f1 = f1s[i];
            f1.setAccessible(true);
            try {
                result.put(prefix + f1.getName(), f1.get(obj));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }


    /**
     * jsr303验证。自定义触发该验证
     *
     * @param obj
     */
    public static void validator(Object obj) {
        if (logger.isDebugEnabled()) {
            logger.debug("jsr303自定义验证对象：{}", JSONObject.toJSONString(obj));
        }
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Object>> violations = validator.validate(obj);
        if (violations != null && violations.size() != 0) {
            StringBuffer stb = new StringBuffer();
            for (ConstraintViolation<Object> v : violations) {
                stb.append(v.getPropertyPath() + v.getMessage() + "\n");
            }
            throw new SystemException(HttpStatusCode.BAD_REQUEST, ServiceCode.INVILD_PARAM_CODE, stb.toString());
        }
    }

    public static String string2Unicode(String string) {
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < string.length(); i++) {
            // 取出每一个字符
            char c = string.charAt(i);
            // 转换为unicode
            unicode.append("\\u" + Integer.toHexString(c));
        }
        return unicode.toString();
    }

    /**
     * 将文件上传到指定目录，并返回文件名称
     *
     * @param file
     * @param path
     * @param prefix
     * @return
     */
    public static String moveFile(MultipartFile file, String path, String prefix) {
        String name = prefix + SystemUtil.getUUID() + FileUtil.getFileSuffix(file.getOriginalFilename());
        try {
            file.transferTo(new File(path + name));
            return name;
        } catch (IOException e) {
            e.printStackTrace();
            throw new SystemException(HttpStatusCode.INTERNAL_SERVER_ERROR, ServiceCode.INVILD_PARAM_CODE, "图片上传失败");
        }
    }

    /**
     * 将base64转为file
     *
     * @param base64 不要传递前缀  data:image/jpeg;base64,
     * @param path
     * @return
     */
    public static String base64CoverToFile(String base64, String path, String prefix) {
        byte[] decodedImg = Base64.getDecoder().decode(base64.getBytes(StandardCharsets.UTF_8));
        String name = prefix + getUUID() + ".png";
        Path destinationFile = Paths.get(path, name);
        try {
            Files.write(destinationFile, decodedImg);
            return name;
        } catch (IOException e) {
            e.printStackTrace();
            throw new SystemException(HttpStatusCode.INTERNAL_SERVER_ERROR, ServiceCode.INVILD_PARAM_CODE, "图片上传失败");
        }
    }

    /**
     * 获得request里边的请求
     *
     * @param request
     * @param key
     * @return
     */
    public static Map<String, String> getRequestToMap(HttpServletRequest request, String... keys) {
        Map<String, String> map = new HashMap<>();
        for (String key : keys) {
            String temp = request.getParameter(key);
            if (StringUtils.isNotBlank(temp)) {
                map.put(key, temp);
            }
        }
        return map;
    }

    public static String getFindInvitationCode(Map<String, String> otherSearch, Map<String, Object> custom, String prefix) {
        String hql = "";
        if (otherSearch == null) {
            return hql;
        }
        if (otherSearch.get("agentAccount2") != null) {
            hql += " and " + prefix + ".invitationCode like :invitationCode";
            custom.put("invitationCode", otherSearch.get("agentAccount2") + "%");
        } else if (otherSearch.get("agentAccount1") != null) {
            hql += " and " + prefix + ".invitationCode like :invitationCode";
            custom.put("invitationCode", otherSearch.get("agentAccount1") + "%");
        }
        return hql;
    }

    public static String getAddBeginDateAndEndDate(Map<String, String> otherSearch, Map<String, Object> custom, String prefix) {
        String hql = "";
        if (otherSearch == null) {
            return hql;
        }
        if (StringUtils.isNotBlank(otherSearch.get("beginDate"))) {
            hql += " and " + prefix + ".createdAt>=:beginDate";
            try {
                custom.put("beginDate", DateUtil.yyyyMMddHHmmss.parse(otherSearch.get("beginDate") + " 00:00:00"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (StringUtils.isNotBlank(otherSearch.get("endDate"))) {
            hql += " and " + prefix + ".createdAt<=:endDate";
            try {
                custom.put("endDate", DateUtil.yyyyMMddHHmmss.parse(otherSearch.get("endDate") + " 23:59:59"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return hql;
    }


    public static void printCsv(List<List<String>> lists, HttpServletResponse response, String filename) {
        try {
            response.setContentType("text/csv");
            String chart = "UTF-8";
            response.reset();//清空输出流
            response.setCharacterEncoding(chart);
            filename = UrlUtil.getURLEncoderString(filename);
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes(chart), chart) + ".csv");
            OutputStream ouputStream = response.getOutputStream();

            StringBuffer stb = new StringBuffer();
            for (List<String> ls : lists) {
                Integer len = ls.size();
                for (Integer i = 0; i < len; i++) {
                    stb.append("\"" + ls.get(i) + "\"");
                    if (!i.equals(len - 1)) {
                        stb.append(",");
                    }
                }
                stb.append("\n");
            }
            ouputStream.write(stb.toString().getBytes());
            ouputStream.flush();
            ouputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
