package com.tecomgroup.qos.service.probeconfig;

import com.tecomgroup.qos.AgentStatistic;
import com.tecomgroup.qos.communication.message.SwUpgrade;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.exception.SourceNotFoundException;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by uvarov.m on 21.01.2016.
 */
public class ProbeConfigStorageServiceImpl implements ProbeConfigStorageService {
    private final static Logger LOGGER = Logger
            .getLogger(ProbeConfigurationServiceImpl.class);

    @Value("${ftp.sw.update.path}")
    String path;
    @Value("${ftp.probe.config.path}")
    String probeConfigPath;
    @Value("${ftp.host}")
    String host;
    @Value("${ftp.username}")
    String username;
    @Value("${ftp.password}")
    String password;

    @Override
    public void uploadProbeConfiguration(String xml, String schema, String agentKey) throws IOException{
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(host);
            ftpClient.login(username, password);
            ftpClient.enterLocalPassiveMode();

            boolean existed = ftpClient.changeWorkingDirectory(probeConfigPath);
            if (!existed) {
                boolean created = ftpClient.makeDirectory(probeConfigPath);
                if (created) {
                    ftpClient.changeWorkingDirectory(probeConfigPath);
                } else {
                    throw new IOException("Cannot create the probe config directory.");
                }
            }
            InputStream inputStream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
            ftpClient.storeFile(agentKey + ".xml", inputStream);

            if (schema != null) {
                inputStream = new ByteArrayInputStream(schema.getBytes(StandardCharsets.UTF_8));
                ftpClient.storeFile(agentKey + ".xsd", inputStream);
            }

            inputStream.close();
        } finally {
            closeClient(ftpClient);
        }
    }

    private void closeClient(FTPClient ftpClient) {
        try {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        } catch (IOException ex) {
            LOGGER.error("Can not close ftpClient", ex);
        }
    }

    @Override
    public boolean validateProbeConfig(String configuration,String agentKey) {
        FTPClient ftpClient = null;
        try {
            ftpClient = getFtpClient();
            InputStream inputStream = ftpClient.retrieveFileStream(probeConfigPath + "/" + agentKey + ".xsd");
            if (inputStream == null || ftpClient.getReplyCode() == 550) {
                // it means that file doesn't exist.
                return true;
            }
            Schema schema;
            try {
                SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                schema = schemaFactory.newSchema(new StreamSource(inputStream));
            } catch (Exception e) {
                LOGGER.error("Unable to read schema for agent " + agentKey, e);
                return true;
            }
            try {
                StringReader configReader = new StringReader(configuration);
                Source xmlSource = new StreamSource(configReader);
                Validator validator = schema.newValidator();
                validator.validate(xmlSource);
                LOGGER.info("Configuration for agent " + agentKey + " is valid.");
            } catch (SAXException e) {
                LOGGER.error("Configuration for agent " + agentKey + " is not valid.", e);
                return false;
            } catch (Exception e) {
                LOGGER.error("Unable to validate configuration for agent " + agentKey, e);
            }
        } catch (IOException e) {
            LOGGER.error("Unable to store agent file:", e);
        } finally {
            closeClient(ftpClient);
        }
        return true;
    }

    private FTPClient getFtpClient() throws IOException {
        FTPClient client = new FTPClient();
        client.connect(host);
        client.login(username, password);
        client.enterLocalPassiveMode();
        return client;
    }

    @Override
    public String getSwFtpUrl(String filename) {
        String ftpUrl = "ftp://" + username + ":" + password + "@" + host + "/" + path + "/" + filename;
        return ftpUrl;
    }

    @Override
    public List<String> getSwList () throws IOException {
        FTPClient client = getFtpClient();
        FTPFile[] files = client.listFiles(path);
        List<String> result = new ArrayList<>();
        for (FTPFile file : files) {
            if(file.isFile()) {
                result.add(file.getName());
            }
        }
        return result;
    }

    @Override
    public String downloadProbeConfiguration (String key) throws IOException {
        FTPClient ftpClient = getFtpClient();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ftpClient.retrieveFile(probeConfigPath + "/" + key + ".xml", byteArrayOutputStream);
        if (byteArrayOutputStream.toString().equals("")) {
            throw new SourceNotFoundException();
        }
        return byteArrayOutputStream.toString(StandardCharsets.UTF_8.toString());
    }
}
