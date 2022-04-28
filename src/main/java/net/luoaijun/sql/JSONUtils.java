package net.luoaijun.sql;

import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author LUO AI JUN
 * @date 2022/4/27 14:51
 */
public class JSONUtils {
    static Logger logger = Logger.getLogger(JSONUtils.class);

    /**
     * @param inputStream
     * @param fileName
     */
    public static void saveFile(String inputStream, String fileName, boolean append) {
        try {
            if(fileName == "") {
                return;
            }
            File writeName = new File(fileName);  // 相对路径，如果没有则要建立一个新的output.txt文件
            if(!writeName.exists()) {
                writeName.createNewFile(); // 创建新文件,有同名的文件的话直接覆盖
            }
            FileWriter writer = new FileWriter(writeName, append);
            BufferedWriter out = new BufferedWriter(writer);
            out.write(inputStream);
            out.flush(); // 把缓存区内容压入文件
            out.close();
            logger.info("存储数据成功");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
