package cloud.catfish.api.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * Base class for all RequestParam classes providing common pagination and sorting functionality.
 * 
 * <p>This class contains standard pagination and sorting fields that are commonly used
 * across all request parameter classes:</p>
 * <ul>
 *   <li><strong>page</strong> - Page number for pagination (starting from 1)</li>
 *   <li><strong>size</strong> - Number of items per page</li>
 *   <li><strong>sort</strong> - Sort field and direction specification</li>
 * </ul>
 * 
 * <p>All generated RequestParam classes should extend this base class to inherit
 * these common pagination and sorting capabilities, eliminating code duplication
 * and ensuring consistency across all request parameter classes.</p>
 * 
 * <p><strong>Benefits of using BaseRequestParam:</strong></p>
 * <ul>
 *   <li>Eliminates code duplication across RequestParam classes</li>
 *   <li>Provides consistent pagination and sorting behavior</li>
 *   <li>Centralizes validation rules for common fields</li>
 *   <li>Simplifies maintenance and updates to pagination logic</li>
 * </ul>
 */
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