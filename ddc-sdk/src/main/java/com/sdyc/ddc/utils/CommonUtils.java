package com.sdyc.ddc.utils;

import com.radiance.tonclient.Abi;
import com.sdyc.ddc.bean.DDCResponse;

import java.io.*;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.Base64;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;

public class CommonUtils {

    private static Pattern pattern = Pattern.compile("(0|-1):[0-9a-fA-F]{64}$");

    public static String subscribeResult = "id out_messages{id,boc,msg_type,msg_type_name} now";
    /**
     * 加载tvc文件
     * @param name
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public static String tvcFromResource(String name) throws IOException, URISyntaxException {
//        return new String(Base64.getEncoder().encode(Files.readAllBytes(Paths.get(CommonUtils.class.getResource(name).toURI()))));
        return new String(Base64.getEncoder().encode(toByteArray(CommonUtils.class.getResourceAsStream(name))));
    }

    /**
     * 加载 abi文件
     * @param name
     * @return
     */
    public static Abi.ABI abiFromResource(String name) {
        Scanner s = new Scanner(CommonUtils.class.getResourceAsStream(name)).useDelimiter("\\A");
        String data = s.hasNext() ? s.next() : "";
        s.close();
        return new Abi.ABI.Serialized(data);
    }
    /**
     * 16进制转Integer
     *
     * @param value
     * @return
     */
    public static Integer hexStrToInteger(String value) {
        return Integer.valueOf(hexStrToBigInteger(value).toString(10));
    }

    /**
     * 16进制转Long
     *
     * @param value
     * @return
     */
    public static Long hexStrToLong(String value) {
        return Long.valueOf(hexStrToBigInteger(value).toString(10));
    }

    public static BigInteger hexStrToBigInteger(String value) {
        value = value.toLowerCase();
        if (value.startsWith("0x")) {
            value = value.substring(2);
        }
        return new BigInteger(value, 16);
    }

    /**
     * 用来去掉占位的0
     *
     * @param value
     * @return
     */
    public static String formatHexStr(String value) {
        value = value.toLowerCase();
        if (value.startsWith("0x")) {
            value = value.substring(2);
        }
        String str = new BigInteger(value, 16).toString(16);
        if ("0".equals(str)) {
            str = "0000000000000000000000000000000000000000";
        }
        return "0x" + str;
    }

    /**
     * 16进制转文本
     *
     * @param s
     * @return
     */
    public static String hexStrToStr(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(
                        s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "gbk");
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    public static String strToHexStr(String str)
    {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;

        for (int i = 0; i < bs.length; i++)
        {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
//            sb.append(' ');
        }
        return sb.toString().trim();
    }

    public static boolean matcherAds (String address){
        return pattern.matcher(address).matches();
    }

    static Random random = new Random();
    public static String getRandomWallet() {
        StringBuffer sb = new StringBuffer();
        String wallet = "";
        sb.append("0x");
        String str = "0123456789abcdef";
        for (int i = 0; i < 64; i ++) {
            sb.append(str.charAt(random.nextInt(16)));
        }
        wallet = sb.toString();

        return wallet;
    }

    public static byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }
    /**
     *
     * @param path 文件路径
     * @return 文件转成字节数组
     */

    public static byte[] getByteArrayFrom(String path){

        byte[] result=null;

        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();

        //创建文件
        File file=new File(path);
        FileInputStream fileInputStream=null;
        try {
            fileInputStream=new FileInputStream(file);
            int len=0;
            byte[] buffer=new byte[1024];
            while((len=fileInputStream.read(buffer))!=-1){
                outputStream.write(buffer, 0, len);
            }
            result=outputStream.toByteArray();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            if(fileInputStream!=null){
                try {
                    fileInputStream.close();
                    fileInputStream=null;
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
        return result;
    }

    public static String  subscribeFilter (String address){
        return "{\"account_addr\":{\"eq\":\"" + address + "\"}}";
    }

    public static String getMinter (String minter) {
        return PropertiesReader.config.getString(minter);
    }
}
