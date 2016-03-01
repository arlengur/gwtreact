package com.tecomgroup.qos.service.rbac;

import com.tecomgroup.qos.domain.rbac.Node;
import com.tecomgroup.qos.domain.rbac.Subject;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;

import static com.tecomgroup.qos.service.rbac.FileUtils.MultiProperty;

/**
 * Created by uvarov.m on 31.01.2016.
 */
public class SubjectMapper<T extends Subject> {
    private final static Logger LOGGER = Logger.getLogger(SubjectMapper.class);
    private String fileName;
    private FileUtils utils;

    private Class<T> clazz;
    private Structure structure;
    private Map<String, T> mapping = new TreeMap<String, T>();

    public SubjectMapper(Class clazz) {
        this.clazz = clazz;
    }

    public void init() throws IOException, IllegalAccessException, InstantiationException {
        List<MultiProperty> props = utils.getResourceAsMultivalueProperties(fileName);
        for(MultiProperty prop: props) {
            T subject = getSubjectInstance(prop.getKey());

            for (String fullPath : prop.getValues()) {
                Node node = structure.getNodeByPath(fullPath);
                if (node == null) {
                    LOGGER.warn("Subject [" + clazz + ", " + prop.getKey() + "=" + fullPath + "] not found in structure");
                } else {
                    subject.getNodes().add(node);
                    LOGGER.info("Subject [" + clazz + ", " + prop.getKey() + "=" + fullPath + "] mapped");
                }
            }
            mapping.put(prop.getKey(), subject);
        }
    }

    private T getSubjectInstance(String name) throws IllegalAccessException, InstantiationException {
        T instance = clazz.newInstance();
        instance.setName(name);
        return instance;
    }

    protected T getSubjectByName(String name) {
        return mapping.get(name);
    }

    public void setUtils(FileUtils utils) {
        this.utils = utils;
    }

    public void setStructure(StructureImpl structure) {
        this.structure = structure;
    }

    public Structure getStructure() {
        return structure;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Collection<T> getAllSubjects() {
        return mapping.values();
    }

    public Set<String> getAllSubjectKeys() {
        return mapping.keySet();
    }
}
