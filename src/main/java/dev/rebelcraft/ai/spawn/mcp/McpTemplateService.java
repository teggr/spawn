package dev.rebelcraft.ai.spawn.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class McpTemplateService {

    private static final Logger logger = LoggerFactory.getLogger(McpTemplateService.class);
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{input:([^}]+)\\}");
    
    private final ResourcePatternResolver resourceResolver;
    private final ObjectMapper objectMapper;
    
    // Index: normalized name -> (filename, template)
    private final Map<String, TemplateEntry> templateIndex = new HashMap<>();
    
    public McpTemplateService(ResourcePatternResolver resourceResolver, ObjectMapper objectMapper) {
        this.resourceResolver = resourceResolver;
        this.objectMapper = objectMapper;
    }
    
    @PostConstruct
    public void loadTemplates() {
        try {
            Resource[] resources = resourceResolver.getResources("classpath:/mcp/templates/*.json");
            logger.info("Found {} template files", resources.length);
            
            for (Resource resource : resources) {
                try {
                    String filename = resource.getFilename();
                    if (filename == null) {
                        continue;
                    }
                    
                    McpTemplate template = objectMapper.readValue(resource.getInputStream(), McpTemplate.class);
                    String baseName = filename.substring(0, filename.length() - 5); // Remove .json
                    
                    // Index by exact name (preserve case)
                    templateIndex.put(baseName, new TemplateEntry(filename, template));
                    
                    // Index by lowercase for case-insensitive lookup
                    String lowerName = baseName.toLowerCase();
                    if (!lowerName.equals(baseName)) {
                        templateIndex.putIfAbsent(lowerName, new TemplateEntry(filename, template));
                    }
                    
                    // Index by normalized name
                    String normalizedName = normalizeName(baseName);
                    if (!normalizedName.equals(lowerName) && !normalizedName.equals(baseName)) {
                        templateIndex.putIfAbsent(normalizedName, new TemplateEntry(filename, template));
                    }
                    
                    logger.info("Loaded template: {} (normalized: {})", filename, normalizedName);
                } catch (IOException e) {
                    logger.error("Failed to parse template file: {}", resource.getFilename(), e);
                }
            }
        } catch (IOException e) {
            logger.error("Failed to load templates", e);
        }
    }
    
    public Optional<McpTemplate> getTemplateForServer(String serverName) {
        // Try exact match first
        TemplateEntry entry = templateIndex.get(serverName);
        if (entry != null) {
            return Optional.of(entry.template);
        }
        
        // Try case-insensitive match
        entry = templateIndex.get(serverName.toLowerCase());
        if (entry != null) {
            return Optional.of(entry.template);
        }
        
        // Try normalized match
        String normalized = normalizeName(serverName);
        entry = templateIndex.get(normalized);
        if (entry != null) {
            return Optional.of(entry.template);
        }
        
        return Optional.empty();
    }
    
    public Optional<String> getTemplateFilenameForServer(String serverName) {
        // Try exact match first
        TemplateEntry entry = templateIndex.get(serverName);
        if (entry != null) {
            return Optional.of(entry.filename);
        }
        
        // Try case-insensitive match
        entry = templateIndex.get(serverName.toLowerCase());
        if (entry != null) {
            return Optional.of(entry.filename);
        }
        
        // Try normalized match
        String normalized = normalizeName(serverName);
        entry = templateIndex.get(normalized);
        if (entry != null) {
            return Optional.of(entry.filename);
        }
        
        return Optional.empty();
    }
    
    public Map<String, McpTemplate> getAllTemplates() {
        Map<String, McpTemplate> result = new HashMap<>();
        Set<String> addedFilenames = new HashSet<>();
        
        for (Map.Entry<String, TemplateEntry> entry : templateIndex.entrySet()) {
            String filename = entry.getValue().filename;
            if (!addedFilenames.contains(filename)) {
                result.put(entry.getKey(), entry.getValue().template);
                addedFilenames.add(filename);
            }
        }
        
        return Collections.unmodifiableMap(result);
    }
    
    public String compileTemplate(McpTemplate template) {
        try {
            // Build resolved inputs map
            Map<String, String> resolvedInputs = new HashMap<>();
            if (template.getInputs() != null) {
                for (McpTemplate.McpTemplateInput input : template.getInputs()) {
                    String value;
                    if (input.getDefaultValue() != null && !input.getDefaultValue().isEmpty()) {
                        value = input.getDefaultValue();
                    } else if (Boolean.TRUE.equals(input.getPassword())) {
                        value = "*****";
                    } else {
                        value = "<" + input.getId() + ">";
                    }
                    resolvedInputs.put(input.getId(), value);
                }
            }
            
            // Deep copy and replace placeholders
            Object compiledServers = replacePlaceholders(template.getServers(), resolvedInputs);
            
            // Create a map with the compiled servers
            Map<String, Object> result = new HashMap<>();
            result.put("servers", compiledServers);
            
            // Pretty print
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
        } catch (Exception e) {
            logger.error("Failed to compile template", e);
            return "{}";
        }
    }
    
    private Object replacePlaceholders(Object node, Map<String, String> resolvedInputs) {
        if (node == null) {
            return null;
        }
        
        if (node instanceof String) {
            String str = (String) node;
            Matcher matcher = PLACEHOLDER_PATTERN.matcher(str);
            StringBuffer result = new StringBuffer();
            
            while (matcher.find()) {
                String inputId = matcher.group(1);
                String replacement = resolvedInputs.getOrDefault(inputId, "<" + inputId + ">");
                matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
            }
            matcher.appendTail(result);
            return result.toString();
        }
        
        if (node instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) node;
            Map<String, Object> result = new LinkedHashMap<>();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                result.put(entry.getKey(), replacePlaceholders(entry.getValue(), resolvedInputs));
            }
            return result;
        }
        
        if (node instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) node;
            List<Object> result = new ArrayList<>();
            for (Object item : list) {
                result.add(replacePlaceholders(item, resolvedInputs));
            }
            return result;
        }
        
        // For other types (numbers, booleans, etc.), return as-is
        return node;
    }
    
    private String normalizeName(String name) {
        if (name == null) {
            return "";
        }
        
        // Lowercase, trim, whitespace to hyphen, keep only a-z0-9-_
        return name.toLowerCase()
                .trim()
                .replaceAll("\\s+", "-")
                .replaceAll("[^a-z0-9\\-_]", "");
    }
    
    private static class TemplateEntry {
        final String filename;
        final McpTemplate template;
        
        TemplateEntry(String filename, McpTemplate template) {
            this.filename = filename;
            this.template = template;
        }
    }
}
