package wtf.silhouette.shaeware.utils;

import wtf.silhouette.shaeware.ShaeWare;

import java.io.File;
import java.nio.file.Files;

public final class FileUtil {

  private static final String FILE_EXTENSION = ".json";

  private static final String FILE_DIRECTORY = System.getProperty("user.home") + File.separator + "shaeware" + File.separator;

  private FileUtil() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }

  public static void createDirectory() {
    File directory = new File(FILE_DIRECTORY);

    if (!directory.exists()) {
      try {
        if (directory.mkdirs()) {
          ShaeWare.getInstance().getLogger().info("Created directory");
        }
      } catch (Exception e) {
        ShaeWare.getInstance().getLogger().error("Failed to create directory - {}", e.getMessage());
      }
    } else {
      ShaeWare.getInstance().getLogger().info("Directory already exists");
    }
  }

  public static void createFile(final String fileName) {
    File file = new File(FILE_DIRECTORY + fileName + FILE_EXTENSION);

    createDirectory();

    if (!file.exists()) {
      try {
        if (file.createNewFile()) {
          ShaeWare.getInstance().getLogger().info("Created file: {}", fileName);
        }
      } catch (Exception e) {
        ShaeWare.getInstance().getLogger().error("Failed to create file: {} - {}", fileName, e.getMessage());
      }
    }
  }

  public static void deleteFile(final String fileName) {
    File file = new File(FILE_DIRECTORY + fileName + FILE_EXTENSION);

    if (file.exists()) {
      try {
        if (file.delete()) {
          ShaeWare.getInstance().getLogger().info("Deleted file: {}", fileName);
        }
      } catch (Exception e) {
        ShaeWare.getInstance().getLogger().error("Failed to delete file: {} - {}", fileName, e.getMessage());
      }
    }
  }

  public static String readFile(final String fileName) {
    File file = new File(FILE_DIRECTORY + fileName + FILE_EXTENSION);

    if (file.exists()) {
      try {
        return new String(Files.readAllBytes(file.toPath()));
      } catch (Exception e) {
        ShaeWare.getInstance().getLogger().error("Failed to read file: {} - {}", fileName, e.getMessage());
      }
    }

    return null;
  }

  public static void writeFile(final String fileName, final String content) {
    File file = new File(FILE_DIRECTORY + fileName + FILE_EXTENSION);

    createDirectory();

    if (!file.exists()) {
      createFile(fileName);
    }

    try {
      Files.write(file.toPath(), content.getBytes());
    } catch (Exception e) {
      ShaeWare.getInstance().getLogger().error("Failed to write file: {} - {}", fileName, e.getMessage());
    }
  }
}