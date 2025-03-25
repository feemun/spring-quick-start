package cloud.catfish.neo4j.entity;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Organization")
public class Organization {

    @Id
    private String id;
    private String name;
    private String type;

}