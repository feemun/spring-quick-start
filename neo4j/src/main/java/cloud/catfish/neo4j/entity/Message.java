package cloud.catfish.neo4j.entity;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.LocalDateTime;

@RelationshipProperties
public class Message {

    @Id
    @GeneratedValue
    private Long id;
    private String messageContent;
    private LocalDateTime timestamp;

    @TargetNode
    private User recipient;
    
}