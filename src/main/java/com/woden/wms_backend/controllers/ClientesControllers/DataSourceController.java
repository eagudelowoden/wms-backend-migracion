// package com.woden.wms_backend.controllers.ClientesControllers;

// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

// import com.woden.wms_backend.config.DataSource.DataSourceContextHolder;

// @RestController
// public class DataSourceController {

//     @GetMapping("/change-datasource")
//     public String changeDataSource(@RequestParam String datasourceKey) {
//         DataSourceContextHolder.setDatasourceKey(datasourceKey);
//         return "DataSource cambiado a: " + datasourceKey;
//     }
// }