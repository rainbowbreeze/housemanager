/**
 * 
 */
package it.rainbowbreeze.housemanager.logic;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.lang3.StringUtils;

/**
 * Utilities for scraping process
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public class ScraperUtils {
    // ------------------------------------------ Private Fields

    // -------------------------------------------- Constructors

    // --------------------------------------- Public Properties

    // ------------------------------------------ Public Methods
    /**
     * Gets the string contained inside specified tags, tags excluded
     * 
     * @param sourceString
     * @param leftTag
     * @param rightTag
     * @return
     */
    public static String getTextBetween(String sourceString, String leftTag, String rightTag) {
        if (StringUtils.isEmpty(sourceString)) return null;
        if (StringUtils.isEmpty(leftTag) && StringUtils.isEmpty(rightTag)) return sourceString;
        int startPos = 0;
        if (StringUtils.isNotEmpty(leftTag)) {
            startPos = sourceString.indexOf(leftTag);
            if (startPos < 0) return null;
            startPos+= leftTag.length();
        }
        if (startPos >= sourceString.length()) return null;
        if (StringUtils.isEmpty(rightTag)) return sourceString.substring(startPos);
        
        int endPos = sourceString.indexOf(rightTag, startPos);
        if (endPos < 0) return null;
        return sourceString.substring(startPos, endPos);
    }
    
    /**
     * Returns a 8 digits year - month - day string from a given date
     * @param date
     * @return
     */
    public static String getyyyyMMdd(Date date) {
        if (null == date) return null;
        String formattedDate = new SimpleDateFormat("yyyyMMddHHmm").format(date);
        return formattedDate;
    }
    
    /**
     * Returns only numbers in a given string.
     * Not able to manage negative number
     * 
     * @param mixedString
     * @return
     */
    public static String extractNumbers(String mixedString) {
        if (StringUtils.isEmpty(mixedString)) return mixedString;
        return mixedString.replaceAll("[^0-9]","");
    }

    
    /**
     * Inflates a given string using a GZIP algorithm
     * @param stringToCompress
     * @return
     * @throws IOException
     */
    public static String compress(String stringToCompress) throws IOException {
        if (StringUtils.isEmpty(stringToCompress)) {
            return stringToCompress;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(stringToCompress.getBytes());
        gzip.close();
        String outStr = out.toString("ISO-8859-1");
        return outStr;
     }
    
    /**
     * Deflates a given string compressed using a GZIP algorithm
     * @param compressedString
     * @return
     * @throws IOException
     */
    public static String decompress(String compressedString) throws IOException {
        if (StringUtils.isEmpty(compressedString)) {
            return compressedString;
        }
        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(compressedString.getBytes("ISO-8859-1")));
        BufferedReader bf = new BufferedReader(new InputStreamReader(gis, "ISO-8859-1"));
        StringBuffer outStr = new StringBuffer();
        String line;
        while ((line=bf.readLine())!=null) {
          outStr.append(line);
        }
        return outStr.toString();
    }
    
    public static String truncateDesciption(String description) {
        if (StringUtils.isNotEmpty(description) && description.length() > 490) {
            return description.substring(0, 490) + "...";
        } else {
            return description;
        }
    }
    
    /**
     * RFC2396 encoding of a string (spaces are substituted by %20)
     * @param value
     * @return
     */
    public static String encodeRFC2396(String value) {
        try {
            //a RFC2396 encoding is requested, so spaces are substituted by %20, for example
            //I wasn't able to find a smarter way to do this
            return StringUtils.isNotEmpty(value)
                    ? (new URI("http", value, null)).toASCIIString().substring(5) //remove the http: part
                    : value;
        } catch (URISyntaxException e) {
            return value;
        }
    }
    // ----------------------------------------- Private Methods

    // ----------------------------------------- Private Classes
}
