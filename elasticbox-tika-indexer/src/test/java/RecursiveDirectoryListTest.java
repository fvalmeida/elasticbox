import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;

/**
 * Created by fvalmeida on 8/19/15.
 */
public class RecursiveDirectoryListTest {

    public static void main(String[] args) throws IOException {

        Files.walk(Paths.get("/Users/fvalmeida/Dropbox"))
                .filter(Files::isRegularFile)
                .filter(path -> {
                    try {
                        return new AgeFileFilter(
                                DateFormatUtils.ISO_DATE_FORMAT.parse("2012-09-22"), false)
                                .accept(path.toFile());
                    } catch (ParseException ignore) {
                    }
                    return true;
                })
                .sorted((o1, o2) -> LastModifiedFileComparator.LASTMODIFIED_COMPARATOR
                        .compare(o1.toFile(), o2.toFile()))
                .forEach(file -> process(file.toFile()));
    }

    private static void process(File file) {
        System.out.println(file);
//        System.out.print(" : " );
//        System.out.println(DateFormatUtils.ISO_DATE_TIME_ZONE_FORMAT.format(file.lastModified()));
    }

}
