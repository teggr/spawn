package dev.rebelcraft.ai.spawn.models;

public class ModelResponse {

    private String provider;
    private String multimodality;
    private String toolsFunctions;
    private String streaming;
    private String retry;
    private String observability;
    private String builtInJson;
    private String local;
    private String openAiApiCompatible;
    private boolean favorite;

    // Constructors
    public ModelResponse() {
    }

    public ModelResponse(String provider, String multimodality, String toolsFunctions, String streaming,
                        String retry, String observability, String builtInJson, String local,
                        String openAiApiCompatible) {
        this.provider = provider;
        this.multimodality = multimodality;
        this.toolsFunctions = toolsFunctions;
        this.streaming = streaming;
        this.retry = retry;
        this.observability = observability;
        this.builtInJson = builtInJson;
        this.local = local;
        this.openAiApiCompatible = openAiApiCompatible;
    }

    public ModelResponse(String provider, String multimodality, String toolsFunctions, String streaming,
                        String retry, String observability, String builtInJson, String local,
                        String openAiApiCompatible, boolean favorite) {
        this.provider = provider;
        this.multimodality = multimodality;
        this.toolsFunctions = toolsFunctions;
        this.streaming = streaming;
        this.retry = retry;
        this.observability = observability;
        this.builtInJson = builtInJson;
        this.local = local;
        this.openAiApiCompatible = openAiApiCompatible;
        this.favorite = favorite;
    }

    // Getters and Setters
    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getMultimodality() {
        return multimodality;
    }

    public void setMultimodality(String multimodality) {
        this.multimodality = multimodality;
    }

    public String getToolsFunctions() {
        return toolsFunctions;
    }

    public void setToolsFunctions(String toolsFunctions) {
        this.toolsFunctions = toolsFunctions;
    }

    public String getStreaming() {
        return streaming;
    }

    public void setStreaming(String streaming) {
        this.streaming = streaming;
    }

    public String getRetry() {
        return retry;
    }

    public void setRetry(String retry) {
        this.retry = retry;
    }

    public String getObservability() {
        return observability;
    }

    public void setObservability(String observability) {
        this.observability = observability;
    }

    public String getBuiltInJson() {
        return builtInJson;
    }

    public void setBuiltInJson(String builtInJson) {
        this.builtInJson = builtInJson;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getOpenAiApiCompatible() {
        return openAiApiCompatible;
    }

    public void setOpenAiApiCompatible(String openAiApiCompatible) {
        this.openAiApiCompatible = openAiApiCompatible;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
