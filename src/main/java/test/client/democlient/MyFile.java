package test.client.democlient;

import lombok.*;

import javax.persistence.*;
import java.io.File;
import java.net.URL;

@Entity
@Setter
@Getter
public class MyFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private URL url;

    private File file;

    private long removeFileSize;

    private boolean download;
}
