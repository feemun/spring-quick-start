package cloud.catfish.neo4j.entity;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@Node("User")
public class User {

    @Id
    private String id;
    private String name;
    private String email;

    @Relationship(type = "BELONGS_TO")
    private Organization organization;

    @Relationship(type = "MESSAGED", direction = Relationship.Direction.OUTGOING)
    private List<Message> messagesSent;

}