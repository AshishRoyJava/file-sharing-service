package co.alaap.filesharingservice.services;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Objects;

@Service
public class FileSharingService {

    @Value("${aws.accessKeyId}")
    private String awsAccessKeyId;

    @Value("${aws.endpointUrl}")
    private String endpointUrl;

    @Value("${aws.secretKey}")
    private String awsSecretKey;

    @Value("${aws.bucketName}")
    private String awsBucketName;

    private AmazonS3 s3client;;

    @PostConstruct
    public void init() {
        AWSCredentials credentials = new BasicAWSCredentials(awsAccessKeyId, awsSecretKey);
        s3client = new AmazonS3Client(credentials);
    }

    public String uploadFile (MultipartFile file) throws IOException {
        File fileForUpload = transformMultipartToFile(file);
        String fileName = generateFileName(file);
        String fileUrl = endpointUrl + "/" + awsBucketName + "/" + fileName;
        s3client.putObject(new PutObjectRequest(awsBucketName, fileName, fileForUpload));
        return fileUrl;
    }

    private String generateFileName(MultipartFile multiPart) {
        return new Date().getTime() + "-" + Objects.requireNonNull(multiPart.getOriginalFilename()).replace(" ", "_");
    }

    public InputStream downloadFile (String amazonFileKey) throws IOException {
        S3Object fetchFile = s3client.getObject(new GetObjectRequest(awsBucketName, amazonFileKey));
        return fetchFile.getObjectContent();
    }

    private File transformMultipartToFile (MultipartFile multipart) throws IOException {
        File convertedFile = new File(Objects.requireNonNull(multipart.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convertedFile);
        fos.write(multipart.getBytes());
        fos.close();
        return convertedFile;
    }

}
