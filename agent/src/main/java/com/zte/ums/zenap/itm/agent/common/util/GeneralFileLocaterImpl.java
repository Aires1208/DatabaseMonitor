package com.zte.ums.zenap.itm.agent.common.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author TangYabin
 * 
 */
public class GeneralFileLocaterImpl
{
    public final static String WILDCODE_STRING = "*";
    public final static char WILDCODE = '*';
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralFileLocaterImpl.class);
    public final static String DICNAME_CONF = "conf";
    private static GeneralFileLocaterImpl instance = new GeneralFileLocaterImpl();
    private String configPath;
    private String appRootPath;

    private GeneralFileLocaterImpl()
    {
        String jarPath = "";
        try
        {
            jarPath = GeneralFileLocaterImpl.class.getProtectionDomain().getCodeSource().getLocation().getFile();
            jarPath = URLDecoder.decode(jarPath, System.getProperty("file.encoding"));
        }
        catch (UnsupportedEncodingException e)
        {
            jarPath = System.getProperty("user.dir");
            System.err.println("get jarPath err:" + e.getMessage());
            
        }
        int index = jarPath.lastIndexOf("/");
        
        appRootPath = jarPath.substring(0, index);
        logInfo("app root path is " + appRootPath);
        this.configPath = appRootPath + File.separator + DICNAME_CONF;
    }

    /**
     * @param msg
     */
    protected void logError(String msg)
    {
        LOGGER.error(msg);
    }

    /**
     * @param msg
     */
    protected void logInfo(String msg)
    {
        LOGGER.info(msg);
    }

    public static GeneralFileLocaterImpl getGeneralFileLocater()
    {
        return instance;
    }

    /**
     * Get the configuration file path
     * @return
     */
    public String getConfigPath()
    {
        return this.configPath;
    }

    protected void setConfigPath(String configPath)
    {
        this.configPath = configPath;
    }

    /**
     * According to the configuration file version of the root directory (ITMP-Proxyconf) 
     * relative path name file access file instance.
     * 
     * @param fileName
     *            The relative path of the file relative to the root directory
     *             of the version configuration file
     *  for example: \dbcp\dbcp_config.properties
     * @return
     */
    private File getFile(String fileName)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getConfigPath());
        sb.append(File.separatorChar);
        sb.append(fileName);
        return new File(sb.toString());
    }

    public File getFile(String directory, String fileName)
    {
        StringBuilder sb = new StringBuilder(directory);
        sb.append(File.separatorChar);
        sb.append(fileName);
        return getFile(sb.toString());
    }

    public String getRootPath()
    {
        return this.appRootPath;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.zte.ums.it.proxy.util.GeneralFileLocater#getFilePath(java.lang.String)
     */
    public String getFilePath(String fileName) throws IOException
    {
        File file = getFile(fileName);
        if (!file.exists())
        {
            throw new FileNotFoundException(fileName + " is not found under " + getConfigPath());
        }
        return file.getCanonicalPath();
    }

    public File[] getFiles(String directory, String fileCode)
    {
        int sepPos = fileCode.lastIndexOf(File.separatorChar);
        if (sepPos >= 0)
        {
            String dirPath = fileCode.substring(0, sepPos);
            String realFileCode = fileCode.substring(sepPos + 1, fileCode.length());
            return getFiles(dirPath, realFileCode);
        }
        else
        {
            File directoryFile = getFile(directory);
            String regex = fileCode.replace(WILDCODE_STRING, ".*");
            final Pattern pattern = Pattern.compile(regex);
            return directoryFile.listFiles(new FilenameFilter()
            {
                @Override
                public boolean accept(File dir, String name)
                {
                    Matcher matcher = pattern.matcher(name);
                    if (matcher.matches())
                    {
                        return true;
                    }
                    return false;
                }
            });
        }
    }
}
