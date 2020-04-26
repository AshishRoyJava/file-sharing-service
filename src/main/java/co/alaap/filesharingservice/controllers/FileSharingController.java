package co.alaap.filesharingservice.controllers;

import co.alaap.filesharingservice.services.FileSharingService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/storage")
public class FileSharingController {

    private final FileSharingService fileSharingService;

    public FileSharingController(FileSharingService fileSharingService) {
        this.fileSharingService = fileSharingService;
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, String> uploadFile(@RequestParam(value = "file") MultipartFile file) throws IOException {
        Map<String, String> fileData = new HashMap<>();
        fileData.put("fileUrl", fileSharingService.uploadFile(file));
        return fileData;
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    @ResponseBody
    public void downloadFile(@RequestParam(value = "fileName") String fileName, HttpServletResponse response)
            throws IOException {

        InputStream inputStream = fileSharingService.downloadFile(fileName);

        OutputStream outStream = response.getOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;

        response.setContentType("application/octet-stream");

        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", fileName);
        response.setHeader(headerKey, headerValue);

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }

        inputStream.close();
        outStream.close();

    }
}
