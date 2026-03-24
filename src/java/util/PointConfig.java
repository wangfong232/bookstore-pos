package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import jakarta.servlet.ServletContext;

/**
 * Utility class to read point configuration from pricePerPoint.txt
 */
public class PointConfig {

    private static final String CONFIG_FILE_PATH = "/WEB-INF/pricePerPoint.txt";
    private static final int DEFAULT_PRICE_PER_POINT = 10000;

    /**
     * Reads the price per point from the configuration file.
     * 
     * @param context The ServletContext to locate the file
     * @return The price per point, or a default value if not found or invalid
     */
    public static int getPricePerPoint(ServletContext context) {
        String realPath = context.getRealPath(CONFIG_FILE_PATH);
        if (realPath == null) {
            return DEFAULT_PRICE_PER_POINT;
        }

        File file = new File(realPath);
        if (!file.exists()) {
            return DEFAULT_PRICE_PER_POINT;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            if (line != null && !line.trim().isEmpty()) {
                return Integer.parseInt(line.trim());
            }
        } catch (IOException | NumberFormatException e) {
            // Log error or ignore and return default
        }
        return DEFAULT_PRICE_PER_POINT;
    }
}
