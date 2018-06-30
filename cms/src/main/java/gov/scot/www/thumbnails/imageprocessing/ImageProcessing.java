package gov.scot.www.thumbnails.imageprocessing;

import gov.scot.www.thumbnails.FileType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.concurrent.*;

import static java.util.stream.Collectors.joining;

public class ImageProcessing {

    public static File extractPdfCoverImage(InputStream source) throws ImageProcessingException {

        try {
            File sourceFile = TempFileUtil.createTempFile(FileType.PDF, source);
            File targetFile = TempFileUtil.createTempFile(FileType.PNG);

            execute(targetFile,
                    "gm",
                    "convert",
                    "-page",
                    "A4",
                    "-define",
                    "pdf:use-cropbox=true",
                    "-density", "96",
                    sourceFile.getAbsolutePath() + "[0]",
                    targetFile.getAbsolutePath());
            return targetFile;
        } catch (IOException e) {
            throw new ImageProcessingException("Failed to extract pdf cover image", e);
        } finally {
            IOUtils.closeQuietly(source);
        }
    }

    public static File thumbnail(InputStream source, int width) throws ImageProcessingException {
        try {
            File sourceFile = TempFileUtil.createTempFile(FileType.PNG, source);
            File targetFile = TempFileUtil.createTempFile(FileType.PNG);

            execute(targetFile,
                    "gm",
                    "convert",
                    sourceFile.getAbsolutePath(),
                    "-resize",
                    width + "x",
                    targetFile.getAbsolutePath());
            return targetFile;
        } catch (IOException e) {
            throw new ImageProcessingException("Failed to generate thumbnail", e);
        } finally {
            IOUtils.closeQuietly(source);
        }
    }

    public static void execute(File target, String... commands) throws ImageProcessingException {
        try {
            run(target, commands);
        } catch (InterruptedException | IOException e) {
            FileUtils.deleteQuietly(target);
            throw new ImageProcessingException("ImageProcessing execute failed", e);
        }
    }

    private static void run(File target, String... commands)
            throws InterruptedException, IOException, ImageProcessingException {

        ProcessBuilder pb = new ProcessBuilder(commands);
        java.lang.Process process = pb.start();

        InputStream outputStream = process.getInputStream();
        InputStream errorStream = process.getErrorStream();

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Future<String> outputFuture = executorService.submit(copyStream(outputStream));
        Future<String> errorFuture = executorService.submit(copyStream(errorStream));

        int code = process.waitFor();

        String output = toString(outputFuture);
        String error = toString(errorFuture);

        if (code != 0) {
            FileUtils.deleteQuietly(target);
            String errorMessage = new StringBuilder()
                    .append("Failed to run imageprocessing: ")
                    .append("\n Command: ")
                    .append(pb.command().stream().collect(joining(" ")))
                    .append("\n Exit code: ")
                    .append(code)
                    .append("\n Output: ")
                    .append(output.replace("\n", "\\n"))
                    .append("\n Error: ")
                    .append(error.replace("\n", "\\n"))
                    .toString();
            throw new ImageProcessingException(errorMessage);
        }
    }

    private static Callable<String> copyStream(InputStream stream) {
        return () -> IOUtils.toString(stream, Charset.defaultCharset());
    }

    private static String toString(Future<String> future) throws IOException, InterruptedException {
        try {
            return future.get();
        } catch (ExecutionException ex) {
            throw new IOException(ex);
        }
    }

}