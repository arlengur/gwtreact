package com.tecomgroup.qos.rest.data;

import com.tecomgroup.qos.domain.MAlertType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author muvarov
 */
@XmlType
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class FilePath {
    public static final String BASE_IMAGE_PATH = "qos/resources/images/";

    public String path;

    public FilePath(){}

    public FilePath(String path) {
        this.path = path;
    }

    private static String shortUUID() {
        UUID uuid = UUID.randomUUID();
        long l = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
        return Long.toString(l, Character.MAX_RADIX);
    }

    public static FilePath uniqImgFilePath() {
        return new FilePath ("img/channels/" + shortUUID() + ".png");
    }

    public void saveFile(InputStream fis, String basePath) {
            try (OutputStream fileOutputStream = new FileOutputStream(basePath + path)) {
                int read;
                final byte[] bytes = new byte[1024];
                while ((read = fis.read(bytes)) != -1) {
                    fileOutputStream.write(bytes, 0, read);
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
    }


}
