package dev.rebelcraft.ai.spawn.mcp;

class McpServerServiceTest {

//    // Use a real repository instance to avoid mocking issues on newer JDKs
//    private McpServerRepository mcpServerRepository;
//
//    private McpServerFavoriteRepository favoriteRepository;
//
//    private McpServerConfigurationTemplateRepository templateService;
//
//    private McpServerService service;
//
//    private final McpServer sample1 = new McpServer("LocalFS", "fa-folder", "Provides filesystem access");
//    private final McpServer sample2 = new McpServer("DBServer", "fa-database", "Provides database access");
//
//    @BeforeEach
//    void setup() {
//        mcpServerRepository = new McpServerRepository(List.of(sample1, sample2));
//        favoriteRepository = mock(McpServerFavoriteRepository.class);
//        templateService = new McpServerConfigurationTemplateService(new McpServerConfigurationTemplateRepository());
//        service = new McpServerService(mcpServerRepository, favoriteRepository, templateService);
//    }
//
//    @Test
//    void getAllMcpServers_marksFavoritesCorrectly() {
//        when(favoriteRepository.findAll()).thenReturn(List.of(new McpServerFavorite("LocalFS")));
//        ((StubServerConfigurationTemplateRepository)templateService).putTemplate("LocalFS", null);
//
//        List<McpServerResponse> responses = service.getAllMcpServers();
//        assertEquals(2, responses.size());
//
//        McpServerResponse r0 = responses.stream().filter(r -> "LocalFS".equals(r.getName())).findFirst().orElse(null);
//        assertNotNull(r0);
//        assertTrue(r0.isFavorite());
//
//        McpServerResponse r1 = responses.stream().filter(r -> "DBServer".equals(r.getName())).findFirst().orElse(null);
//        assertNotNull(r1);
//        assertFalse(r1.isFavorite());
//    }
//
//    @Test
//    void getMcpServerByName_foundAndFavorite() {
//        when(favoriteRepository.existsByServerName("LocalFS")).thenReturn(true);
//        ((StubServerConfigurationTemplateRepository)templateService).putTemplate("LocalFS", new McpServerConfigurationTemplate( null, null, null, null));
//        ((StubServerConfigurationTemplateRepository)templateService).putFilename("LocalFS", "template.yml");
//
//        Optional<McpServerResponse> resp = service.getMcpServerByName("LocalFS");
//        assertTrue(resp.isPresent());
//        McpServerResponse r = resp.get();
//        assertEquals("LocalFS", r.getName());
//        assertTrue(r.isFavorite());
//        assertTrue(r.isTemplateAvailable());
//        assertEquals("template.yml", r.getTemplateFilename());
//    }
//
//    @Test
//    void addFavorite_existingServer_savesFavorite() {
//        when(favoriteRepository.existsByServerName("LocalFS")).thenReturn(false);
//
//        service.addFavorite("LocalFS");
//
//        ArgumentCaptor<McpServerFavorite> captor = ArgumentCaptor.forClass(McpServerFavorite.class);
//        verify(favoriteRepository).save(captor.capture());
//        assertEquals("LocalFS", captor.getValue().getServerName());
//    }
//
//    @Test
//    void addFavorite_nonExistingServer_throws() {
//        when(favoriteRepository.existsByServerName("Nope")).thenReturn(false);
//        assertThrows(IllegalArgumentException.class, () -> service.addFavorite("Nope"));
//    }
//
//    @Test
//    void removeFavorite_delegatesDelete() {
//        service.removeFavorite("LocalFS");
//        verify(favoriteRepository).deleteByServerName("LocalFS");
//    }
//
//    // Simple test stub to avoid inline mocking of McpTemplateService (ByteBuddy issues on Java 24)
//    private static class StubServerConfigurationTemplateRepository extends McpServerConfigurationTemplateService {
//        private final Map<String, Optional<McpServerConfigurationTemplate>> templates = new HashMap<>();
//        private final Map<String, Optional<String>> filenames = new HashMap<>();
//
//        StubServerConfigurationTemplateRepository() {
//            super(null);
//        }
//
//        void putTemplate(String name, McpServerConfigurationTemplate template) {
//            templates.put(name, Optional.ofNullable(template));
//        }
//
//        void putFilename(String name, String filename) {
//            filenames.put(name, Optional.ofNullable(filename));
//        }
//
//    }
}
