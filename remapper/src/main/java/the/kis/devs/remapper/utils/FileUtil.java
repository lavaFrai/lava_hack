package the.kis.devs.remapper.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {
    @SuppressWarnings("UnusedReturnValue")
    public static Path getDirectory(Path parent, String... paths) {
        if ( paths.length < 1 ) {
            return parent;
        }

        Path dir = lookupPath(parent, paths);
        createDirectory(dir);
        return dir;
    }

    public static void createDirectory(Path dir)
    {
        try
        {
            if (!Files.isDirectory(dir))
            {
                if (Files.exists(dir))
                {
                    Files.delete(dir);
                }

                Files.createDirectories(dir);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    public static Path lookupPath(Path root, String...paths)
    {
        return Paths.get(root.toString(), paths);
    }
}

