package org.chapchap.be.domain.map.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record KakaoResponse<T>(
        Meta meta,
        List<T> documents
) {
    public record Meta(
            @JsonProperty("total_count") Integer totalCount,
            @JsonProperty("pageable_count") Integer pageableCount,
            @JsonProperty("is_end") Boolean isEnd
    ) {}
}
