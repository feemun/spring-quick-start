package cloud.catfish.api.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
@Schema(description = "Base request parameters with pagination and sorting support")
public class BaseRequestParam {
    
    /**
     * Page number for pagination (starting from 1).
     */
    private Integer offset = 1;
    
    /**
     * Number of items per page.
     */
    @Schema(description = "Page size", example = "10")
    @Min(value = 1, message = "Page size must be greater than 0")
    private Integer limit = 10;
    
    /**
     * Sort field and direction specification.
     * Format: 'fieldName,direction' (e.g., 'id,desc' or 'username,asc')
     */
    @Schema(description = "order field and direction (e.g., 'id ASC, username DESC')")
    private String orderByClause;
}