
package org.example.travel.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeeseekRequest {
    private String model;
    private List<Message> messages;
 
    @Data
    @Builder
    public static class Message {
        private String role;
        private String content;
    }
}