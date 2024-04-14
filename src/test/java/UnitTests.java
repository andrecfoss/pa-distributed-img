// UnitTests.java

//Libraries
import org.junit.jupiter.api.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.*;

//Main class for the Unit Tests
class UnitTests
{
    // Let's start by testing server connectivity
    // also when a client enters the server

    @Nested
    @DisplayName("Server")
    class serverTest {

        private Server server_1;
        private Server server_2;
        private Server server_3;
        private LoadInfoWriter writer;

        private ReentrantLock loadInfoLock;


        @BeforeEach
        void setUp() {

            this.writer = new LoadInfoWriter("load.info.txt", loadInfoLock);


            this.server_1 = new Server(8888, 4, writer);
            this.server_2 = new Server(8000, 2, writer);
            this.server_3 = new Server(8100, 0, writer);



            //Using Thread to start the server
            Thread serverThread = new Thread(() -> {
                try {
                    server_1.startServer();
                    server_2.startServer();
                    server_3.startServer();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            serverThread.start();
        }
        @Test
        @DisplayName("Test Server Connection through different servers")
        void testServerConnection() {
            assertAll(
                    () -> assertEquals(8888, server_1.port),
                    () -> assertEquals(8000, server_2.port),
                    () -> assertEquals(8100, server_3.port),
                    () -> assertNotNull(writer)
            );
        }
    }

    @Nested
    @DisplayName("Client")
    class ClientTest {
        private Client client;

        private Request request;

        @BeforeEach
        void setUp() {
            this.client = new Client("joao");
        }


        @Test
        @DisplayName("Check of send request and receive response")
        void testSendRequestAndReceiveResponse() throws InterruptedException {
            BufferedImage image = ImageReader.readImage("sample.png");
            assertNotNull(image, "Image should not be null");

            ReentrantLock loadInfoLock = new ReentrantLock();
            LoadInfoWriter loadInfoWriter = new LoadInfoWriter("load.info.txt",loadInfoLock);
            Server server = new Server(8888, 5,loadInfoWriter);
            server.start();

            Client client = new Client("TestClient");
            this.request = new Request("message", "Hello, Server!", image);
            Response response = client.sendRequestAndReceiveResponse("localhost", 8888, request);

            assertAll(
                    () ->assertNotNull(response),
                    () ->assertEquals("Hello, Client!", response.getMessage())
            );

        }

        @Test
        @DisplayName("Check the name")
        void testgetName(){
            assertAll(
                    () -> assertEquals("joao",client.getName())
            );
        }
    }

    @Nested
    @DisplayName("ConfigReader")
    class ConfigReaderTest {

        private static final String TestConf = "Config.txt";

        @Test
        @DisplayName("Test to read from file")
        void TestReadFile() {
            ConfigReader config = new ConfigReader();
            config.ReadFile(TestConf);
            assertAll(
                    // Verificar se os valores lidos correspondem aos valores esperados
                    () -> assertEquals(4, config.getnColumns()),
                    () -> assertEquals(4, config.getnRows()),
                    () -> assertEquals(3, config.getnServers()),
                    () -> assertEquals(3, config.getnClients())
            );
        }

    }

    @Nested
    @DisplayName("ImageReader")
    class ImageReaderTest{

        @Test
        @DisplayName("Test if image exists when reading")
        void testReadExistingImage() {
            BufferedImage image = ImageReader.readImage("sample.png");
            assertNotNull(image, "Image should not be null");
        }
    }

    @Nested
    @DisplayName("ImageTransformer")
    class ImagetransformerTest{

        private SIMDExecutor simd;

        @BeforeEach
        void setUp(){
            BufferedImage image = ImageReader.readImage("sample.png");
            assertNotNull(image, "Image should not be null");

        }

        @Test
        @DisplayName("Remove reds from the image")
        void testRemoveReds() {
            BufferedImage image = ImageReader.readImage("sample.png");
            assertNotNull(image, "Image should not be null");

            BufferedImage imageWithoutRed = ImageTransformer.removeReds(image); // Remove a componente vermelha
            assertNotNull(imageWithoutRed);

            int width = imageWithoutRed.getWidth();
            int height = imageWithoutRed.getHeight();

            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    int checkred = imageWithoutRed.getRGB(i, j);
                    int red = (checkred >> 16) & 0xFF; // Componente vermelha
                    assertEquals(0, red); // Verifica se o componente vermelho é zero
                }
            }
        }

        @Test
        @DisplayName("Create image from bytes")
        void testCreateImageFromBytes() {
            BufferedImage image = ImageReader.readImage("sample.png");
            assertNotNull(image, "Image should not be null");

            byte[] BytesFromImage = ImageTransformer.createBytesFromImage(image); // Converte a imagem para um array de bytes
            assertNotNull(BytesFromImage);

            BufferedImage ImageFromBytes = ImageTransformer.createImageFromBytes(BytesFromImage); // Converte o array de bytes de volta para uma imagem
            assertNotNull(ImageFromBytes);

            assertEquals(image.getWidth(), ImageFromBytes.getWidth());
            assertEquals(image.getHeight(), ImageFromBytes.getHeight());


            for (int i = 0; i < image.getWidth(); i++) {
                for (int j = 0; j < image.getHeight(); j++) {
                    assertEquals(image.getRGB(i, j), ImageFromBytes.getRGB(i, j));
                }
            }
        }
/*
        @Test
        @DisplayName("Check if red is fully removed")
        void testRemoveFullImage() throws InterruptedException {
            BufferedImage image = ImageReader.readImage("sample.png");
            assertNotNull(image, "Image should not be null");

            BufferedImage removeRed = ImageTransformer.removeRedFullImage(image,2,2);
            assertNotNull(removeRed, "Image should not be null");

            int width = removeRed.getWidth();
            int height = removeRed.getHeight();
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    int checkred = removeRed.getRGB(i, j);
                    int red = (checkred >> 16) & 0xFF; // Componente vermelha
                    assertEquals(0, red); // Verifica se o componente vermelho é zero
                }
            }
        }
 */


    }

    @Nested
    @DisplayName("LoadInfoReader")
    class LoadInfoReaderTest{
        private LoadInfoReader reader;
        private ReentrantLock loadInfoLock;
        @BeforeEach
        void setUp(){
            this.reader = new LoadInfoReader("load.info.txt",loadInfoLock);
        }

        @Test
        @DisplayName("Verify if the input is saved")
        void testLoadInfoReader(){
            assertAll(
                    () -> assertEquals("load.info.txt", reader.fileName),
                    () -> assertEquals(loadInfoLock, reader.lock)
            );
        }

        @Test
        @DisplayName("Testing if ports are saved")
        void testLoadRead(){
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("load.info.txt"))) {
                writer.write("8888: 30\n");
                writer.write("8000: 10\n");
                writer.write("8100: 20\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            ReentrantLock lock = new ReentrantLock();
            LoadInfoReader reader = new LoadInfoReader("load.info.txt", lock);


            assertEquals(8000, reader.LoadRead());

        }
    }

    @Nested
    @DisplayName("LoadInfoWriter")
    class testLoadInfoWriter{
        private LoadInfoWriter writer;
        private ReentrantLock loadInfoLock;


        @BeforeEach
        void setUp(){
            this.writer = new LoadInfoWriter("load.info.txt", loadInfoLock);
        }

        @Test
        @DisplayName("Verify if the LoadInfoWriter is saved")
        void testLoadInfoWriter(){
            assertAll(
                    () -> assertEquals(loadInfoLock, writer.lock),
                    () -> assertNotNull( writer.file)
            );
        }
/*
        @Test
        @DisplayName("Verify if add a load correctly")
        public void testAddLoad() throws IOException {
            ReentrantLock lock = new ReentrantLock();
            writer.AddLoad(8050);
            assertEquals("8050", writer.file);
        }



 */
    }


    @Nested
    @DisplayName("Request")
    class requestTest{

        private Request request;

        @BeforeEach
        void setUp(){
            BufferedImage image = ImageReader.readImage("sample.png");
            assertNotNull(image, "Image should not be null");

            this.request = new Request("text","New Message", image);
        }

        @Test
        @DisplayName(" Check if the request is saved")
        void testImage(){
            assertAll(
                    () ->assertNotNull(request.getImageSection()),
                    () ->assertNotNull(request.getImageSection()),
                    () ->assertTrue(request.getImageSection().length > 0),
                    () ->assertEquals("text",request.getMessageType()),
                    () ->assertEquals("New Message",request.getMessageContent())
            );

        }
    }

    @Nested
    @DisplayName("Response")
    class TestResponse{

        private Response response;

        @BeforeEach
        void setUp(){
            BufferedImage image = ImageReader.readImage("sample.png");
            assertNotNull(image, "Image should not be null");
            this.response = new Response("Success","Message sucess", image);
        }

        @Test
        @DisplayName("Check if the response is saved")
        void testResponse(){
            assertAll(
                    () ->assertNotNull(response.getImageSection()),
                    () ->assertTrue(response.getImageSection().length > 0),
                    () ->assertEquals("Success",response.getStatus()),
                    () ->assertEquals("Message sucess",response.getMessage())
            );
        }
    }
/*
    @Nested
    @DisplayName("SIMDExecutor")
    class SIMDExecutorTest {

        private SIMDExecutor simd;
        private LoadInfoWriter writer;
        private LoadInfoReader reader;

        private ReentrantLock lock;

        @BeforeEach
        void setUp() {
            BufferedImage image = ImageReader.readImage("sample.png");
            assertNotNull(image, "Image should not be null");

            this.reader = new LoadInfoReader("load.info.txt", lock);


            this.simd = new SIMDExecutor(image, 2, 2, 50, 50, 2,reader,writer);
            simd.execute();
        }

        @Test
        @DisplayName("Check if the SIMDExecutor is saved")
        void testgetImageWithoutRed() throws InterruptedException {

            BufferedImage finalImage = simd.getImageWithoutRed();
            assertAll(
                    () -> assertEquals(50, finalImage.getHeight()),
                    () -> assertEquals(50, finalImage.getWidth()),
                    () -> assertEquals(2, finalImage.getType())
            );
            assertNotNull(simd.getImageWithoutRed());

        }
    }

 */
    @Nested
    @DisplayName("TaskPool")
    class TaskPoolTest{
        private TaskPool pool;
        @BeforeEach
        void setUp(){
            this.pool = new TaskPool(5);
        }

        @AfterEach
        void shutDown() {
            pool.shutdown();
        }

        @Test
        @DisplayName("Verifiy the number of task is correct")
        void testSubmitTask() throws InterruptedException {
            // Create a shared resource to test task execution
            final int[] add = {0};
            Runnable task = () -> add[0]++;
            // Submit the task 5 times
            for (int i = 0; i < 5; i++) {
                pool.submitTask(task);
            }
            // Wait to all tasks have been processed
            Thread.sleep(1000);
            // Verify tasks
            assertEquals(5, add[0]);
        }

    }

    @Nested
    @DisplayName("ThreadRemoveRed")
    class testThreadRemoveRedTest{

        private ThreadRemoveRed removeRed;
        private LoadInfoReader reader;
        private LoadInfoWriter writer;

        @BeforeEach
        void setUp(){
            BufferedImage image = ImageReader.readImage("sample.png");
            assertNotNull(image);

            this.removeRed = new ThreadRemoveRed(image,2,2, reader, writer);
        }

        @Test
        @DisplayName("Check if the values are the same")
        void getResultingImage_ReturnsNull_WhenNotSet() {

            BufferedImage image = ImageReader.readImage("sample.png");
            assertNotNull(image);

            assertAll(
                    () -> assertEquals(2,removeRed.getI()),
                    () -> assertEquals(2,removeRed.getJ()),
                    () -> assertEquals(reader,removeRed.reader),
                    () -> assertEquals(writer,removeRed.writer),
                    () -> assertNull(removeRed.getResultingImage())
            );
        }
    }


}