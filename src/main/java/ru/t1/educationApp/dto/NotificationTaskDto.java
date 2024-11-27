package ru.t1.educationApp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.t1.educationApp.entity.TaskStatusEnum;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationTaskDto {
    @JsonProperty("id")
    private int id;

    @JsonProperty("status")
    private TaskStatusEnum status;
}
