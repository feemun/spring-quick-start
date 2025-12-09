package cloud.catfish.elasticsearch9.model;

import lombok.Data;

@Data
public class MyDocument {
    private String id;
    private String title;
    private String description;
}
