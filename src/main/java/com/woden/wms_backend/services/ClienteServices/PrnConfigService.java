package com.woden.wms_backend.services.ClienteServices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class PrnConfigService {

    @Value("${prn.path}")
    private String basePath;

    public String getBasePath() {
        return basePath;
    }

}
