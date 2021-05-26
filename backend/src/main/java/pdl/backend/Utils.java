package pdl.backend;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import pdl.backend.mysqldb.Image;
import pdl.backend.mysqldb.ImageRepository;

public final class Utils {

    public static final Logger logger = LoggerFactory.getLogger(BackendApplication.class);

    /**
     * List all the .jpeg and .tif files for the given path
     *
     * @param p Path to look at
     * @return Set<String> Set of String corresponding to each file .jpeg and .tif
     *         at the given path
     * @throws IOException
     */
    public static Set<String> listFiles(final Path p) throws IOException {
        try (Stream<Path> stream = Files.walk(p)) {
            return stream.map(Path::getFileName).map(Path::toString)
                    .filter(file -> file.endsWith(".jpeg") || file.endsWith(".tif") || file.endsWith(".jpg"))
                    .collect(Collectors.toSet());
        }
    }

    /**
     * Give the type of an Image
     *
     * @param file File to check from
     * @return MediaType type of the file
     * @throws IOException               If impossible to determine the type of the
     *                                   file
     * @throws InvalidMediaTypeException If the type of media cannot be parsed
     */
    public static MediaType typeOfFile(final File file) throws IOException, InvalidMediaTypeException {
        return MediaType.parseMediaType(Files.probeContentType(file.toPath()));
    }

    public static MediaType typeOfFile(final Path p) throws IOException, InvalidMediaTypeException {
        return MediaType.parseMediaType(Files.probeContentType(p));
    }

    public static void copyInputStreamToFile(InputStream inputStream, File file) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            byte[] bytes = inputStream.readAllBytes();
            outputStream.write(bytes);

        }
    }

    /**
     * Give the size of an Image in a string of the form :
     * "width*height*numberOfComponentsInColorModel" example : "680*480*3" width:
     * 680, height: 480, 3 components This function assume that the file is an
     * Image, it's up to the caller to be sure to pass an Image as parameter
     *
     * @param file File to
     * @return String
     * @throws IOException If impossible to read the file as a BufferedImage
     */
    public static String sizeOfImage(final File file) throws IOException {
        return "" + ImageIO.read(file).getWidth() + "*" + ImageIO.read(file).getHeight() + "*"
                + ImageIO.read(file).getColorModel().getNumComponents();
    }

    public static String sizeOfImage(final Path p) throws IOException {
        final File f = p.toFile();
        return "" + ImageIO.read(f).getWidth() + "*" + ImageIO.read(f).getHeight() + "*"
                + ImageIO.read(f).getColorModel().getNumComponents();
    }

    public static String sizeOfImage(final InputStream p) throws IOException {
        return "" + ImageIO.read(p).getWidth() + "*" + ImageIO.read(p).getHeight() + "*"
                + ImageIO.read(p).getColorModel().getNumComponents();
    }

    /**
     * Give the type of an Image
     *
     * @param file MultipartFile
     * @return MediaType
     * @throws InvalidMediaTypeException If impossible to parse the type of the file
     */
    public static MediaType typeOfFile(final MultipartFile file) throws InvalidMediaTypeException {
        return MediaType.parseMediaType(file.getContentType());
    }

    /**
     * Give the size of an Image in a string of the form :
     * "width*height*numberOfComponentsInColorModel" example : "680*480*3" width:
     * 680, height: 480, 3 components This function assume that the file is an
     * Image, it's up to the caller to be sure to pass an Image as parameter
     *
     * @param file MultipartFile
     * @return String
     * @throws IOException If impossible to read the file as a BufferedImage
     */
    public static String sizeOfImage(final MultipartFile file) throws IOException {
        final BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
        return "" + bufferedImage.getWidth() + "*" + bufferedImage.getHeight() + "*"
                + bufferedImage.getColorModel().getNumComponents();
    }

    public static String sizeOfImageJar(final Path p) throws IOException {
        final BufferedImage bufferedImage = ImageIO.read(Files.newInputStream(p));
        return "" + bufferedImage.getWidth() + "*" + bufferedImage.getHeight() + "*"
                + bufferedImage.getColorModel().getNumComponents();

    }

    /**
     * Return a random Number depending on the type specified int, float long, or
     * double
     *
     * @param c   Class<?> to specify the number
     * @param min int minimum
     * @param max int maxmum inclusive
     * @return Obect
     * @throws IllegalArgumentException If the min >= max or wrong Class passed in
     *                                  parameters
     */
    public static Object getRandomNumber(final Class<?> c, final int min, final int max)
            throws IllegalArgumentException {
        if (min >= max) {
            throw new IllegalArgumentException("max has to be greater than min !");
        }

        final Random r = new Random();

        if (c == int.class) {
            return Integer.valueOf(r.nextInt((max - min) + 1) + min);
        } else if (c == float.class) {
            return Float.valueOf(r.nextFloat() * ((float) max));
        } else if (c == double.class) {
            return Double.valueOf(r.nextDouble() * ((double) max));
        } else if (c == long.class) {
            return Long.valueOf(r.nextInt((max - min) + 1) + min);
        }

        throw new IllegalArgumentException("Class must be either int, float, double or long, you gave :" + c.getName());
    }

    public static void readContent(Path path, String relativePath, ImageRepository imageRepository) {
        try {
            Stream<Path> list = Files.list(path);
            List<Path> paths = list.parallel().collect(Collectors.toList());
            Set<String> publicImages = imageRepository.publicSet();

            System.err.println("----- Public images : ");
            for (String string : publicImages) {
                System.err.println(string);
            }
            for (Path p : paths) {
                logger.warn(p.toString());
            }

            paths.stream().parallel().forEach(p -> {
                String fileName = p.toString().split(relativePath)[1];
                if (!publicImages.contains(fileName))
                    try {
                        MediaType type = MediaType.parseMediaType(Files.probeContentType(p));
                        String size = sizeOfImageJar(p);
                        Image image = new Image(fileName, Files.readAllBytes(p), type, size);
                        imageRepository.save(image);
                        System.out.println(image);
                    } catch (IOException e) {
                        logger.warn("Init FAIL: Could not read property of file while loading images on server: "
                                + fileName);
                        e.printStackTrace();
                    }
            });
            list.close();
        } catch (IOException e) {
            logger.error("Could not read property of one file while loading it on server !");
            e.printStackTrace();
        }
    }

    /*
     * public static void readContent(Path path, ImageRepository imageRepository) {
     * try { Stream<Path> list = Files.list(path); List<Path> paths =
     * list.parallel().collect(Collectors.toList());
     * paths.stream().parallel().forEach(filePath -> { if
     * (Files.isDirectory(filePath)) { readContent(filePath, imageRepository); }
     * else { try { String fileName = filePath.toString().split("classes")[1];
     * logger.debug("File :" + fileName); } catch (Exception e) {
     * logger.error("Unable to read file " + e.getMessage()); } } }); list.close();
     * } catch (Exception e) { logger.error(e.getMessage()); } }
     */

}