package bot.ninetail.commands.general;

import static org.mockito.Mockito.*;

import java.io.IOException;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;

import bot.ninetail.clients.RandomFoxClient;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RandomFoxTest {

    private RandomFoxClient mockFoxClient;
    private SlashCommandInteractionEvent mockEvent;

    @BeforeEach
    void setUp() {
        mockFoxClient = mock(RandomFoxClient.class);
        mockEvent = mock(SlashCommandInteractionEvent.class);
    }

    @Test
    void testInvokeWithValidResponse() throws IOException, InterruptedException {
        JsonObject jsonObject = Json.createObjectBuilder().add("image", "http://example.com/fox.jpg").build();
        JsonArray jsonArray = Json.createArrayBuilder().add(jsonObject).build();

        when(mockFoxClient.getImage()).thenReturn(jsonArray);

        RandomFox.invoke(mockEvent);

        verify(mockEvent).reply("Here's a random fox for you: http://example.com/fox.jpg").queue();
    }

    @Test
    void testInvokeWithEmptyResponse() throws IOException, InterruptedException {
        JsonArray jsonArray = Json.createArrayBuilder().build();

        when(mockFoxClient.getImage()).thenReturn(jsonArray);

        RandomFox.invoke(mockEvent);

        verify(mockEvent).reply("Could not retrieve a fox image at this time.").queue();
    }

    @Test
    void testInvokeWithIOException() throws IOException, InterruptedException {
        when(mockFoxClient.getImage()).thenThrow(new IOException("Test exception"));

        RandomFox.invoke(mockEvent);

        verify(mockEvent).reply("An error occurred while fetching a fox image.").queue();
    }

    @Test
    void testInvokeWithInterruptedException() throws IOException, InterruptedException {
        when(mockFoxClient.getImage()).thenThrow(new InterruptedException("Test exception"));

        RandomFox.invoke(mockEvent);

        verify(mockEvent).reply("Interrupted while retrieving random fox image.").queue();
    }
}