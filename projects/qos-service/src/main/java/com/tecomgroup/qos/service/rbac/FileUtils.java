package com.tecomgroup.qos.service.rbac;

import com.tecomgroup.qos.domain.rbac.Node;
import com.tecomgroup.qos.util.ExposedPropertyPlaceholderConfigurer;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;

import java.io.*;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Created by uvarov.m on 31.01.2016.
 */
public class FileUtils {
    private ExposedPropertyPlaceholderConfigurer commonPropertyPlaceholder;

    public static String PROPERTY_DELIMITER = ";";

    public static class MultiProperty {
        private String key;
        private List<String> values = new LinkedList<String>();

        public MultiProperty(String key, String[] values) {
            this.key = key;
            for(String value: values) {
                this.values.add(Node.preparePath(value.trim()));
            }
        }

        public String getKey() {
            return key;
        }

        public List<String> getValues() {
            return values;
        }
    }

    private InputStreamReader wrapResouceInputStream(Resource resource) throws IOException {
        return new InputStreamReader(resource.getInputStream(), "UTF-8");
    }

    public String getResourceAsString(String fileName) throws IOException {
        Resource resource = commonPropertyPlaceholder.getResource(fileName);
        if(resource != null) {
            return IOUtils.toString(wrapResouceInputStream(resource));
        }
        return "";
    }

    public Properties getResourceAsProperties(String fileName) throws IOException {
        Properties prop = new Properties();
        Resource resource = commonPropertyPlaceholder.getResource(fileName);
        if(resource != null) {
            prop.load(wrapResouceInputStream(resource));
        }
        return prop;
    }

    public List<MultiProperty> getResourceAsMultivalueProperties(String fileName) throws IOException {
        List<MultiProperty> result = new LinkedList<MultiProperty>();

        Properties props = getResourceAsProperties(fileName);
        Enumeration<?> e = props.propertyNames();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            String valueString = props.getProperty(key);
            result.add( new MultiProperty(key, valueString.split(PROPERTY_DELIMITER)));
        }

        return result;
    }

    public void setCommonPropertyPlaceholder(ExposedPropertyPlaceholderConfigurer commonPropertyPlaceholder) {
        this.commonPropertyPlaceholder = commonPropertyPlaceholder;
    }
}
