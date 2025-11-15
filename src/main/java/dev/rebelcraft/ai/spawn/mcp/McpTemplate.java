package dev.rebelcraft.ai.spawn.mcp;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class McpTemplate {

    private Map<String, Object> servers;
    private List<McpTemplateInput> inputs;

    // Constructors
    public McpTemplate() {
    }

    public McpTemplate(Map<String, Object> servers, List<McpTemplateInput> inputs) {
        this.servers = servers;
        this.inputs = inputs;
    }

    // Getters and Setters
    public Map<String, Object> getServers() {
        return servers;
    }

    public void setServers(Map<String, Object> servers) {
        this.servers = servers;
    }

    public List<McpTemplateInput> getInputs() {
        return inputs;
    }

    public void setInputs(List<McpTemplateInput> inputs) {
        this.inputs = inputs;
    }

    // Inner class for template inputs
    public static class McpTemplateInput {
        private String type;
        private String id;
        private String description;
        private Boolean password;
        
        @JsonProperty("default")
        private String defaultValue;

        // Constructors
        public McpTemplateInput() {
        }

        public McpTemplateInput(String type, String id, String description, Boolean password, String defaultValue) {
            this.type = type;
            this.id = id;
            this.description = description;
            this.password = password;
            this.defaultValue = defaultValue;
        }

        // Getters and Setters
        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Boolean getPassword() {
            return password;
        }

        public void setPassword(Boolean password) {
            this.password = password;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }
    }
}
