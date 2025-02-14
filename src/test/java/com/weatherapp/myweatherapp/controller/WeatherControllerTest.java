package com.weatherapp.myweatherapp.controller;

import com.weatherapp.myweatherapp.model.CityInfo;
import com.weatherapp.myweatherapp.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WeatherController.class)
@ExtendWith(MockitoExtension.class)
public class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeatherService weatherService;

    @InjectMocks
    private WeatherController weatherController;

    @BeforeEach
    void setUp() {
        CityInfo chicago = createMockCity("06:30:00", "18:30:00", "Cloudy");
        CityInfo newYork = createMockCity("06:45:00", "19:00:00", "Rain");
        CityInfo portland = createMockCity("06:30:00", "18:30:00", "Rain");
        CityInfo miami = createMockCity("06:45:00", "19:00:00", "Sunny");

        when(weatherService.forecastByCity("Chicago")).thenReturn(chicago);
        when(weatherService.forecastByCity("NewYork")).thenReturn(newYork);
        when(weatherService.forecastByCity("Portland")).thenReturn(portland);
        when(weatherService.forecastByCity("Miami")).thenReturn(miami);
    }

    @Test
    public void testCompareDaylight() throws Exception {
        mockMvc.perform(get("/compareDaylight/Chicago/NewYork"))
                .andExpect(status().isOk())
                .andExpect(content().string("NewYork"));
    }

    @Test
    public void testCompareDaylight_SameCity() throws Exception {
        CityInfo chicago = createMockCity("06:30:00", "18:30:00", "Cloudy");

        when(weatherService.forecastByCity("Chicago")).thenReturn(chicago);

        mockMvc.perform(get("/compareDaylight/Chicago/Chicago"))
                .andExpect(status().isOk())
                .andExpect(content().string("both cities have same daylight"));
    }

    @Test
    public void testRainingCity_OneRaining() throws Exception {
        mockMvc.perform(get("/rainingCity/Portland/Miami"))
                .andExpect(status().isOk())
                .andExpect(content().string("Portland"));
    }

    @Test
    public void testRainingCity_BothRaining() throws Exception {
        CityInfo portland = createMockCity("06:30:00", "18:30:00", "Rain");
        CityInfo seattle = createMockCity("06:45:00", "19:00:00", "Heavy Rain");

        when(weatherService.forecastByCity("Portland")).thenReturn(portland);
        when(weatherService.forecastByCity("Seattle")).thenReturn(seattle);

        mockMvc.perform(get("/rainingCity/Portland/Seattle"))
                .andExpect(status().isOk())
                .andExpect(content().string("Portland,Seattleboth raining"));
    }

    @Test
    public void testRainingCity_NoneRaining() throws Exception {
        CityInfo losAngeles = createMockCity("06:30:00", "18:30:00", "Sunny");
        CityInfo phoenix = createMockCity("06:45:00", "19:00:00", "Clear");

        when(weatherService.forecastByCity("LosAngeles")).thenReturn(losAngeles);
        when(weatherService.forecastByCity("Phoenix")).thenReturn(phoenix);

        mockMvc.perform(get("/rainingCity/LosAngeles/Phoenix"))
                .andExpect(status().isOk())
                .andExpect(content().string("neither are raining"));
    }

    private CityInfo createMockCity(String sunrise, String sunset, String conditions) {
        CityInfo.CurrentConditions conditionsObj = new CityInfo.CurrentConditions(sunrise, sunset, conditions);
        return new CityInfo(conditionsObj);
    }
}
